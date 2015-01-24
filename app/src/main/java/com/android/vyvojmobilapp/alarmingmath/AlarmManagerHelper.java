package com.android.vyvojmobilapp.alarmingmath;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

/**
 * Created by honza on 12/15/14.
 */
public class AlarmManagerHelper extends BroadcastReceiver{
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TIMEHOUR = "timeHour";
    public static final String TIMEMINUTE = "timeMinute";
    public static final String TONE = "alarmTone";

    private static String TAG = AlarmManagerHelper.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // tohle tu je jen tak, aspon myslim
        startAlarmPendingIntent(context);
        // tady prijme budik ktery zrovna zvoni
        //je potreba zajistit wakelock, jinak se muze stat, ze se aktivita AlarmResponse vubec nespusti... wakelock releasuje AlarmResponse (!important)
//        WakeLocker.acquire(context);
//
//        // spusti obrazovku ze zvoni budik
//
//        Intent responseIntent = new Intent(context, AlarmResponse.class);
//        responseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        responseIntent.putExtras(intent.getExtras());
//
//        String gotIt = ((Alarm)intent.getParcelableExtra(Alarm.ALARM_FLAG)).toString();
//        Toast.makeText(context, gotIt, Toast.LENGTH_LONG).show();
//
//        context.startActivity(responseIntent);
    }

    public static void startAlarmPendingIntent(Context context) {
        cancelAlarmPendingIntents(context);
        // Honza:
        // neni nic moc narocnyho to nacist znovu,
        // kdyz tak predelat databazi na singleton
        AlarmDatabase alarmDatabase = new AlarmDatabase(context);
        List<Alarm> alarms = alarmDatabase.getAlarms();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        if (alarms == null) {
            return;
        }

        ALARM_LOOP:
        for (Alarm alarm : alarms) {
            if (!alarm.isActive()) {
                continue;
            }

            Calendar cal = Calendar.getInstance();
            // honza: Zatim nastavujeme jen budiky na stejny den,
            // ktere budou pozdeji v ten den
            // TODO: pridat funkcnost pro budiky v ruznych dnech
            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            boolean alarmStarted = false;

            DayRecorder days = alarm.getDays();

            final int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            final int curMin = Calendar.getInstance().get(Calendar.MINUTE);

            cal.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            cal.set(Calendar.MINUTE, alarm.getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            if (alarm.getHour() >= curHour &&
                    alarm.getMinute() > curMin &&
                    days.isDaySet(currentDay - 1)) {

                cal.set(Calendar.DAY_OF_WEEK, currentDay);
                PendingIntent sender = createPendingIntent(context, alarm);
                // naplanuje budik na spravnej cas
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
                continue ALARM_LOOP;
            }


            for (int i = currentDay + 1; i <= Calendar.SATURDAY; i++) {
                if (days.isDaySet(i - 1)) {

                    cal.set(Calendar.DAY_OF_WEEK, i);
                    PendingIntent sender = createPendingIntent(context, alarm);
                    // naplanuje budik na spravnej cas
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

                    continue ALARM_LOOP;
                }
            }

            for (int i = Calendar.SUNDAY; i <= currentDay; ++i) {
                if (days.isDaySet(i-1)) {
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                    PendingIntent sender = createPendingIntent(context, alarm);
                    // naplanuje budik na spravnej cas
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
                    continue ALARM_LOOP;
                }
            }
        }
    }

    public static void cancelAlarmPendingIntents(Context context) {
        // zrusi vsechny aktivni alarmy - pending intenty
        AlarmDatabase alarmDatabase = new AlarmDatabase(context);
        List<Alarm> alarms = alarmDatabase.getAlarms();

        if (alarms == null) {
            return;
        }

        for (Alarm alarm : alarms) {
            if (!alarm.isActive()) {
               continue;
            }

            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

            PendingIntent pendingIntent = createPendingIntent(context, alarm);
            alarmManager.cancel(pendingIntent);
        }
    }

    private static PendingIntent createPendingIntent(Context context, Alarm alarm) {
        // honza: tady to blblo nejakej divnej bug s parceable
        Parcel parcel = Parcel.obtain();
        alarm.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Intent intent = new Intent(context, AlarmService.class);

        intent.putExtra(Alarm.ALARM_FLAG, parcel.marshall());
        // Honza: problematicky pretypovani long na int
        // dulezity je to id, tim je znacenej jeden pending intent a ten flag viz dokumentace
        return PendingIntent.getService(context, (int)alarm.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
