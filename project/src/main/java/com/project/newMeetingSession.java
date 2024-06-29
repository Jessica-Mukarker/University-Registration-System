package com.project;

import java.time.LocalTime;

public class newMeetingSession {
    private LocalTime startTime;
    private LocalTime endTime;
    private String day; 
    private String room; 

    public newMeetingSession(String day,LocalTime startTime, LocalTime endTime,  String room) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.room = room;
    }

    //check if this session onerlap with the other session
    public boolean hasOverlap(newMeetingSession other) {
        return this.day.equals(other.day) &&
               this.startTime.isBefore(other.endTime) &&
               this.endTime.isAfter(other.startTime);
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
