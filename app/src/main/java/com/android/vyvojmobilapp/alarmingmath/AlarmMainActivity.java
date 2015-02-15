package com.android.vyvojmobilapp.alarmingmath;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

// dodelat mazani budiku, plus budiky v ruznych dnech a nejakej ringtone

// TODO podpora budiku v ruznych dnech, aktivace a deaktivace budiku + zabudovani mechanismu pro ruzny response
public class AlarmMainActivity extends ActionBarActivity {
    private String TAG = "MAIN ACTIVITY";
    ListView alarmListView;
    AlarmContainer alarms;
    ArrayAdapter<Alarm> alarmArrayAdapter2;
    AlarmListAdapter alarmArrayAdapter;
    int counter;
    static final int ALARM_CREATE_RESULT = 1;
    static final int ALARM_UPDATE_RESULT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AlarmDatabase alarmDatabase = new AlarmDatabase(this);

        //--debugging purposes
        //getApplicationContext().deleteDatabase("alarmDatabase.db");

        alarms = new AlarmContainer(alarmDatabase);
        alarmArrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                alarms);
        alarmArrayAdapter = new AlarmListAdapter(this, R.layout.listview_main_item, alarms);

        alarmListView = (ListView)findViewById(R.id.alarm_list);
        alarmListView.setAdapter(alarmArrayAdapter);
        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Alarm alarm = alarmArrayAdapter.getItem(arg2);
                AlarmManagerHelper.cancelAlarmPendingIntents(getApplicationContext());
                if (alarm.isActive()) {
                    alarmDatabase.setAlarmActive(false, alarm.getId());
                    alarm.active = false;
                } else {
                    alarmDatabase.setAlarmActive(true, alarm.getId());
                    alarm.active = true;
                }
                alarmArrayAdapter.notifyDataSetChanged();
                AlarmManagerHelper.startAlarmPendingIntent(getApplicationContext(), true);
            }});

        //registrujeme tridy AlarmMainActivity jako obsluznou pro alarmListView (metody jsou nize)
        registerForContextMenu(alarmListView);
        counter = 42;
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarms = new AlarmContainer(new AlarmDatabase(this));
        alarmArrayAdapter = new AlarmListAdapter(this, R.layout.listview_main_item, alarms);
        alarmListView.setAdapter(alarmArrayAdapter);
    }

    //vytvoreni kontextoveho menu pro poloznky v seznamu budiku
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //magic pro ziskani pozice polozky v seznamu
        AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

        Alarm alarm = alarmArrayAdapter.getItem(aInfo.position);
        menu.setHeaderTitle(getString(R.string.alarm_click_options) + alarm.toString());
        menu.add(1, 1, 1, R.string.details);
        menu.add(1, 2, 2, R.string.update);
        menu.add(1, 3, 3, R.string.delete_alarm);
    }

    // This method is called when user selects an Item in the Context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //index polozky v menu, nikoliv budiku v seznamu!
        int itemId = item.getItemId();

        //same magic..
        AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Alarm alarm = alarmArrayAdapter.getItem(aInfo.position);

        switch (itemId){
            case 1: //Details
                String toastMsg = createInfoToastMsg(alarm);

                Toast.makeText(this, toastMsg,
                        Toast.LENGTH_LONG).show();
                break;
            case 2: //update
                // todo no prostě to dodělat
                //Toast.makeText(getApplicationContext(), alarm. , Toast.LENGTH_SHORT).show();
                Parcel parcel = alarm.createParcel();
                Intent intent = new Intent(this, AlarmCreateActivity.class);
                intent.putExtra(Alarm.ALARM_FLAG, parcel.marshall());
                AlarmManagerHelper.cancelAlarmPendingIntents(this);
                alarms.remove(alarm);
                AlarmManagerHelper.startAlarmPendingIntent(this, true);
                startActivityForResult(intent, ALARM_UPDATE_RESULT);
                break;
            case 3: //delete
                AlarmManagerHelper.cancelAlarmPendingIntents(this);
                alarms.remove(alarm); //mozna bych tohle pole nejak provazal s databazi
                alarmArrayAdapter.notifyDataSetChanged();  // important
                String msg = String.format(getString(R.string.delete_msg_alarm), alarm.toString());
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                AlarmManagerHelper.startAlarmPendingIntent(this, true);
                break;
            default:

        }

        return true;
    }

    private String createInfoToastMsg(Alarm alarm) {
        StringBuilder msg = new StringBuilder(getString(R.string.informations) + "\n");
        msg.append(String.format("%s %s\n", getString(R.string.name_textview), alarm.getName()));
        msg.append(String.format("%s %s\n", getString(R.string.time), alarm.toString()));
        msg.append(String.format("%s %s %s\n", getString(R.string.snoozeDelay_text), alarm.getSnoozeDelay(),
                getString(R.string.minute)));

        int lenghtOfRinging = alarm.getLengthOfRinging() + 30;
        msg.append(String.format("%s %s %s\n", getString(R.string.lengthOfRinging_text),
                lenghtOfRinging, getString(R.string.second)));


        int resMethodField;
        switch (alarm.getMethodId()) {
            case 0:
                resMethodField = R.string.simple;
                break;
            case 1:
                resMethodField = R.string.math;
                break;
            default:
                resMethodField = R.string.qr;
                break;
        }
        msg.append(String.format("%s %s\n", getString(R.string.method_text), getString(resMethodField)));

        Ringtone ringtone = RingtoneManager.getRingtone(this, Uri.parse(alarm.getRingtoneUri()));
        msg.append(String.format("%s %s\n", getString(R.string.ringing_text), ringtone.getTitle(this)));
        msg.append(String.format("%s %s\n", getString(R.string.vibrate_text),
                alarm.isVibrate() ? getString(R.string.yes) : getString(R.string.no)));


        return msg.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void showCreateAlarm(View view) {
        Log.v(TAG, "create pressed");

        Intent intent = new Intent(this, AlarmCreateActivity.class);
        // honza: ceka na vysledek aktivity vytvor budik
        startActivityForResult(intent, ALARM_CREATE_RESULT);
    }

    public void clearAlarms(View view) {
        AlarmManagerHelper.cancelAlarmPendingIntents(this);
        alarms.clear();
        alarmArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ALARM_CREATE_RESULT) {
            if (resultCode == RESULT_OK && data.hasExtra(Alarm.ALARM_FLAG)) {
                // honza: dosel novej budik
                AlarmManagerHelper.cancelAlarmPendingIntents(this);
                Alarm newAlarm = data.getExtras().getParcelable(Alarm.ALARM_FLAG);
                alarms.add(newAlarm);
                alarmArrayAdapter.notifyDataSetChanged();
                AlarmManagerHelper.startAlarmPendingIntent(this, true);
            }
        } else if (requestCode == ALARM_UPDATE_RESULT) {
            if (resultCode == RESULT_OK && data.hasExtra(Alarm.ALARM_FLAG)) {
                AlarmManagerHelper.cancelAlarmPendingIntents(this);
                Alarm newAlarm = data.getExtras().getParcelable(Alarm.ALARM_FLAG);
                alarms.add(newAlarm);
                alarmArrayAdapter.notifyDataSetChanged();
                AlarmManagerHelper.startAlarmPendingIntent(this, true);
            }
        }
    }
}
