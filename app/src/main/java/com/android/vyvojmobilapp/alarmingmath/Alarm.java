package com.android.vyvojmobilapp.alarmingmath;

import android.net.Uri;

/**
 * Created by honza on 12/15/14.
 */
public class Alarm {
    //definice konstant pro metody buzeni
    public static final int NO_TASK = 0;
    public static final int MATH = 1;
    public static final int QR_CODE = 2;

    public static final String HOUR = "hour";
    public static final String MINUTES = "minutes";
    public static final String RINGTONE = "ringtone";
    public static final String SNOOZE_DELAY = "snooze";
    public static final String LENGTH_OF_RINGING = "length";
    public static final String METHOD_ID = "method";
    public static final String VOLUME = "volume";
    public static final String IS_ACTIVE = "active";
    public static final String IS_VIBRATE = "vibrate";
    public static final String NAME = "name";

    int hour, minute, ringtoneId, snoozeDelay, lengthOfRinging, methodId, volume;
    long id;
    boolean active, vibrate;
    String name;


    public Alarm(int hour, int minute, int ringtoneId, int snoozeDelay, int lengthOfRinging, int methodId, int volume, boolean active, boolean vibrate, String name) {
        this.hour = hour;
        this.minute = minute;
        this.ringtoneId = ringtoneId;
        this.snoozeDelay =snoozeDelay;
        this.lengthOfRinging = lengthOfRinging;
        this.methodId = methodId;
        this.volume = volume;
        this.active = active;
        this.vibrate = vibrate;
        this.name = name;
    }
    public Alarm(int hour, int minute, long id, int ringtoneId, int snoozeDelay, int lengthOfRinging, int methodId, int volume, boolean active, boolean vibrate, String name) {
        this.hour = hour;
        this.minute = minute;
        this.id = id;
        this.ringtoneId = ringtoneId;
        this.snoozeDelay =snoozeDelay;
        this.lengthOfRinging = lengthOfRinging;
        this.methodId = methodId;
        this.volume = volume;
        this.active = active;
        this.vibrate = vibrate;
        this.name = name;
    }

    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Alarm(int hour, int minute, long id) {
        this.hour = hour;
        this.minute = minute;
        this.id = id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public int getRingtoneId() {return ringtoneId;}
    public int getSnoozeDelay() {
        return snoozeDelay;
    }
    public int getLengthOfRinging() {return lengthOfRinging;}
    public int getMethodId() { return methodId;}
    public int getVolume() {
        return volume;
    }
    public boolean isActive() {
        return active;
    }
    public boolean isVibrate() { return vibrate; }
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        //korektni zobrazeni napr. 15:07 misto 15:7
        String minuteStr = (minute < 10) ? "0" + minute : "" + minute;
        return hour + ":" + minuteStr;
    }
}
