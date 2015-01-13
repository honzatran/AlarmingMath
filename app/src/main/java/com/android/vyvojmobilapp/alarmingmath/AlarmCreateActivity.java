package com.android.vyvojmobilapp.alarmingmath;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class AlarmCreateActivity extends ActionBarActivity {
    TimePicker picker;
    EditText nameField;
    Switch activeSwitch;
    Switch vibrateSwitch;
    Spinner snoozeDelaySpinner;
    Spinner lengthOfRingingSpinner;
    Spinner methodSpinner, difficultySpinner;
    SeekBar volumeSeekbar;
    Uri uri = null;

    private static String TAG = "Alarm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_create);

        picker = (TimePicker) findViewById(R.id.alarm_time_picker);
        picker.setIs24HourView(true);

        //defaultne nastavi napr. pri spusteni v 15:27 cas 3:27, nasleduje fix
        picker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        nameField = (EditText)findViewById(R.id.name_field);
        activeSwitch = (Switch)findViewById(R.id.active_switch);
        vibrateSwitch = (Switch)findViewById(R.id.vibrate_switch);
        snoozeDelaySpinner = (Spinner)findViewById(R.id.snoozeDelay_spinner);
        lengthOfRingingSpinner = (Spinner)findViewById(R.id.lengthOfRinging_spinner);
        methodSpinner = (Spinner)findViewById(R.id.method_spinner);
        difficultySpinner = (Spinner)findViewById(R.id.difficulty_spinner);

        methodSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            difficultySpinner.setEnabled(true);
                        } else {
                            difficultySpinner.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        difficultySpinner.setEnabled(false);
                    }
                }
        );
        volumeSeekbar = (SeekBar)findViewById(R.id.volumeSeekBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_create, menu);
        return true;
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

    public void createAlarm(View view) {
        int hour = picker.getCurrentHour();
        int minute = picker.getCurrentMinute();
        String name = nameField.getText().toString();
        boolean active = activeSwitch.isChecked();
        boolean vibrate = vibrateSwitch.isChecked();
        int snoozeDelay = Integer.parseInt(snoozeDelaySpinner.getSelectedItem().toString());
        int lengthOfRinging = (lengthOfRingingSpinner.getSelectedItemPosition() == 5)
                ? -1
                : lengthOfRingingSpinner.getSelectedItemPosition()*30;
        int method = methodSpinner.getSelectedItemPosition();
        int difficulty = difficultySpinner.getSelectedItemPosition();
        int volume = volumeSeekbar.getProgress();


        if (uri == null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        String ringtoneUri = uri.toString();

        Alarm alarm = new Alarm(hour, minute, ringtoneUri, snoozeDelay, lengthOfRinging, method, difficulty, volume, active, vibrate, name);

        Intent intent = new Intent(this, AlarmMainActivity.class);
        intent.putExtra(Alarm.ALARM_FLAG, alarm);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onRingtonePickerClick(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Vyberte melodii pro budík:");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        startActivityForResult( intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri == null)
            {
                Log.v(TAG, "returned uri is null");
            }
            final Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            ((Button)findViewById(R.id.ringtonePicker)).setText(ringtone.getTitle(this));
        }
    }
}
