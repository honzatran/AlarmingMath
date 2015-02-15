package com.android.vyvojmobilapp.alarmingmath;

import android.view.View;

import junit.framework.Assert;

//listener pro klikani dnu a activite AlarmCreate
public class TgnDayButtonClickListener implements View.OnClickListener {
    DayRecorder recorder;

    // seznam vsech cudliku ktere nam signalizuji den zazvoneni
    static public int [] tgnDaysBtnIds =  {
            R.id.sunday_toggle_btn, R.id.monday_toggle_btn,
            R.id.tuesday_toggle_btn, R.id.wednesday_toggle_btn,
            R.id.thursday_toggle_btn, R.id.friday_toggle_btn,
            R.id.saturday_toggle_btn
    };

    public TgnDayButtonClickListener() {
        this.recorder = new DayRecorder();
    }

    public TgnDayButtonClickListener(DayRecorder recorder) {
        this.recorder = recorder;
    }

    public DayRecorder getRecorder() {
        return recorder;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();


        int i = 0;
        for (; i < tgnDaysBtnIds.length; i++) {
            if (tgnDaysBtnIds[i] == id) {
                break;
            }
        }

        if (id == tgnDaysBtnIds.length)
        {
            Assert.assertTrue("unreachable state in program", false);
        }

        boolean b = recorder.isDaySet(i);
        recorder.setDay(!b, i);
    }

}
