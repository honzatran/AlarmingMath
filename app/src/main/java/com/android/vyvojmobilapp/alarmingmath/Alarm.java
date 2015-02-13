package com.android.vyvojmobilapp.alarmingmath;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by honza on 12/15/14.
 */

//parcelable = lze připojit k intentu jako "extras"
public class Alarm implements Parcelable, Cloneable {
    //definice konstant pro metody buzeni
    public static final int MATH = 1;
    public static final int QR_CODE = 2;

    public static final String ALARM_FLAG = "alarm";

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
    QR qr;
    // rika jestli je budik repeating, tj. furt se opakuje
    // nebo jestli je oneShot zazvoni jednou a pak se deaktivuje
    // nebo jestli je snooze zazvoni jednou a smaze se z databaze
    AlarmType alarmType;
    DayRecorder days;

    /**
     * constructor for creating alarm from AlarmActivity
     * @param hour
     * @param minute
     * @param ringtoneUri
     * @param snoozeDelay
     * @param lengthOfRinging
     * @param methodId
     * @param difficulty
     * @param volume
     * @param active
     * @param vibrate
     * @param name
     * @param days
     */
    public Alarm(
            int hour, int minute, String ringtoneUri,
            int snoozeDelay, int lengthOfRinging, int methodId,
            int difficulty, int volume, boolean active,
            boolean vibrate, String name, DayRecorder days, AlarmType alarmType, QR qr) {

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
        this.days = days;
        this.alarmType = alarmType;
        this.qr = qr;
    }


    /**
     * constructor for creating alarm from database
     * @param hour
     * @param minute
     * @param id
     * @param ringtoneUri
     * @param snoozeDelay
     * @param lengthOfRinging
     * @param methodId
     * @param difficulty
     * @param volume
     * @param active
     * @param vibrate
     * @param name
     * @param days
     */
    public Alarm(
            int hour, int minute, long id,
            String ringtoneUri, int snoozeDelay, int lengthOfRinging,
            int methodId, int difficulty, int volume,
            boolean active, boolean vibrate, String name,
            DayRecorder days, AlarmType alarmType, QR qr) {

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
        this.days = days;
        this.alarmType = alarmType;
        this.qr = qr;
    }


    // honza: gettery settery snad jasny
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
    public DayRecorder getDays() {
        return days;
    }
    public String getName() {
        return name;
    }
    public AlarmType getAlarmType() {
        return alarmType;
    }
    public QR getQr(){
        return qr;
    }

    @Override
    public String toString() {
        //korektni zobrazeni napr. 15:07 misto 15:7
        String minuteStr = (minute < 10) ? "0" + minute : "" + minute;
        return hour + ":" + minuteStr;
    }


    @Override
    public Alarm clone() throws CloneNotSupportedException {
        return (Alarm) super.clone();
    }

    /**
     * Returns snoozing version of alarm, which is 5 minutes later then alarm
     *
     * @author Tran Tuan Hiep
     * @param currDay current day in week
     * @return snoozing version of alarm, which is delayed 5
     * @throws CloneNotSupportedException
     */
    public Alarm getSnoozingVersion(int currDay) throws CloneNotSupportedException {
        // honza: naclonujeme si budik at nemusime kopirovat vsechno rucne
        Alarm snoozeAlarm = this.clone();
        snoozeAlarm.minute += snoozeAlarm.snoozeDelay;
        snoozeAlarm.days = new DayRecorder();
        snoozeAlarm.days.setDay(true, currDay);

        if (snoozeAlarm.minute > 60) {
            snoozeAlarm.hour += 1;
            snoozeAlarm.minute %= 60;

            if (snoozeAlarm.hour > 23) {
                snoozeAlarm.hour %= 24;
                snoozeAlarm.days.setDay(false, currDay);
                snoozeAlarm.days.setDay(true, (currDay + 1) % 7);
            }
        }

        snoozeAlarm.id = 0;
        snoozeAlarm.alarmType = AlarmType.SNOOZE;

        return snoozeAlarm;
    }


    // nasleduji metody impementující rozhraní Parcelable - vygenerovano pomoci http://www.parcelabler.com
    protected Alarm(Parcel in) {
        hour = in.readInt();
        minute = in.readInt();
        ringtoneUri = in.readString();
        snoozeDelay = in.readInt();
        lengthOfRinging = in.readInt();
        methodId = in.readInt();
        volume = in.readInt();
        difficulty = in.readInt();
        id = in.readLong();
        active = in.readByte() != 0x00;
        vibrate = in.readByte() != 0x00;
        name = in.readString();
        days = (DayRecorder) in.readValue(DayRecorder.class.getClassLoader());
        alarmType = AlarmType.getEnum(in.readInt());
        qr = new QR(in.readString(), in.readString());
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
        dest.writeInt(volume);
        dest.writeInt(difficulty);
        dest.writeLong(id);
        dest.writeByte((byte) (active ? 0x01 : 0x00));
        dest.writeByte((byte) (vibrate ? 0x01 : 0x00));
        dest.writeString(name);
        dest.writeValue(days);
        dest.writeInt(alarmType.convert());
        dest.writeString(qr.getHint());
        dest.writeString(qr.getCode());
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

    public boolean isSnoozingAlarm() {
        return alarmType == AlarmType.SNOOZE;
    }


    /**
     * create Parcel from alarm
     * @return Parcel containing current instance of alarm
     */
    public Parcel createParcel() {
        Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        return parcel;
    }

    /**
     * extract an alarm from intent
     * @param intent i
     * @return alarm saved on intent.
     */
    public static Alarm extractAlarmFromIntent(Intent intent) {
        byte[] arr = intent.getByteArrayExtra(Alarm.ALARM_FLAG);
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(arr, 0, arr.length);
        parcel.setDataPosition(0);
        return Alarm.CREATOR.createFromParcel(parcel);
    }

}