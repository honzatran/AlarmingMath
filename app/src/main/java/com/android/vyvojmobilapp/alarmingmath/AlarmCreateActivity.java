package com.android.vyvojmobilapp.alarmingmath;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;


public class AlarmCreateActivity extends ActionBarActivity {
    private TimePicker picker;
    private EditText nameField;
    private Switch activeSwitch;
    private Switch vibrateSwitch;
    private Spinner snoozeDelaySpinner;
    private Spinner lengthOfRingingSpinner;
    private Spinner methodSpinner, difficultySpinner, qrSpinner;
    private SeekBar volumeSeekbar;
    private Button qrNewScan;
    private Uri uri = null;
    private QrDatabase qrDatabase;
    private TgnDayButtonClickListener daysListener;

    private static String TAG = AlarmCreateActivity.class.getName();

    //adapter pro napojeni spinneru s ulozenymi scanu kodu na databazi
    ArrayAdapter<QR> qrSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_create);

        //napojeni spinneru na databazi scanu
        qrDatabase = new QrDatabase(this);
        qrSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, qrDatabase.getQRs());
        qrSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        //nastaveni TimePickeru
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
        qrSpinner = (Spinner)findViewById(R.id.qr_spinner);
        qrNewScan = (Button) findViewById(R.id.qrNewScan);
        volumeSeekbar = (SeekBar)findViewById(R.id.volumeSeekBar);

        qrSpinner.setAdapter(qrSpinnerAdapter);

        //zpristupneni dalsich nastaveni pro jednotlive metody buzeni podle aktualni hodnoty methodSpinneru
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
    }

    private void setUpDaysButtons() {
        Resources res = getResources();
        int[] tgnDaysIds = TgnDayButtonClickListener.tgnDaysBtnIds;

        daysListener = new TgnDayButtonClickListener();

        for (int tgnDayId : tgnDaysIds) {
            ToggleButton tgnButton = (ToggleButton) findViewById(tgnDayId);
            tgnButton.setOnClickListener(daysListener);
        }
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

    /**
     * Vytvori instanci tridy Alarm podle parametru vybranych uzivatelem, vlozi jej do intentu urceneho zpet do
     * AlarmMainActivity a ukonci soucasnou aktivitu AlarmCreateActivity.
     * @param view
     */
    public void createAlarm(View view) {
        //inicializace parametru pro konstruktor budiku podle stavu jednotlivych komponent GUI
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

        QR qr = new QR("","");
        if (method == Alarm.QR_CODE){
            if (qrSpinner.getSelectedItemPosition() == Spinner.INVALID_POSITION){
                //pokud uzivatel zvolil buzeni pomoci QR kodu, musi si nejaky vybrat (ci vytvorit novy), jinak neni dovoleno opustit aktivitu
                Toast.makeText(this, "No QR code selected!", Toast.LENGTH_SHORT).show();
                return;
            }
            qr = qrSpinnerAdapter.getItem(qrSpinner.getSelectedItemPosition());
        }

        //pokud uzivatel nevybral vyzvaneci melodii, pouzijeme defaultni
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


        //vytvoreni instance tridy Alarm
        Alarm alarm = new Alarm(
                hour, minute, ringtoneUri,
                snoozeDelay, lengthOfRinging, method,
                difficulty, volume, active,
                vibrate, name, dayRecorder, alarmType, qr);

        Intent intent = new Intent(this, AlarmMainActivity.class);
        intent.putExtra(Alarm.ALARM_FLAG, alarm);
        setResult(RESULT_OK, intent);
        //navrat do AlarmMainActivity
        finish();
    }

    /**
     * Zobrazi dialog pro vyber vyzvaneci melodie.
     * @param view
     */
    public void onRingtonePickerClick(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.ringtonePickerTitle));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        startActivityForResult( intent, 0);
    }

    /**
     * Obsluhuje jednak vysledek vyberu vyzvaneni a jednak vysledek skenovani QR kodu.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //vysledek qr scanu
        if ((scanResult != null) && (scanResult.getContents() != null)) {
            final String scanContent = scanResult.getContents();

            //zobrazime dialog pro zadani textoveho hintu pro tento sken
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.fragment_qr_set_hint, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this, AlertDialog.THEME_HOLO_LIGHT);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                //vytvorime novy qr scan a vlozime do databaze
                                public void onClick(DialogInterface dialog,int id) {
                                    qrDatabase.addQr(new QR(userInput.getText().toString(), scanContent));
                                    //je treba znovu vytvorit adapter (pouha notifikace o zmene dat bohuzel nefunguje)
                                    qrSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, qrDatabase.getQRs());
                                    qrSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                                    qrSpinner.setAdapter(qrSpinnerAdapter);
                                    qrSpinner.setSelection(qrSpinner.getCount()-1);
                                }
                            });


            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        } else

        //vysledek vyberu ringtone
        if (requestCode == 0 && resultCode == RESULT_OK) {
            uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            final Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            ((Button)findViewById(R.id.ringtonePicker)).setText(ringtone.getTitle(this));
        }
    }

    /**
     * Spusti skenovani.
     * @param view
     */
    public void onQrNewScan(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt(getString(R.string.qrScanPrompt));
        integrator.initiateScan();
    }

    /**
     * Vyprazdni databazi ulozenych skenu.
     * @param view
     */
    public void onQrClearAll(View view) {
        qrDatabase.deleteAll();
        //opet je treba resetovat adapter
        qrSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, new ArrayList<QR>());
        qrSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        qrSpinner.setAdapter(qrSpinnerAdapter);
    }
}
