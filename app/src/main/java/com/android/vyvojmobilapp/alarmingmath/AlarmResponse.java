package com.android.vyvojmobilapp.alarmingmath;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.os.Vibrator;


public class AlarmResponse extends Activity {
    Vibrator v;
    private MediaPlayer player;
    private String TAG = AlarmResponse.class.getName();
    private PowerManager.WakeLock mWakeLock;

    private int WAKELOCK_TIMEOUT = 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_response);

        Intent intent = getIntent();
        Bundle bundles = intent.getExtras();
        Alarm alarm = bundles.getParcelable(Alarm.ALARM_FLAG);

        TextView tv = (TextView)findViewById(R.id.alarm_ringing);
        tv.setText("Alarm is ringing!\n"
                        + alarm.getName()
                        + "\n" + alarm.toString()
                        + "\nringtone id: " + alarm.getRingtoneUri()
                        + "\nsnooze delay: " + alarm.getSnoozeDelay()
                        + "\nlength of ringing: " + alarm.getLengthOfRinging()
                        + "\nmethod id: " + alarm.getMethodId()
                        + "\ndifficulty: " + alarm.getDifficulty()
                        + "\nvolume: " + alarm.getVolume()
                        + "\nvibrate: " + alarm.isVibrate()
        );


        player = MediaPlayer.create(this, Uri.parse(alarm.ringtoneUri));
        if (player == null) {
            Log.v(TAG, "line 65 : player null ptr");
        } else {
            Log.v(TAG, "ok");
        }
        player.setLooping(true);
        float floatVolume = (float)alarm.getVolume() / 100;
        player.setVolume(floatVolume, floatVolume);
        player.start();

        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {500,500};

        // The '0' here means to repeat indefinitely
        // '0' is actually the index at which the pattern keeps repeating from (the start)
        // To repeat the pattern from any other point, you could increase the index, e.g. '1'

        if(alarm.isVibrate()){
            v.vibrate(pattern, 0);
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
        v.cancel();
        player.stop();
        player.release();

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
}
