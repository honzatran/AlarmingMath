package com.android.vyvojmobilapp.alarmingmath;

import android.content.Context;
import android.content.Intent;

/**
 * Created by honza on 1/13/15.
 */
public class ResponseIntentFactory {


    public Intent createResponseIntent(Context context,Intent intent) {
        // tady bude vytvaret jednotlivy intent pro spusteni QR, matematickeho kodu atd.
        return new Intent(context, AlarmResponse.class);
    }
}
