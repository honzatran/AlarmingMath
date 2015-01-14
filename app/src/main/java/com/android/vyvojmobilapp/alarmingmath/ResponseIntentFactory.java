package com.android.vyvojmobilapp.alarmingmath;

import android.content.Context;
import android.content.Intent;

/**
 * Created by honza on 1/13/15.
 */
public class ResponseIntentFactory {


    public Intent createResponseIntent(Context context,Intent intent) {
        // tady bude vytvaret jednotlivy intent pro spusteni QR, matematickeho kodu atd.
        Alarm alarm = intent.getParcelableExtra(Alarm.ALARM_FLAG);
        switch(alarm.getMethodId()){
            case 2:
                //QR
                return new Intent(context, AlarmResponseQR.class);
            default:
                return new Intent(context, AlarmResponse.class);
        }

    }
}
