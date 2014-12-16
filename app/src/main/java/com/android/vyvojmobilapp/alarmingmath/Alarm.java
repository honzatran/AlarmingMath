package com.android.vyvojmobilapp.alarmingmath;

import android.net.Uri;

/**
 * Created by honza on 12/15/14.
 */
public class Alarm {
    int hour, minute;

    public static String HOUR = "hour";
    public static String MINUTES = "minutes";

    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return hour + ":" + minute;
    }
}
