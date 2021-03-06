package com.vyvojmobilapp.alarmingmath.response;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
    Service pro zvoneni.
 */
public class AlarmService extends Service {
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

        // spusti activity alarm response
        Intent responseIntent =  new Intent(getBaseContext(), AlarmResponse.class);
        responseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        responseIntent.putExtras(intent.getExtras());

        getApplication().startActivity(responseIntent);

        // nastavi vsechny dalsi budiky
        AlarmManagerHelper.startAlarmPendingIntent(this, false);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "destroyed");
    }
}
