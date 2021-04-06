package com.example.hssdailyannouncements.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hssdailyannouncements.MainActivity;
import com.example.hssdailyannouncements.R;
import com.example.hssdailyannouncements.utils.CalendarDay;
import com.example.hssdailyannouncements.utils.CalendarEvent;
import com.example.hssdailyannouncements.utils.DayViewAdapter;
import com.example.hssdailyannouncements.utils.DownloadAnnouncements;
import com.example.hssdailyannouncements.utils.EventViewAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalendarFragment extends Fragment implements DayViewAdapter.ItemClickListener, EventViewAdapter.ItemClickListener{

    //String start_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(System.currentTimeMillis() + 60 * 86400000)); //date 30 days ago
    String start_date = LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); //date 30 days ago
    String APIKEY = "AIzaSyCEw_j9_X5gGoe91x4Q0LUKyLT7JRCRdC4";
    String REQUESTURL = "https://www.googleapis.com/calendar/v3/calendars/howesoundsecondaryschool@gmail.com/events?key=" + APIKEY + "&timeMin=" + start_date + "T00:00:00-00:00&orderBy=startTime&singleEvents=true";
    File calendarFile;
    SwipeRefreshLayout swipeRefreshLayout;

    DayViewAdapter dayViewAdapter;
    View view;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarFile = new File(getContext().getCacheDir(), "calendar.json");

        //re-download and show latest calendar on refresh
        swipeRefreshLayout = view.findViewById(R.id.calendar_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //download the latest calendar
                downloadCalendar();
            }
        });

        //if we already have the calendar, parse the contents
        if (calendarFile.exists()) {
            //parse response into a list of day objects
            List<CalendarDay> days = parseCalendar();
            showSchedule(days);
        }
        //else, download the calendar from the internet
        else {
            downloadCalendar();
        }

        //return the inflated view
        return view;
    }

    //shows every day in the list in a recycler view
    //days are passed to {DayViewAdapter}
    void showSchedule(List<CalendarDay> days) {
        //if the first item in the days list is today
        if (days.get(0).date.isEqual(LocalDate.now())) {
            CalendarDay today = days.get(0);
            days.remove(0);

            TextView todayDate = view.findViewById(R.id.today_date);
            todayDate.setText(today.date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + today.date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + today.date.getDayOfMonth());

            TextView todayBlockRotation = view.findViewById(R.id.today_block_rotation);
            todayBlockRotation.setText(today.blockRotation);

            RecyclerView recyclerView = view.findViewById(R.id.today_events_container);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            EventViewAdapter eventViewAdapter = new EventViewAdapter(getContext(), today.events);
            eventViewAdapter.setClickListener(this);
            recyclerView.setAdapter(eventViewAdapter);
        }

        //start the recycler view
        RecyclerView recyclerView = view.findViewById(R.id.schedule_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dayViewAdapter = new DayViewAdapter(getContext(), days);
        dayViewAdapter.setClickListener(this);
        recyclerView.setAdapter(dayViewAdapter);

        swipeRefreshLayout.setRefreshing(false);
    }

    //download calendar from the internet using the google api
    void downloadCalendar() {
        //make http request to the google calendar api
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(REQUESTURL).build();
        client.newCall(request).enqueue(new Callback() {

            //save the response to a file
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //get the 'items' element
                JsonElement items = new JsonParser().parse(response.body().string()).getAsJsonObject().get("items");

                //save to file
                FileWriter fileWriter = new FileWriter(calendarFile);
                BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
                bufferWriter.write(items.toString());
                bufferWriter.close();

                //parse response into a list of day objects
                final List<CalendarDay> days = parseCalendar();

                new Handler(getContext().getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        showSchedule(days);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
        });
    }

    //parse the calendar json file into a list of day objects
    List<CalendarDay> parseCalendar() {
        List<CalendarDay> days = new ArrayList();

        try {
            //read the json file
            JsonArray events = new JsonParser().parse(new FileReader(calendarFile)).getAsJsonArray();

            CalendarEvent previousEvent = null;
            String blockRotation = null;
            CalendarDay day = null;

            for (JsonElement item: events) {
                try {
                    //create new day object from item
                    CalendarEvent event = new CalendarEvent(item.getAsJsonObject());

                    //change the block rotationif the event if it contains the word rotation
                    if (event.name.contains("Rotation")) {
                        blockRotation = event.name;
                    }
                    else {
                        //skip the event if it has no name
                        if (event.name != null) {
                            //set previous event and day during the first loop
                            if (previousEvent == null) {
                                previousEvent = event;
                                day = new CalendarDay(event.startDate);
                                day.events.add(event);
                            }
                            else {
                                if (event.startDate.isAfter(previousEvent.startDate)) {
                                    if (day.date.isAfter(LocalDate.now()) || day.date.isEqual(LocalDate.now())) {
                                        if (day.date.isEqual(LocalDate.now())) {
                                            day.isToday = true;
                                        }
                                        days.add(day);
                                    }
                                    day = new CalendarDay(event.startDate);
                                    day.blockRotation = blockRotation;
                                }
                                day.events.add(event);
                                previousEvent = event;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    //if we fail, skip the event
                    e.printStackTrace();
                    continue;
                }
                
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            days = null;
        }

        return days;
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("debug", String.valueOf(view.getTag()));
    }
}