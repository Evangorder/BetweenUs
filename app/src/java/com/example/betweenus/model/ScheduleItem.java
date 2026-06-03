package com.example.betweenus.model;

/**
 * PURPOSE:
 * Represents a single item in a user's daily schedule.
 *
 * WHAT THIS MODEL STORES:
 * - Time (ex: 8:00 AM)
 * - Activity title (ex: Morning Workout)
 */
public class ScheduleItem {
    private String time;
    private String title;

    public ScheduleItem(String time, String title) {
        this.time = time;
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
