package com.android.vyvojmobilapp.alarmingmath;

import android.content.Context;
import android.os.PowerManager;

// stara se o wakelock
public abstract class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    // ziskej zamek
    public static void acquire(Context ctx) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);

        //flag FULL_WAKE_LOCK je deprecated, ale funguje
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "mytag");
        wakeLock.acquire();
    }

    // pust zamek
    public static void release() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }
}