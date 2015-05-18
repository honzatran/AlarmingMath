package com.vyvojmobilapp.alarmingmath;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.vyvojmobilapp.alarmingmath.alarm.Alarm;
import com.vyvojmobilapp.alarmingmath.alarm.create.AlarmCreateActivity;
import com.vyvojmobilapp.alarmingmath.response.AlarmManagerHelper;

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

        //--debugging purposes
        //getApplicationContext().deleteDatabase("alarmDatabase.db");

        alarms = new AlarmContainer();
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
                    Alarm.setAlarmActive(false, alarm.getId());
                    alarm.setActive(false);
                } else {
                    Alarm.setAlarmActive(true, alarm.getId());
                    alarm.setActive(true);
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
        alarms = new AlarmContainer();
        alarmArrayAdapter = new AlarmListAdapter(this, R.layout.listview_main_item, alarms);
        alarmListView.setAdapter(alarmArrayAdapter);
    }

    //vytvoreni kontextoveho menu pro poloznky v seznamu budiku
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_context, menu);

        //magic pro ziskani pozice polozky v seznamu
        //AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

        //Alarm alarm = alarmArrayAdapter.getItem(aInfo.position);
        //menu.setHeaderTitle(getString(R.string.alarm_click_options) + alarm.toString());
        //menu.add(1, 1, 1, R.string.details);
        //menu.add(1, 2, 2, R.string.update);
        //menu.add(1, 3, 3, R.string.delete_alarm);
    }

    // This method is called when user selects an Item in the Context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //index polozky v menu, nikoliv budiku v seznamu!
        //int itemId = item.getItemId();

        //AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Alarm alarm = alarmArrayAdapter.getItem(info.position);

        switch (item.getItemId()) {
            case R.id.details:
                String toastMsg = createInfoToastMsg(alarm);

                Toast.makeText(this, toastMsg,
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.update_alarm:
                Parcel parcel = alarm.createParcel();
                Intent intent = new Intent(this, AlarmCreateActivity.class);
                intent.putExtra(Alarm.ALARM_FLAG, parcel.marshall());
                AlarmManagerHelper.cancelAlarmPendingIntents(this);
                alarms.remove(alarm);
                AlarmManagerHelper.startAlarmPendingIntent(this, true);
                startActivityForResult(intent, ALARM_UPDATE_RESULT);
                break;
            case R.id.delete_alarm:
                AlarmManagerHelper.cancelAlarmPendingIntents(this);
                alarms.remove(alarm); //mozna bych tohle pole nejak provazal s databazi
                alarmArrayAdapter.notifyDataSetChanged();  // important
                String msg = String.format(getString(R.string.delete_msg_alarm), alarm.toString());
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                AlarmManagerHelper.startAlarmPendingIntent(this, true);
                break;
            default:
                return super.onContextItemSelected(item);
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

    public void showCreateAlarm(View view) {
        Log.v(TAG, "create pressed");

        Intent intent = new Intent(this, AlarmCreateActivity.class);
        // ceka na vysledek aktivity vytvor budik
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
                // dosel novej budik
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
