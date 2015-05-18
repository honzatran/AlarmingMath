package com.vyvojmobilapp.alarmingmath.alarm;

import android.content.Intent;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.vyvojmobilapp.alarmingmath.response.qr.QR;

import java.util.List;

/**
 * budik
 */

//parcelable = lze připojit k intentu jako "extras"
public class Alarm extends SugarRecord<Alarm> implements Parcelable, Cloneable  {
    //definice konstant pro metody buzeni
    public static final int NO_TASK = 0;
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
    DayRecorder days;

    String qr_code;
    String qr_hint;
    byte mask;

    // rika jestli je budik repeating, tj. furt se opakuje
    // nebo jestli je oneShot zazvoni jednou a pak se deaktivuje
    // nebo jestli je snooze zazvoni jednou a smaze se z databaze
    AlarmType alarmType;

    boolean oneShot;

    public Alarm() {
    }

    /**
     * constructor pro vytvoreni z AlarmActivity
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

        this.mask = days.getMask();
        this.qr_hint = qr.getHint();
        this.qr_code = qr.getCode();
        //this.save();
    }

    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
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

    public void setActive(boolean active) {
        this.active = active;
    }

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
     * Vytvoří snoozing verzi budíku, ktera je odlozena o Alarm.snoozeDelay minut
     *
     * @param currDay soucasny den v tydnu
     * @return snoozing verze budíku
     * @throws CloneNotSupportedException
     */
    public Alarm getSnoozingVersion(int currDay) throws CloneNotSupportedException {
        // naclonujeme si budik at nemusime kopirovat vsechno rucne
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
     * vytvori parcel z alarmu
     * @return Parcel obsahujici aktualni instanci alarmu
     */
    public Parcel createParcel() {
        Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        return parcel;
    }

    /**
     * extrahuje alarm z intentu
     * @param intent i
     * @return alarm ulozeny na intent
     */
    public static Alarm extractAlarmFromIntent(Intent intent) {
        byte[] arr = intent.getByteArrayExtra(Alarm.ALARM_FLAG);
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(arr, 0, arr.length);
        parcel.setDataPosition(0);
        return Alarm.CREATOR.createFromParcel(parcel);
    }

    /**
     * Smaze budik s danym id z databaze.
     * @param id id budiku, ktery bude smazan
     * @return
     */
    public static void deleteAlarm(long id) {
        Alarm alarm = Alarm.findById(Alarm.class, id);
        alarm.delete();
    }

    /**
     * Aktivuje ci deaktivuje budik s danym id v databazi.
     * @param active
     * @param id
     */
    public static void setAlarmActive(boolean active, long id) {
        Alarm alarm = Alarm.findById(Alarm.class, id);
        alarm.active = active;
    }

    /**
     * Smaze vsechny ulozene budiky.
     */
    public static void deleteAll() {
        Alarm.deleteAll(Alarm.class);
    }

    /**
     * Vrati seznam vsech budiku v databazi.
     * @return Seznam budiku v databazi.
     */
    public static List<Alarm> getAlarms() {
        //return Alarm.listAll(Alarm.class);
        List<Alarm> list = listAll(Alarm.class);
        for (Alarm a : list) {
            a.qr = new QR(a.qr_hint,a.qr_code);
            a.days = new DayRecorder(a.mask);
        }
        return list;
    }

    public static /*long*/ void addAlarm(Alarm alarm) {
        alarm.save();
        //return alarm.getId();
    }
}