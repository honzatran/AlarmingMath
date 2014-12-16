package com.android.vyvojmobilapp.alarmingmath;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by honza on 12/15/14.
 */
public class AlarmService extends Service {
    // opsany z netu zatim zadna funkcnost
    public static String TAG = AlarmService.class.getName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return -1;
    }
}
