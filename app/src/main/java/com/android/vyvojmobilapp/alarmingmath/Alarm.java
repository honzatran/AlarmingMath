package com.android.vyvojmobilapp.alarmingmath;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by honza on 12/15/14.
 */

//parcelable = lze připojit k intentu jako "extras"
public class Alarm implements Parcelable {
    //definice konstant pro metody buzeni
    public static final int NO_TASK = 0;
    public static final int MATH = 1;
    public static final int QR_CODE = 2;

    public static final String ALARM_FLAG = "alarm";
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

    int hour;
    int minute;
    String ringtoneUri;
    int snoozeDelay;
    int lengthOfRinging;
    int methodId;
    int volume;
    int difficulty;
    long id;
    boolean active;
    boolean vibrate;
    String name;


    public Alarm(int hour, int minute, String ringtoneUri, int snoozeDelay, int lengthOfRinging, int methodId, int difficulty, int volume, boolean active, boolean vibrate, String name) {
        this.hour = hour;
        this.minute = minute;
        this.ringtoneUri = ringtoneUri;
        this.snoozeDelay =snoozeDelay;
        this.lengthOfRinging = lengthOfRinging;
        this.methodId = methodId;
        this.difficulty = difficulty;
        this.volume = volume;
        this.active = active;
        this.vibrate = vibrate;
        this.name = name;
    }
    public Alarm(int hour, int minute, long id, String ringtoneUri, int snoozeDelay, int lengthOfRinging, int methodId, int difficulty, int volume, boolean active, boolean vibrate, String name) {
        this.hour = hour;
        this.minute = minute;
        this.id = id;
        this.ringtoneUri = ringtoneUri;
        this.snoozeDelay =snoozeDelay;
        this.lengthOfRinging = lengthOfRinging;
        this.methodId = methodId;
        this.difficulty = difficulty;
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

    public int getMinute() {
        return minute;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getRingtoneUri() {return ringtoneUri;}
    public int getSnoozeDelay() {
        return snoozeDelay;
    }
    public int getLengthOfRinging() {return lengthOfRinging;}
    public int getMethodId() { return methodId;}
    public int getDifficulty() { return difficulty;}
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



    //nasleduji metody impementující rozhraní Parcelable - vygenerovano pomoci http://www.parcelabler.com
    protected Alarm(Parcel in) {
        hour = in.readInt();
        minute = in.readInt();
        ringtoneUri = in.readString();
        snoozeDelay = in.readInt();
        lengthOfRinging = in.readInt();
        methodId = in.readInt();
        difficulty= in.readInt();
        volume = in.readInt();
        id = in.readLong();
        active = in.readByte() != 0x00;
        vibrate = in.readByte() != 0x00;
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeString(ringtoneUri);
        dest.writeInt(snoozeDelay);
        dest.writeInt(lengthOfRinging);
        dest.writeInt(methodId);
        dest.writeInt(difficulty);
        dest.writeInt(volume);
        dest.writeLong(id);
        dest.writeByte((byte) (active ? 0x01 : 0x00));
        dest.writeByte((byte) (vibrate ? 0x01 : 0x00));
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

}