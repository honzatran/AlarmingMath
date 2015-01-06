package com.android.vyvojmobilapp.alarmingmath;

/**
 * Created by Petr on 6.1.2015.
 */
import android.content.Context;
import android.os.PowerManager;


//opsano z netu, managuje wakelock

public abstract class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context ctx) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);

        //flag FULL_WAKE_LOCK je deprecated, ale funguje a zatim jsem to neresil. Snad ok.
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "mytag");
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }
}