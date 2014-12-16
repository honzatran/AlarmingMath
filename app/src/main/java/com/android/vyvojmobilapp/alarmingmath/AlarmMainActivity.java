package com.android.vyvojmobilapp.alarmingmath;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class AlarmMainActivity extends ActionBarActivity {
    List<Alarm> alarms;
    ListView alarmListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // tady bude potreba nejaka databaze sqlite ci co
        alarms = new ArrayList<>();
        alarms.add(new Alarm(14, 13));

        Intent intent = getIntent();
        Bundle bundles = intent.getExtras();
        if (intent.hasExtra(Alarm.HOUR) && intent.hasExtra(Alarm.MINUTES))
        {
            Alarm newAlarm = new Alarm(bundles.getInt(Alarm.HOUR), bundles.getInt(Alarm.MINUTES));
            alarms.add(newAlarm);
            setAlarm(newAlarm);
        }

        ArrayAdapter<Alarm> alarmArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                alarms);

        alarmListView = (ListView)findViewById(R.id.alarm_list);
        alarmListView.setAdapter(alarmArrayAdapter);

    }

    private void setAlarm(Alarm newAlarm) {
        Calendar cal = Calendar.getInstance();
//        int diff = newAlarm.getHour() * 60 + newAlarm.getMinute() - cal.get(Calendar.HOUR_OF_DAY) * 60
//                - cal.get(Calendar.MINUTE);

//        Log.d("diff", String.valueOf(diff));

        if (newAlarm.getHour() >= cal.get(Calendar.HOUR_OF_DAY) &&
                newAlarm.getMinute() > cal.get(Calendar.MINUTE)) {
            cal.set(Calendar.HOUR_OF_DAY, newAlarm.getHour());
            cal.set(Calendar.MINUTE, newAlarm.getMinute());
            cal.set(Calendar.SECOND, 0);

            Intent intent = new Intent(getApplicationContext(), AlarmManagerHelper.class);
            PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 156,
                    intent, 0);

            Context context = getApplicationContext();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
        } else {
            String msg = "not implemented";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
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
