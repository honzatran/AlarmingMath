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
    public static String TAG = AlarmService.class.getName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmManagerHelper.setAlarm(this);
        System.out.println("start");

        Log.d("AlarmService", "on start cmd");
        Toast.makeText(getApplicationContext(), intent.getExtras().getInt("HOUR") +
                " " + intent.getExtras().getInt("MINUTE"), Toast.LENGTH_LONG).show();

        getApplication().startService(intent);

        return super.onStartCommand(intent, flags, startId);
    }
}
