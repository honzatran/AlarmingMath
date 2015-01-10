package com.android.vyvojmobilapp.alarmingmath;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// dodelat mazani budiku, plus budiky v ruznych dnech a nejakej ringtone

public class AlarmMainActivity extends ActionBarActivity {
    List<Alarm> alarms;
    ListView alarmListView;
    AlarmDatabase alarmDatabase;
    ArrayAdapter<Alarm> alarmArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmDatabase = new AlarmDatabase(this);

        //--debugging purposes
        //getApplicationContext().deleteDatabase("alarmDatabase.db");

        alarms = alarmDatabase.getAlarms();
        if (alarms == null) {
            alarms = new ArrayList<>();
        }



        alarmArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                alarms);

        alarmListView = (ListView)findViewById(R.id.alarm_list);
        alarmListView.setAdapter(alarmArrayAdapter);

        //registrujeme tridy AlarmMainActivity jako obsluznou pro alarmListView (metody jsou nize)
        registerForContextMenu(alarmListView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle bundles = intent.getExtras();
        if (intent.hasExtra(Alarm.ALARM_FLAG))
        {
            // vratil jsem se obrazovky vytvoreni budiku
            // pridam novy do databaze
            Alarm newAlarm = intent.getExtras().getParcelable(Alarm.ALARM_FLAG);

            long id = alarmDatabase.addAlarm(newAlarm);
            newAlarm.setId(id);
            alarms.add(newAlarm);

            // spustim ho
            if(newAlarm.isActive()){
                setAlarm(newAlarm);
            }

        }
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
                                "\nVibrate: "+alarm.isVibrate()
                                , Toast.LENGTH_LONG).show();
                break;
            case 2: //delete
                alarmDatabase.deleteAlarm(alarm.getId());
                alarms.remove(alarm); //mozna bych tohle pole nejak provazal s databazi
                alarmArrayAdapter.notifyDataSetChanged();  // important
                Toast.makeText(this, "Budík v čase "+alarm.toString()+" smazán.", Toast.LENGTH_SHORT).show();
                break;
            default:

        }

        return true;
    }

    private void setAlarm(Alarm newAlarm) {
        Calendar cal = Calendar.getInstance();

        if (newAlarm.getHour() >= cal.get(Calendar.HOUR_OF_DAY) &&
                newAlarm.getMinute() > cal.get(Calendar.MINUTE)) {
            cal.set(Calendar.HOUR_OF_DAY, newAlarm.getHour());
            cal.set(Calendar.MINUTE, newAlarm.getMinute());
            cal.set(Calendar.SECOND, 0);

        } else {
            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            cal.set(Calendar.DAY_OF_WEEK, (currentDay + 1) % 7);
            cal.set(Calendar.HOUR_OF_DAY, newAlarm.getHour());
            cal.set(Calendar.MINUTE, newAlarm.getMinute());
            cal.set(Calendar.SECOND, 0);
        }

        // nastavi indent
        Intent intent = new Intent(getApplicationContext(), AlarmManagerHelper.class);
        intent.putExtra(Alarm.ALARM_FLAG, newAlarm);

        // indent vydrzi i konec aplikace
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 156,
                intent, 0);

        Context context = getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        // naplanuje budik na spravnej cas
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
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
        Intent intent = new Intent(this, AlarmCreateActivity.class);
        startActivity(intent);
    }
}
