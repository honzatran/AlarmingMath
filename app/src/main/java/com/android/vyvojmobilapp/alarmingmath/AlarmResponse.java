package com.android.vyvojmobilapp.alarmingmath;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;


public class AlarmResponse extends Activity implements QrResponseFragment.OnQrFragmentInteractionListener {
    private String TAG = AlarmResponse.class.getName();
    private PowerManager.WakeLock mWakeLock;
    private Fragment fragment;

    private int WAKELOCK_TIMEOUT = 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_response);

        Intent intent = getIntent();
        Bundle bundles = intent.getExtras();
        Alarm alarm = bundles.getParcelable(Alarm.ALARM_FLAG);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


            switch(alarm.getMethodId()){
                case 2:
                    //QR
                    Log.i(TAG, "Starting QR response");
                    fragment = QrResponseFragment.newInstance(alarm);
                    break;
                default:
                    Log.i(TAG, "Starting simple response");
                    fragment = SimpleResponseFragment.newInstance(alarm);

            }
            fragmentTransaction.add(R.id.response_layout, fragment);
            fragmentTransaction.commit();

            //spustime vyzvaneni a vibrace
            Intent ringtoneIntent = new Intent(getApplicationContext(),RingtonePlayerService.class);
            ringtoneIntent.putExtra(RingtonePlayerService.START_PLAY, true);
            ringtoneIntent.putExtra(RingtonePlayerService.RINGTONE, alarm.getRingtoneUri());
            ringtoneIntent.putExtra(RingtonePlayerService.VOLUME, alarm.getVolume());
            ringtoneIntent.putExtra(RingtonePlayerService.VIBRATE, alarm.isVibrate());
            startService(ringtoneIntent);
        }






        Runnable wakeLockReleaser = new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
        };

        new Handler().postDelayed(wakeLockReleaser, WAKELOCK_TIMEOUT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //je treba zajistit, aby se pri buzeni rozsvitila a odemknula obrazovka
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
            Log.i(TAG, "Wakelock aquired!!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_response, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dismissAlarm(View view) {
        // honza : metoda ktera se vola po stisknuti tlacitka dismiss

        //propoustime wakelock - dulezite, jinak by nam to mohlo silne drainovat baterii
        WakeLocker.release();

        stopService(new Intent(getApplicationContext(), RingtonePlayerService.class));

        //taky vycistime flags, ktere jsme nastavili v onResume
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //... a vratime se na hlavni obrazovku
//        Intent intent = new Intent(this, AlarmMainActivity.class);
//        startActivity(intent);
        // honza : ukoncime aktivitu
        stopService(new Intent(getApplicationContext(), AlarmService.class));
        finish();
    }

    public void snoozeAlarm(View view) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
