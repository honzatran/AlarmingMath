package com.android.vyvojmobilapp.alarmingmath;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Trida pro udrzovani SQLite databaze ulozenych QR ci barcode scanu.
 */
public class QrDatabase extends SQLiteOpenHelper {
    public static final int version = 1;
    public static final String dbName = "qrDatabase.db";

    /**
     * Query pro vytvoreni tabulky scanu ve vhodnem formatu.
     */
    private static final String CREATE_ALARM_TABLE =
            "CREATE TABLE " + QrDtbColumns.name + " (" +
                    QrDtbColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    QrDtbColumns.column_hint + " TEXT, " +
                    QrDtbColumns.column_code + " TEXT)";

    /**
     * Query pro vymazani tabulky scanu.
     */
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

    /**
     * Prida QR scan do db.
     * @param qr QR scan
     * @return id nove pridaneho scanu
     */
    public long addQr(QR qr) {
        return getWritableDatabase().insert(QrDtbColumns.name,
                null, convertToContentValue(qr));
    }

    /**
     * Smaze vsechny scany z db.
     */
    public void deleteAll() {
        getWritableDatabase().execSQL(
                "delete from " + QrDtbColumns.name
        );
    }

    /**
     * Vrati vsechny scany ulozene v db.
     * @return seznam scanu ulozenych v db
     */
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

        return new QR(hint,code);
    }
}
