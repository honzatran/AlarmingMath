package com.android.vyvojmobilapp.alarmingmath;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AlarmDatabase alarmDatabase = new AlarmDatabase(this);

        //--debugging purposes
        //getApplicationContext().deleteDatabase("alarmDatabase.db");

        alarms = new AlarmContainer(alarmDatabase);
        alarmArrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                alarms);
        alarmArrayAdapter = new AlarmListAdapter(this, R.layout.listview_main_item, alarms);

        alarmListView = (ListView)findViewById(R.id.alarm_list);
        alarmListView.setAdapter(alarmArrayAdapter);

        //registrujeme tridy AlarmMainActivity jako obsluznou pro alarmListView (metody jsou nize)
        registerForContextMenu(alarmListView);
        counter = 42;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //vytvoreni kontextoveho menu pro poloznky v seznamu budiku
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //magic pro ziskani pozice polozky v seznamu
        AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

        Alarm alarm = alarmArrayAdapter.getItem(aInfo.position);
        menu.setHeaderTitle("Options for alarm at " + alarm.toString());
        menu.add(1, 1, 1, "Details");
        menu.add(1, 2, 2, "Delete");
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
                Toast.makeText(this, "Info:\nČas: "+alarm.toString()+
                                "\nName: "+alarm.getName()+
                                "\nid: "+alarm.getId()+
                                "\nRingtone: "+alarm.getRingtoneUri()+
                                "\nSnooze delay: "+alarm.getSnoozeDelay()+
                                "\nLength of ringing: "+alarm.getLengthOfRinging()+
                                "\nMethod: "+alarm.getMethodId()+
                                "\nVolume: "+alarm.getVolume()+
                                "\nActive: "+alarm.isActive()+
                                "\nVibrate: "+alarm.isVibrate() +
                                "\nDays mask:" + alarm.getDays().getMask() +
                                "\nOne shot:" + alarm.isSnoozingAlarm() ,
                        Toast.LENGTH_LONG).show();
                break;
            case 2: //delete
                AlarmManagerHelper.cancelAlarmPendingIntents(this);
                alarms.remove(alarm); //mozna bych tohle pole nejak provazal s databazi
                alarmArrayAdapter.notifyDataSetChanged();  // important
                Toast.makeText(this, "Budík v čase "+alarm.toString()+" smazán.", Toast.LENGTH_SHORT).show();
                AlarmManagerHelper.startAlarmPendingIntent(this, true);
                break;
            default:

        }

        return true;
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
        startActivityForResult(intent, 1);
    }

    public void clearAlarms(View view) {
        AlarmManagerHelper.cancelAlarmPendingIntents(this);
        alarms.clear();
        alarmArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data.hasExtra(Alarm.ALARM_FLAG)) {
                // honza: dosel novej budik
                AlarmManagerHelper.cancelAlarmPendingIntents(this);
                Alarm newAlarm = data.getExtras().getParcelable(Alarm.ALARM_FLAG);
                alarms.add(newAlarm);
                alarmArrayAdapter.notifyDataSetChanged();
                AlarmManagerHelper.startAlarmPendingIntent(this, true);
            }
        }
    }
}
