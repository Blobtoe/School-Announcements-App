package com.felixperron.hssapp.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.felixperron.hssapp.R;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class DayViewAdapter extends RecyclerView.Adapter<DayViewAdapter.ViewHolder> implements EventViewAdapter.ItemClickListener{

    View view;

    EventViewAdapter eventViewAdapter;

    private List<CalendarDay> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public DayViewAdapter(Context context, List<CalendarDay> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //save inflated view as global variable so we use its context later
        view = mInflater.inflate(R.layout.day, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull DayViewAdapter.ViewHolder holder, int position) {
        //get the day at {position} in the days list (this function will iterate over every day in the list)
        CalendarDay day =  mData.get(position);

        //set the value of views extracted from the view ViewHolder class below
        holder.date.setText(day.date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + day.date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + day.date.getDayOfMonth());
        holder.blockRotation.setText(day.blockRotation);

        //this start another recycler view that deals with every event in the day
        holder.events_container.setLayoutManager(new LinearLayoutManager(view.getContext()));
        eventViewAdapter = new EventViewAdapter(view.getContext(), day.events);
        eventViewAdapter.setClickListener(this);
        holder.events_container.setAdapter(eventViewAdapter);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("debug", "You clicked " + eventViewAdapter.getItem(position) + " on row number " + position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //the views we will extract from the day xml
        TextView date;
        RecyclerView events_container;
        TextView blockRotation;

        public ViewHolder(@NonNull View itemView) {
            //itemView is the xml for this specific day
            super(itemView);

            //find the views we need
            date = itemView.findViewById(R.id.date);
            events_container = itemView.findViewById(R.id.events_container);
            blockRotation = itemView.findViewById(R.id.day_rotation);

            //set a click listener on the whole day view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public CalendarDay getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
