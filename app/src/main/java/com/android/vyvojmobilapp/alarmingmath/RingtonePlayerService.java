package com.android.vyvojmobilapp.alarmingmath;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;


//service pro ringtone a vibrace
public class RingtonePlayerService extends Service {

    private MediaPlayer mediaPlayer = null;
    private Vibrator vibrator = null;
    private boolean isPlaying = false;


    public static String START_PLAY = "START_PLAY";
    public static String RINGTONE = "ringtone";
    public static String VOLUME = "volume";
    public static String VIBRATE = "vibrate";


    public RingtonePlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(START_PLAY, false)) {
            Uri ringtone = Uri.parse(intent.getStringExtra(RINGTONE));
            int volume = intent.getIntExtra(VOLUME, 75);
            boolean vibrate = intent.getBooleanExtra(VIBRATE, true);

            play(ringtone, volume, vibrate);
        }
        return Service.START_STICKY;
    }

    private void play(Uri ringtoneUri, int volume, boolean vibrate) {

        // todo smazat return
        // marketa - ladeni - muj emulator si neporadi se zvonenim
//        if (true) return;

        if (!isPlaying) {
            isPlaying = true;


            mediaPlayer = MediaPlayer.create(this, ringtoneUri);
            mediaPlayer.setLooping(true); // this will make it loop forever
            float floatVolume = (float)volume / 100;
            mediaPlayer.setVolume(floatVolume, floatVolume);
            mediaPlayer.start();

            vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {500,500};

            // The '0' here means to repeat indefinitely
            // '0' is actually the index at which the pattern keeps repeating from (the start)
            // To repeat the pattern from any other point, you could increase the index, e.g. '1'

            if(vibrate){
                vibrator.vibrate(pattern, 0);
            }
        }
    }

    @Override
    public void onDestroy() {
        stop();
    }

    private void stop() {
        if (isPlaying) {
            isPlaying = false;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if(vibrator != null){
                vibrator.cancel();
            }

        }
    }

}
