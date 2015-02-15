package com.android.vyvojmobilapp.alarmingmath;

import android.provider.BaseColumns;

/**
 * Pro databazi budiku
 */
public class AlarmDtbColumns implements BaseColumns{
    public static String name = "AlarmTable";
    public static String column_hour = "hour";
    public static String column_minute = "minute";
    public static String column_ringtoneUri = "ringtoneUri";
    public static String column_snoozeDelay = "snoozeDelay";
    public static String column_lengthOfRinging = "lengthOfRinging";
    public static String column_methodId = "methodId";
    public static String column_difficulty = "difficulty";
    public static String column_volume = "volume";
    public static String column_active = "active";
    public static String column_vibrate = "vibrate";
    public static String column_name = "name";
    public static String column_days_mask = "days_mask";
    public static String column_alarm_type = "alarm_type";
    public static String column_qr_hint = "qr_hint";
    public static String column_qr_code = "qr_code";
}
