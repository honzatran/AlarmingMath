package com.android.vyvojmobilapp.alarmingmath;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by honza on 12/15/14.
 */
public class AlarmService extends Service {
    // honza opsany z netu
    public static String TAG = AlarmService.class.getName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // tady prijme budik ktery zrovna zvoni

        //je potreba zajistit wakelock, jinak se muze stat, ze se aktivita AlarmResponse vubec nespusti... wakelock releasuje AlarmResponse (!important)
        Log.v(TAG, "service start");
        // spusti obrazovku ze zvoni budik

        // honza: spusti activity alarm response
        ResponseIntentFactory factory = new ResponseIntentFactory();
        Intent responseIntent = factory.createResponseIntent(getBaseContext(), intent);
        responseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        responseIntent.putExtras(intent.getExtras());

        String gotIt = ((Alarm)intent.getParcelableExtra(Alarm.ALARM_FLAG)).toString();
        Toast.makeText(getApplicationContext(), gotIt, Toast.LENGTH_LONG).show();
        getApplication().startActivity(responseIntent);

        // honza: nastavi vsechny dalsi budiky
        AlarmManagerHelper.startAlarmPendingIntent(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "destroyed");
    }
}
