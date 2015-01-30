package com.android.vyvojmobilapp.alarmingmath;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honza on 12/16/14.
 */
public class AlarmDatabase extends SQLiteOpenHelper {

    public static final int version = 1;
    public static final String dbName = "alarmDatabase.db";

    private static String TAG = AlarmDatabase.class.getName();

    private static final String CREATE_ALARM_TABLE =
            "CREATE TABLE " + AlarmDtbColumns.name + " (" +
                    AlarmDtbColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AlarmDtbColumns.column_hour + " INTEGER, " +
                    AlarmDtbColumns.column_minute + " INTEGER, " +
                    AlarmDtbColumns.column_ringtoneUri + " TEXT, " +
                    AlarmDtbColumns.column_snoozeDelay + " INTEGER, " +
                    AlarmDtbColumns.column_lengthOfRinging + " INTEGER, " +
                    AlarmDtbColumns.column_methodId + " INTEGER, " +
                    AlarmDtbColumns.column_difficulty + " INTEGER, " +
                    AlarmDtbColumns.column_volume + " INTEGER, " +
                    AlarmDtbColumns.column_active + " BOOLEAN, " +
                    AlarmDtbColumns.column_vibrate + " BOOLEAN, " +
                    AlarmDtbColumns.column_name + " TEXT, " +
                    AlarmDtbColumns.column_days_mask + " INTEGER, " +
                    AlarmDtbColumns.column_alarm_type + " INTEGER, " +
                    AlarmDtbColumns.column_qr_hint + " TEXT, " +
                    AlarmDtbColumns.column_qr_code + " TEXT)";

    public static final String DELETE_ALARM_TABLE =
            "DROP TABLE IF EXISTS " + AlarmDtbColumns.name;

    public AlarmDatabase(Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_ALARM_TABLE);
        onCreate(db);
    }

    private ContentValues convertToContentValue(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(AlarmDtbColumns.column_hour, alarm.getHour());
        values.put(AlarmDtbColumns.column_minute, alarm.getMinute());
        values.put(AlarmDtbColumns.column_ringtoneUri, alarm.getRingtoneUri());
        values.put(AlarmDtbColumns.column_snoozeDelay, alarm.getSnoozeDelay());
        values.put(AlarmDtbColumns.column_lengthOfRinging, alarm.getLengthOfRinging());
        values.put(AlarmDtbColumns.column_methodId, alarm.getMethodId());
        values.put(AlarmDtbColumns.column_difficulty, alarm.getDifficulty());
        values.put(AlarmDtbColumns.column_volume, alarm.getVolume());
        values.put(AlarmDtbColumns.column_active, alarm.isActive());
        values.put(AlarmDtbColumns.column_vibrate, alarm.isVibrate());
        values.put(AlarmDtbColumns.column_name, alarm.getName());
        values.put(AlarmDtbColumns.column_days_mask, alarm.getDays().getMask());
        values.put(AlarmDtbColumns.column_alarm_type, alarm.getAlarmType().convert());
        values.put(AlarmDtbColumns.column_qr_hint, alarm.getQr().getHint());
        values.put(AlarmDtbColumns.column_qr_code, alarm.getQr().getCode());

        return values;
    }


    public long addAlarm(Alarm alarm) {
        return getWritableDatabase().insert(AlarmDtbColumns.name,
                null, convertToContentValue(alarm));
    }

    public int deleteAlarm(long id) {
        // neozkouseny
        return getWritableDatabase().delete(
                AlarmDtbColumns.name,
                AlarmDtbColumns._ID + "= ?",
                new String[] {
                      String.valueOf(id)
                }
        );
    }

    public void setAlarmActive(boolean active, long id) {
        SQLiteDatabase dtb = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AlarmDtbColumns.column_active, active);

        int i = dtb.update(AlarmDtbColumns.name, values, AlarmDtbColumns._ID + "=" + id, null);
        Log.v(TAG, "updates count = " + i);
    }


    public void deleteAll() {
        getWritableDatabase().execSQL(
                "delete from " + AlarmDtbColumns.name
        );
    }

    public Alarm getAlarm(long id) {
        // neozkouseny
        SQLiteDatabase dtb = getReadableDatabase();

        String SQL_SELECT_SPECIFIC = "SELECT * FROM " + AlarmDtbColumns.name +
                " WHERE " + AlarmDtbColumns._ID + " = " + id;

        Cursor c = dtb.rawQuery(SQL_SELECT_SPECIFIC, null);

        if (c.moveToNext()) {
            return convertToAlarm(c);
        }

        return null;
    }

    public List<Alarm> getAlarms() {
        SQLiteDatabase dtb = this.getReadableDatabase();
        String sqlSelect = "SELECT * FROM " + AlarmDtbColumns.name;
        Cursor c = dtb.rawQuery(sqlSelect, null);
        try {
            List<Alarm> alarms = new ArrayList<>();

            while (c.moveToNext()) {
                alarms.add(convertToAlarm(c));
            }

            if (!alarms.isEmpty()) {
                return alarms;
            }

            return null;
        } finally {
            c.close();
            dtb.close();
        }
    }

    private Alarm convertToAlarm(Cursor c) {
        int hour = c.getInt(c.getColumnIndex(AlarmDtbColumns.column_hour));
        int minutes = c.getInt(c.getColumnIndex(AlarmDtbColumns.column_minute));
        String ringtoneUri = c.getString(c.getColumnIndex(AlarmDtbColumns.column_ringtoneUri));
        int snoozeDelay = c.getInt(c.getColumnIndex(AlarmDtbColumns.column_snoozeDelay));
        int lengthOfRinging = c.getInt(c.getColumnIndex(AlarmDtbColumns.column_lengthOfRinging));
        int methodId = c.getInt(c.getColumnIndex(AlarmDtbColumns.column_methodId));
        int difficulty = c.getInt(c.getColumnIndex(AlarmDtbColumns.column_difficulty));
        int volume = c.getInt(c.getColumnIndex(AlarmDtbColumns.column_volume));
        boolean active = (c.getInt(c.getColumnIndex(AlarmDtbColumns.column_active)) != 0);
        boolean vibrate = (c.getInt(c.getColumnIndex(AlarmDtbColumns.column_vibrate)) != 0);
        String name = c.getString(c.getColumnIndex(AlarmDtbColumns.column_name));
        byte b = (byte) c.getInt(c.getColumnIndex(AlarmDtbColumns.column_days_mask));

        int alarmTypeId = c.getInt(c.getColumnIndex(AlarmDtbColumns.column_alarm_type));
        AlarmType alarmType = AlarmType.getEnum(alarmTypeId);
        String qr_hint = c.getString(c.getColumnIndex(AlarmDtbColumns.column_qr_hint));
        String qr_code = c.getString(c.getColumnIndex(AlarmDtbColumns.column_qr_code));

        long id = c.getLong(c.getColumnIndex(AlarmDtbColumns._ID));
        return new Alarm(hour, minutes, id,
                ringtoneUri, snoozeDelay,
                lengthOfRinging, methodId,
                difficulty, volume, active,
                vibrate, name, new DayRecorder(b), alarmType, new QR(qr_hint,qr_code));
    }
}
