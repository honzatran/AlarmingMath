package com.vyvojmobilapp.alarmingmath;

import com.vyvojmobilapp.alarmingmath.alarm.Alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * nova trida kompozice arraylistu a databaze
 */
public class AlarmContainer extends ArrayList<Alarm> {

    public AlarmContainer() {
        List<Alarm> alarms = Alarm.getAlarms();

        if (alarms == null) {
            return;
        }

        for (Alarm alarm : alarms) {
            // pridavame jenom budiky, ktery nevznikly jako odlozeny
            if (!alarm.isSnoozingAlarm()) {
                // volame funkci predka abychom nedavali duplikatne furt do databaze
                super.add(alarm);
            }
        }
    }

    @Override
    public boolean add(Alarm object) {
        //long id =
                Alarm.addAlarm(object);
        //object.setId(id);
        return super.add(object);
    }

    @Override
    public boolean remove(Object object) {
        Alarm alarm = (Alarm) object;
        Alarm.deleteAlarm(alarm.getId());
        return super.remove(object);
    }

    @Override
    public void clear() {
        Alarm.deleteAll();
        super.clear();
    }
}
