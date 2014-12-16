package com.android.vyvojmobilapp.alarmingmath;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by honza on 12/15/14.
 */
public class AlarmManagerHelper extends BroadcastReceiver{
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TIMEHOUR = "timeHour";
    public static final String TIMEMINUTE = "timeMinute";
    public static final String TONE = "alarmTone";

    @Override
    public void onReceive(Context context, Intent intent) {
        // tady prijme budik ktery zrovna zvoni
        String gotIt = "GOT IT";
        Toast.makeText(context, gotIt, Toast.LENGTH_LONG).show();
        // spusti obrazovku ze zvoni budik
        Intent responseIntent = new Intent(context, AlarmResponse.class);
        responseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(responseIntent);
    }
}
