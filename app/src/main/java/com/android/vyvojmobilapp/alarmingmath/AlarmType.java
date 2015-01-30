package com.android.vyvojmobilapp.alarmingmath;

/**
 * Created by honza on 1/29/15.
 */
public enum AlarmType {
    REPEATING(0), ONESHOT(1), SNOOZE(2);

    private final int id;

    AlarmType(int id) {
        this.id = id;
    }

    public int convert() {
        return  id;
    }

    static AlarmType getEnum(int id) {
        switch (id)  {
            case 0:
                return REPEATING;
            case 1:
                return ONESHOT;
            case 2:
                return SNOOZE;
            default:
                throw new UnsupportedOperationException("no id for enum");
        }
    }


}
