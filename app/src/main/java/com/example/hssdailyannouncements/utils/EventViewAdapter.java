package com.example.hssdailyannouncements.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hssdailyannouncements.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

public class EventViewAdapter extends RecyclerView.Adapter<EventViewAdapter.ViewHolder> {

    private List<CalendarEvent> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    View view;

    // data is passed into the constructor
    public EventViewAdapter(Context context, List<CalendarEvent> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.event, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get the event at {position} in the list of events (this function will loop over every event in the list)
        CalendarEvent event = mData.get(position);

        if(event.startDate.isEqual(LocalDate.now())) {
            holder.container.setBackgroundColor(Color.parseColor("#C1C1C1"));
        }

        //set the value of views extracted from the view ViewHolder class below
        holder.title.setText(event.name);
        if (event.isAllDay) {
            holder.startTime.setText("All day");
            holder.endTime.setHeight(0);
            //holder.container.setBackgroundColor(Color.parseColor("#A5A5A5"));
        }
        else {
            //holder.container.setBackgroundColor(view.getContext().getColor(R.color.colorPrimaryDark));
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
            holder.startTime.setText(dtf.format(event.startTime));
            holder.endTime.setText(dtf.format(event.endTime));
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //the views we will extract from the event's xml
        ConstraintLayout container;
        TextView title;
        TextView startTime;
        TextView endTime;

        ViewHolder(View itemView) {
            //itemView is the xml for this specific event
            super(itemView);

            //find the views we need
            container = itemView.findViewById(R.id.container);
            title = itemView.findViewById(R.id.event_title);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);

            //set a click listener on the event view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    CalendarEvent getItem(int id) {
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