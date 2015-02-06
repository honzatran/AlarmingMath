package com.android.vyvojmobilapp.alarmingmath;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Calendar;


public class AlarmResponse extends Activity implements QrResponseFragment.OnQrFragmentInteractionListener {
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

    private int WAKELOCK_TIMEOUT = 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_response);

        Intent intent = getIntent();
//        byte[] arr = intent.getByteArrayExtra(Alarm.ALARM_FLAG);
//        Parcel parcel = Parcel.obtain();
//        parcel.unmarshall(arr, 0, arr.length);
//        parcel.setDataPosition(0);
        // extrahujeme budik z intentu
        alarm = Alarm.extractAlarmFromIntent(intent);

        Toast.makeText(this, alarm.toString(),Toast.LENGTH_LONG).show();

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

        // za WAKELOCK_TIMEOUT minut spustime wakeLockReleaser
        handler.postDelayed(wakeLockReleaser, WAKELOCK_TIMEOUT);
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
        // Inflate the menu; this adds items to the action bar if it is present.
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

    /**
     * metoda, ktera zrusi budik na zaklade interakce s uzivatelem
     * @param view
     */
    public void dismissAlarm(View view) {
        // honza : metoda ktera se vola po stisknuti tlacitka dismiss
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
        // honza: na stisknuti z odkladame alarm
        snoozeAlarm(null);
        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
