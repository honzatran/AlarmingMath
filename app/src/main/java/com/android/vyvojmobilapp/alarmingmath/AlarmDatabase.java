package com.android.vyvojmobilapp.alarmingmath;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honza on 12/16/14.
 */
public class AlarmDatabase extends SQLiteOpenHelper {

    public static final int version = 1;
    public static final String dbName = "alarmDatabase.db";

    private static final String CREATE_ALARM_TABLE =
            "CREATE TABLE " + AlarmDtbColumns.name + " (" +
                    AlarmDtbColumns._ID+ " INTEGER, " +
                    AlarmDtbColumns.column_hour + " INTEGER, " +
                    AlarmDtbColumns.columnt_minute + " INTEGER)";

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
        values.put(AlarmDtbColumns.columnt_minute, alarm.getMinute());

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
        Cursor c = dtb.rawQuery(sqlSelect,null);
        List<Alarm> alarms = new ArrayList<>();

        while(c.moveToNext()) {
            alarms.add(convertToAlarm(c));
        }

        if (!alarms.isEmpty()) {
            return alarms;
        }

        return null;
    }

    private Alarm convertToAlarm(Cursor c) {
        int hour = c.getInt(c.getColumnIndex(AlarmDtbColumns.column_hour));
        int minutes = c.getInt(c.getColumnIndex(AlarmDtbColumns.columnt_minute));
        long id = c.getLong(c.getColumnIndex(AlarmDtbColumns._ID));
        return new Alarm(hour, minutes, id);
    }
}
