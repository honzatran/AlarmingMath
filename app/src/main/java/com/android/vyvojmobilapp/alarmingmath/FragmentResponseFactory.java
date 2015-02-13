package com.android.vyvojmobilapp.alarmingmath;

import android.app.Fragment;
import android.util.Log;

/**
 * Created by honza on 1/24/15.
 * factory na vytvareni reakci(simple, QR, matematickej priklad) na budik
 */
public class FragmentResponseFactory {
    private String TAG = FragmentResponseFactory.class.getName();

    public Fragment createResponseFragment(Alarm alarm) {
        Fragment fragment;
        switch(alarm.getMethodId()){
            case Alarm.QR_CODE:
                Log.i(TAG, "Starting QR response");
                fragment = QrResponseFragment.newInstance(alarm);
                break;
            case Alarm.MATH:
                Log.i(TAG, "Starting Math response");
                fragment = MathResponseFragment.newInstance(alarm);
                break;
            default:
                Log.i(TAG, "Starting simple response");
                fragment = SimpleResponseFragment.newInstance(alarm);

        }

        return fragment;
    }


}
