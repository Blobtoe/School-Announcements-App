package com.example.hssdailyannouncements.utils;

import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarDay {
    public List<CalendarEvent> events;
    public LocalDate date;
    public Boolean isToday;
    public String blockRotation;

    public CalendarDay(LocalDate date) {
        this.events = new ArrayList();
        this.date = date;

        if (date.equals(LocalDate.now())) {
            this.isToday = true;
        }
    }
}
