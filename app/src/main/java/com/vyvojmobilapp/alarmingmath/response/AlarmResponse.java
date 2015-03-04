package com.vyvojmobilapp.alarmingmath.response;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.vyvojmobilapp.alarmingmath.R;
import com.vyvojmobilapp.alarmingmath.alarm.Alarm;
import com.vyvojmobilapp.alarmingmath.alarm.database.AlarmDatabase;
import com.vyvojmobilapp.alarmingmath.alarm.AlarmType;
import com.vyvojmobilapp.alarmingmath.response.math.MathResponseFragment;
import com.vyvojmobilapp.alarmingmath.response.qr.QrResponseFragment;

import java.util.Calendar;


public class AlarmResponse extends Activity
        implements QrResponseFragment.OnQrFragmentInteractionListener, MathResponseFragment.OnMathFragmentInteractionListener {
    private String TAG = AlarmResponse.class.getName();
    private PowerManager.WakeLock mWakeLock;
    private Fragment fragment;

    // budik ktery zvoni
    private Alarm alarm;
    // runnable, ktery spusti se asi tak minutu po tom co obrazovka sviti a zvoni budik
    // vypne obrazovku, a odlozi budik o nejakej cas
    private Runnable wakeLockReleaser;
    // to co se stara o spusteni wakeLockReleaser
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_response);

        Intent intent = getIntent();

        // extrahujeme budik z intentu
        alarm = Alarm.extractAlarmFromIntent(intent);

//        Toast.makeText(this, alarm.toString(),Toast.LENGTH_LONG).show();

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // pomoci factory vytvarime framentu jako odezvu dle budiku
            FragmentResponseFactory factory = new FragmentResponseFactory();
            fragment = factory.createResponseFragment(alarm);

            // odezva do obrazovky
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



        wakeLockReleaser = new Runnable() {
            @Override
            public void run() {
                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                    Log.v(TAG, "Wake lock released runnable");
                    snoozeAlarm(null);
                }
            }
        };

        handler = new Handler();
        int wakeLockTimeOut;
        if (alarm.getLengthOfRinging() == -1) {
            wakeLockTimeOut = (alarm.getSnoozeDelay() * 60 - 2) * 1000;
        } else {
            wakeLockTimeOut = (alarm.getLengthOfRinging() + 30) * 1000;
        }

        handler.postDelayed(wakeLockReleaser, wakeLockTimeOut);
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
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK |
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
            Log.i(TAG, "Wakelock aquired!!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_response, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            Log.v(TAG, "Wakelock released pause");
            mWakeLock.release();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * metoda, ktera zrusi budik na zaklade interakce s uzivatelem
     * @param view
     */
    public void dismissAlarm(View view) {
        // metoda ktera se vola po stisknuti tlacitka dismiss
        returnToNormalState();
        checkAlarmType();
        // stopneme sluzbu z ktery se spousti ResponseActivity
        stopService(new Intent(getApplicationContext(), AlarmService.class));
        // ukoncime tuhle aktivitu
        finish();
    }

    /**
     * zhasne obrazovku a prerusi zvoneni budiku
     */
    private void returnToNormalState() {
        stopService(new Intent(getApplicationContext(), RingtonePlayerService.class));

        //taky vycistime flags, ktere jsme nastavili v onResume
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // stopne wakeLockReleasera
        handler.removeCallbacks(wakeLockReleaser);
    }

    /**
     * metoda ktera odlozi budik
     * @param view muze byt null
     */
    public void snoozeAlarm(View view) {
        try {
            returnToNormalState();

            // vytvorime odlozeny budik
            Alarm snoozedAlarm = alarm.getSnoozingVersion(Calendar.getInstance().
                    get(Calendar.DAY_OF_WEEK) - 1);

            checkAlarmType();

            AlarmDatabase dtb = new AlarmDatabase(this);
            // pridame novy budik
            dtb.addAlarm(snoozedAlarm);
            AlarmManagerHelper.startAlarmPendingIntent(this, false);
            // stopne servicu z ktery jsme spousteli activitu a ukoncime tuhle aktivitu
            stopService(new Intent(getApplicationContext(), AlarmService.class));
            finish();
        }
        catch (CloneNotSupportedException exp) {
            Log.v(TAG, "clone not supported exception");
        }
    }

    /**
     * koukne se jestli s budikem neni potreba provadet dodatecne operace
     */
    private void checkAlarmType() {
        AlarmType alarmType = alarm.getAlarmType();

        switch (alarmType) {
            case SNOOZE:
                reactToSnoozingAlarm();
                break;
            case ONESHOT:
                reactToOneShotAlarm();
                break;
            case REPEATING:
                break;
        }
    }

    /**
     * deaktivuje budik
     */
    private void reactToOneShotAlarm() {
        AlarmDatabase dtb = new AlarmDatabase(this);
        AlarmManagerHelper.cancelAlarmPendingIntents(this);
        // deaktivujeme alarm
        dtb.setAlarmActive(false, alarm.getId());
        alarm.setActive(false);
        AlarmManagerHelper.startAlarmPendingIntent(this, true);
    }


    /**
     * vymaze budik z dtb
     */
    private void reactToSnoozingAlarm() {
        AlarmDatabase dtb = new AlarmDatabase(this);
        AlarmManagerHelper.cancelAlarmPendingIntents(this);
        dtb.deleteAlarm(alarm.getId());
        AlarmManagerHelper.startAlarmPendingIntent(this, true);
    }

    @Override
    public void onBackPressed() {
        // na stisknuti z odkladame alarm
        snoozeAlarm(null);
        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
