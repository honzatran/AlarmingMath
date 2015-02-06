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
        startAlarmPendingIntent(context, false);
    }

    /**
     * nacte vsechny budiky v dtb, potom prerusi vsechny co jsou
     * naplanovane a potom vsechny budiky znovu naplanuje
     * @param context context aplikace
     * @param skipCancel flag, jestli je nastaven na true tak nebudem
     *                   rusit vsechny budiky a rovnou je nastartujeme,
     *                   jestli false tak se nejdriv zrusi a pote nastartuji
     */
    public static void startAlarmPendingIntent(Context context, boolean skipCancel) {
        // Honza:
        // zrusime vsechny budiky v dtb kdyz je treba
        if (!skipCancel)
        {
            cancelAlarmPendingIntents(context);
        }
        // nacteme budiky z dtb
        AlarmDatabase alarmDatabase = new AlarmDatabase(context);
        List<Alarm> alarms = alarmDatabase.getAlarms();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        if (alarms == null) {
            return;
        }

        // naplanujeme vsechny budiky
        // kazdej aktivni budik je naplanovanej vzdy na datum nejblizsi zvoneni, budiky navic
        // planujeme jen jeden tyden dopredu od doby planovani
        // pro zazvoneni se pouzivaji pending intenty, ty jsou intenty ktery se spusti po nejake
        // dobe, navic v AlarmManageru(sluzba OS) jsou tyhle pending intenty kodovany podle nejakyho
        // klice, jako klic jsem pouzil id budiku v databazi

        ALARM_LOOP:
        for (Alarm alarm : alarms) {

            if (!alarm.isActive()) {
                continue;
            }

            Calendar cal = Calendar.getInstance();
            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

            DayRecorder days = alarm.getDays();

            final int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            final int curMin = Calendar.getInstance().get(Calendar.MINUTE);

            // nastavime v calendari cas zazvoneni budiku
            cal.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            cal.set(Calendar.MINUTE, alarm.getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // nejdriv zkontrolujeme jestli budik bude zvonit do konce dne
            if (alarm.getHour() >= curHour &&
                    alarm.getMinute() > curMin &&
                    days.isDaySet(currentDay - 1)) {

                cal.set(Calendar.DAY_OF_WEEK, currentDay);
                PendingIntent sender = createPendingIntent(context, alarm);
                // naplanuje budik na spravnej cas
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
                // budik naplanovan pokracujem dalsim budikem
                continue ALARM_LOOP;
            }


            // to same akorat ted kontrolujeme jestli budik byl naplanovan do konce tydne(soboty)
            // to je furt ten samy tyden jako ma Calender.getInstance();
            for (int i = currentDay + 1; i <= Calendar.SATURDAY; i++) {
                if (days.isDaySet(i - 1)) {

                    cal.set(Calendar.DAY_OF_WEEK, i);
                    PendingIntent sender = createPendingIntent(context, alarm);
                    // naplanuje budik na spravnej cas
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

                    continue ALARM_LOOP;
                }
            }

            // budik nebyl naplanovan do soboty, tedy musi se prelit do dalsiho tydne
            // Calender.getInstance ma tyden o 1 mensi
            for (int i = Calendar.SUNDAY; i <= currentDay; ++i) {
                if (days.isDaySet(i-1)) {
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    // incrementujeme polozku tyden
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                    PendingIntent sender = createPendingIntent(context, alarm);
                    // naplanuje budik na spravnej cas
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
                    continue ALARM_LOOP;
                }
            }
        }
    }

    /**
     * prerusi vsechny aktivni budiky
     * @param context context aplikace
     */
    public static void cancelAlarmPendingIntents(Context context) {
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
        Parcel parcel = alarm.createParcel();

        Intent intent = new Intent(context, AlarmService.class);

        intent.putExtra(Alarm.ALARM_FLAG, parcel.marshall());
        return PendingIntent.getService(context, (int)alarm.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
