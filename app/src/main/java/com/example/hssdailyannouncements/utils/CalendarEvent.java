package com.example.hssdailyannouncements.utils;

import android.util.Log;

import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CalendarEvent {
    public LocalDate startDate;
    public LocalTime startTime;
    public LocalDate endDate;
    public LocalTime endTime;
    public String name;
    public Boolean isAllDay;

    public CalendarEvent(JsonObject json) {
        if (json.get("summary") != null) {
            this.name = json.get("summary").getAsString();

            JsonObject end = json.get("end").getAsJsonObject();
            JsonObject start = json.get("start").getAsJsonObject();

            try {
                this.startTime = LocalTime.parse(start.get("dateTime").getAsString().split("T")[1].split("-")[0], DateTimeFormatter.ISO_LOCAL_TIME);
                this.startDate = LocalDate.parse(start.get("dateTime").getAsString().split("T")[0], DateTimeFormatter.ISO_LOCAL_DATE);
                this.endTime = LocalTime.parse(end.get("dateTime").getAsString().split("T")[1].split("-")[0], DateTimeFormatter.ISO_LOCAL_TIME);
                this.endDate = LocalDate.parse(end.get("dateTime").getAsString().split("T")[0], DateTimeFormatter.ISO_LOCAL_DATE);
                this.isAllDay = false;
            } catch (Exception e) {
                this.startDate = LocalDate.parse(start.get("date").getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                this.endDate = LocalDate.parse(end.get("date").getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                this.isAllDay = true;
            }
        }
    }
}
