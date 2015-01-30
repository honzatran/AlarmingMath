package com.android.vyvojmobilapp.alarmingmath;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class QrDatabase extends SQLiteOpenHelper {

    public static final int version = 1;
    public static final String dbName = "qrDatabase.db";

    private static final String CREATE_ALARM_TABLE =
            "CREATE TABLE " + QrDtbColumns.name + " (" +
                    QrDtbColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    QrDtbColumns.column_hint + " TEXT, " +
                    QrDtbColumns.column_code + " TEXT)";

    public static final String DELETE_ALARM_TABLE =
            "DROP TABLE IF EXISTS " + QrDtbColumns.name;

    public QrDatabase(Context context) {
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

    private ContentValues convertToContentValue(QR qr) {
        ContentValues values = new ContentValues();

        values.put(QrDtbColumns.column_hint, qr.getHint());
        values.put(QrDtbColumns.column_code, qr.getCode());
        return values;
    }


    public long addQr(QR qr) {
        return getWritableDatabase().insert(QrDtbColumns.name,
                null, convertToContentValue(qr));
    }

    public void deleteAll() {
        getWritableDatabase().execSQL(
                "delete from " + QrDtbColumns.name
        );
    }

    public QR getQR(long id) {
        // neozkouseny
        SQLiteDatabase dtb = getReadableDatabase();

        String SQL_SELECT_SPECIFIC = "SELECT * FROM " + QrDtbColumns.name +
                " WHERE " + QrDtbColumns._ID + " = " + id;

        Cursor c = dtb.rawQuery(SQL_SELECT_SPECIFIC, null);

        if (c.moveToNext()) {
            return convertToQR(c);
        }

        return null;
    }


    public List<QR> getQRs() {
        SQLiteDatabase dtb = this.getReadableDatabase();
        String sqlSelect = "SELECT * FROM " + QrDtbColumns.name;
        Cursor c = dtb.rawQuery(sqlSelect, null);
        try {
            List<QR> qrs = new ArrayList<>();

            while (c.moveToNext()) {
                qrs.add(convertToQR(c));
            }

            if (!qrs.isEmpty()) {
                return qrs;
            }

            return qrs;
        } finally {
            c.close();
            dtb.close();
        }
    }

    private QR convertToQR(Cursor c) {
        String hint = c.getString(c.getColumnIndex(QrDtbColumns.column_hint));
        String code = c.getString(c.getColumnIndex(QrDtbColumns.column_code));


        long id = c.getLong(c.getColumnIndex(QrDtbColumns._ID));
        return new QR(hint,code);
    }
}
