package com.android.vyvojmobilapp.alarmingmath;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by honza on 1/10/15.
 * @author Tran Tuan Hiep
 * nova trida kompozice arraylistu a databaze
 */
public class AlarmContainer extends ArrayList<Alarm> {
    AlarmDatabase alarmDatabase;

    public AlarmContainer(AlarmDatabase alarmDatabase) {
        this.alarmDatabase = alarmDatabase;
        List<Alarm> alarms = alarmDatabase.getAlarms();

        if (alarms == null) {
            return;
        }

        for (Alarm alarm : alarms) {
            // pridavame jenom budiky, ktery nevznikly jako odlozeny
            if (!alarm.isOneShot()) {
                // honza: volame funkci predka abychom nedavali duplikatne furt do databaze
                super.add(alarm);
            }
        }
    }

    @Override
    public boolean add(Alarm object) {
        long id = alarmDatabase.addAlarm(object);
        object.setId(id);
        return super.add(object);
    }

    @Override
    public boolean remove(Object object) {
        Alarm alarm = (Alarm) object;
        alarmDatabase.deleteAlarm(alarm.getId());
        return super.remove(object);
    }

    @Override
    public void clear() {
        alarmDatabase.deleteAll();
        super.clear();
    }
}
