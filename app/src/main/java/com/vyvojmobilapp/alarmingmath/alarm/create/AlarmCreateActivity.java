package com.vyvojmobilapp.alarmingmath.alarm.create;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.vyvojmobilapp.alarmingmath.AlarmMainActivity;
import com.vyvojmobilapp.alarmingmath.R;
import com.vyvojmobilapp.alarmingmath.alarm.Alarm;
import com.vyvojmobilapp.alarmingmath.alarm.AlarmType;
import com.vyvojmobilapp.alarmingmath.alarm.DayRecorder;
import com.vyvojmobilapp.alarmingmath.response.qr.QR;
import com.vyvojmobilapp.alarmingmath.response.qr.QrDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;


public class AlarmCreateActivity extends ActionBarActivity {
    TimePicker picker;
    EditText nameField;
    Switch activeSwitch;
    Switch vibrateSwitch;
    Spinner snoozeDelaySpinner;
    Spinner lengthOfRingingSpinner;
    Spinner methodSpinner, difficultySpinner, qrSpinner;
    SeekBar volumeSeekbar;
    Button qrNewScan;
    Uri uri = null;
    QrDatabase qrDatabase;
    String qrHint;
    TgnDayButtonClickListener daysListener;

    private static String TAG = AlarmCreateActivity.class.getName();
    ArrayAdapter<QR> qrSpinnerAdapter;


