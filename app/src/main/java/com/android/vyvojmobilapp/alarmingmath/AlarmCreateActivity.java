package com.android.vyvojmobilapp.alarmingmath;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;


public class AlarmCreateActivity extends ActionBarActivity {
    TimePicker picker;
    EditText nameField;
    Switch activeSwitch;
    Switch vibrateSwitch;
    Spinner snoozeDelaySpinner;
    Spinner lengthOfRingingSpinner;
    Spinner methodSpinner;
    SeekBar volumeSeekbar;

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
        int volume = volumeSeekbar.getProgress();

        Intent intent = new Intent(this, AlarmMainActivity.class);
        intent.putExtra(Alarm.HOUR, hour);
        intent.putExtra(Alarm.MINUTES, minute);
        intent.putExtra(Alarm.RINGTONE, -1);
        intent.putExtra(Alarm.NAME, name);
        intent.putExtra(Alarm.IS_ACTIVE, active);
        intent.putExtra(Alarm.IS_VIBRATE, vibrate);
        intent.putExtra(Alarm.SNOOZE_DELAY, snoozeDelay);
        intent.putExtra(Alarm.LENGTH_OF_RINGING, lengthOfRinging);
        intent.putExtra(Alarm.METHOD_ID, method);
        intent.putExtra(Alarm.VOLUME, volume);
        startActivity(intent);
    }
}