    boolean updateAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_alarm_create);

        qrDatabase = new QrDatabase(this);


        qrSpinnerAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, qrDatabase.getQRs());
        qrSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);


        picker = (TimePicker) findViewById(R.id.alarm_time_picker);
        picker.setIs24HourView(true);

        //defaultne nastavi napr. pri spusteni v 15:27 cas 3:27, nasleduje fix
        picker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        initializeWidget();

        methodSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 1) {
                            difficultySpinner.setEnabled(true);
                            qrSpinner.setEnabled(false);
                            qrNewScan.setEnabled(false);
                        } else if (position == 2) {
                            difficultySpinner.setEnabled(false);
                            qrSpinner.setEnabled(true);
                            qrNewScan.setEnabled(true);
                        } else {
                            difficultySpinner.setEnabled(false);
                            qrSpinner.setEnabled(false);
                            qrNewScan.setEnabled(false);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        difficultySpinner.setEnabled(false);
                        qrSpinner.setEnabled(false);
                        qrNewScan.setEnabled(false);
                    }
                }
        );

        setUpDaysButtons();

        if (intent.hasExtra(Alarm.ALARM_FLAG)) {
            updateAlarm = true;
            setTitle(R.string.title_activity_alarm_update);
            setUpWidgetsFromAlarm(intent);
        }

    }

    private void setUpWidgetsFromAlarm(Intent intent) {
        Alarm alarm = Alarm.extractAlarmFromIntent(intent);

        volumeSeekbar.setProgress(alarm.getVolume());
        picker.setCurrentHour(alarm.getHour());
        picker.setCurrentMinute(alarm.getMinute());

        nameField.setText(alarm.getName());

        alarm.setActive(true);
        activeSwitch.setChecked(alarm.isActive());
        vibrateSwitch.setChecked(alarm.isVibrate());

        String[] snoozeStrings = getResources().getStringArray(R.array.snoozeDelaySpinnerItems);
        for (int i = 0; i < snoozeStrings.length; i++) {
            if (Integer.parseInt(snoozeStrings[i]) == alarm.getSnoozeDelay())
                snoozeDelaySpinner.setSelection(i);
        }
        methodSpinner.setSelection(alarm.getMethodId());
        if (alarm.getLengthOfRinging() == -1)
            lengthOfRingingSpinner.setSelection(lengthOfRingingSpinner.getCount() - 1);
        else
            lengthOfRingingSpinner.setSelection(alarm.getLengthOfRinging()/30);
        difficultySpinner.setSelection(alarm.getDifficulty());

        if (alarm.getMethodId() == 2) {
            qrNewScan.setActivated(true);
            qrSpinner.setActivated(true);
            for(int i=0; i < qrSpinnerAdapter.getCount(); i++){
                if(qrSpinnerAdapter.getItem(i).toString().equals(alarm.getQr().toString())){
                    qrSpinner.setSelection(i);
                    break;
                }
            }


        }

        uri = Uri.parse(alarm.getRingtoneUri());
        final Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        ((Button)findViewById(R.id.ringtonePicker)).setText(ringtone.getTitle(this));

        int[] tgnDaysIds = TgnDayButtonClickListener.tgnDaysBtnIds;
        daysListener.recorder = alarm.getDays();

        for (int i = 0; i < 7; i++) {
            if (daysListener.recorder.isDaySet(i)) {
                ToggleButton tgBtn = (ToggleButton) findViewById(tgnDaysIds[i]);
                tgBtn.setChecked(true);
            }
        }
    }

    private void initializeWidget() {
        nameField = (EditText)findViewById(R.id.name_field);
        activeSwitch = (Switch)findViewById(R.id.active_switch);
        vibrateSwitch = (Switch)findViewById(R.id.vibrate_switch);
        snoozeDelaySpinner = (Spinner)findViewById(R.id.snoozeDelay_spinner);
        lengthOfRingingSpinner = (Spinner)findViewById(R.id.lengthOfRinging_spinner);
        methodSpinner = (Spinner)findViewById(R.id.method_spinner);
        difficultySpinner = (Spinner)findViewById(R.id.difficulty_spinner);
        qrSpinner = (Spinner)findViewById(R.id.qr_spinner);
        qrNewScan = (Button) findViewById(R.id.qrNewScan);
        qrSpinner.setAdapter(qrSpinnerAdapter);
        volumeSeekbar = (SeekBar)findViewById(R.id.volumeSeekBar);
    }


    private void setUpDaysButtons() {
        int[] tgnDaysIds = TgnDayButtonClickListener.tgnDaysBtnIds;

        daysListener = new TgnDayButtonClickListener();

        for (int tgnDayId : tgnDaysIds) {
            ToggleButton tgnButton = (ToggleButton) findViewById(tgnDayId);
            tgnButton.setOnClickListener(daysListener);
        }
    }


    public void createAlarm(View view) {
        int hour = picker.getCurrentHour();
        int minute = picker.getCurrentMinute();
        String name = nameField.getText().toString();
        boolean active = activeSwitch.isChecked();
        boolean vibrate = vibrateSwitch.isChecked();
        int snoozeDelay = Integer.parseInt(snoozeDelaySpinner.getSelectedItem().toString());

        int lengthOfRinging =
                (lengthOfRingingSpinner.getSelectedItemPosition() == lengthOfRingingSpinner.getCount() - 1)
                ? -1 : lengthOfRingingSpinner.getSelectedItemPosition()*30;

        int method = methodSpinner.getSelectedItemPosition();
        int difficulty = difficultySpinner.getSelectedItemPosition();
        int volume = volumeSeekbar.getProgress();

        QR qr = new QR("","");
        if (method == Alarm.QR_CODE){
            if (qrSpinner.getSelectedItemPosition() == Spinner.INVALID_POSITION){
                Toast.makeText(this, R.string.noqr_selected, Toast.LENGTH_SHORT).show();
                return;
            }
            qr = qrSpinnerAdapter.getItem(qrSpinner.getSelectedItemPosition());
        }

        if (uri == null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        String ringtoneUri = uri.toString();

        DayRecorder dayRecorder = daysListener.getRecorder();
        Log.v(TAG, "mask of days" + dayRecorder.getMask());
        byte mask = dayRecorder.getMask();

        AlarmType alarmType;
        // budik nebyl naplanovanej na zadnej den
        if (mask == 0) {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            if (hour <= calendar.get(Calendar.HOUR) &&
                    minute < calendar.get(Calendar.MINUTE)) {
                // cas uz dnes uplynul
                // naplanuj na dalsi den
                dayRecorder.setDay(true, (currentDay + 1) % 7);
            } else {
                // cas jeste neuplynul tak naplanuj budik na dnesek
                dayRecorder.setDay(true, currentDay);
            }

            alarmType = AlarmType.ONESHOT;
        } else  {
            alarmType = AlarmType.REPEATING;
        }

        Alarm alarm = new Alarm(
                hour, minute, ringtoneUri,
                snoozeDelay, lengthOfRinging, method,
                difficulty, volume, active,
                vibrate, name, dayRecorder, alarmType, qr);

        Intent intent = new Intent(this, AlarmMainActivity.class);
        intent.putExtra(Alarm.ALARM_FLAG, alarm);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onRingtonePickerClick(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, R.string.ringtonePickerTitle);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        startActivityForResult( intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.i("qr", "called onactivityresult");

        //vysledek qr scanu
        if ((scanResult != null) && (scanResult.getContents() != null)) {
            final String scanContent = scanResult.getContents();

            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.fragment_qr_set_hint, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this, AlertDialog.THEME_HOLO_LIGHT);

            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            // nastav dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getApplicationContext().getString(R.string.OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // ziskej uzivatelskej vstup a nastav ho na result
                                    // edituj text

                                    qrDatabase.addQr(new QR(userInput.getText().toString(), scanContent));
                                    Log.i("scanResult","after db, hint: " + qrHint);
                                    qrSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, qrDatabase.getQRs());
                                    qrSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                                    qrSpinner.setAdapter(qrSpinnerAdapter);
                                    qrSpinner.setSelection(qrSpinner.getCount()-1);
                                }
                            });


            // vytvor dialog upozorneni
            AlertDialog alertDialog = alertDialogBuilder.create();
            // ukaz ho
            alertDialog.show();
        } else
        //vysledek vyberu ringtone
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

    public void onQrNewScan(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt(getApplicationContext().getString(R.string.qrScanPrompt));
        integrator.initiateScan(); // `this` is the current Activity
    }


    public void onQrClearAll(View view) {
        qrDatabase.deleteAll();
        qrSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, new ArrayList<QR>());
        qrSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        qrSpinner.setAdapter(qrSpinnerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (updateAlarm)  {
            activeSwitch.setChecked(false);
            createAlarm(null);
        }

        super.onBackPressed();
    }
}
