package com.android.vyvojmobilapp.alarmingmath;

import android.view.View;

import junit.framework.Assert;

/**
 * Created by honza on 1/22/15.
 * listener pro klikani dnu
 */
public class TgnDayButtonClickListener implements View.OnClickListener {
    DayRecorder recorder;

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

        int[] daysBtnIds = DayRecorder.tgnButtleIds;

        int i = 0;
        for (; i < daysBtnIds.length; i++) {
            if (daysBtnIds[i] == id) {
                break;
            }
        }

        if (id == daysBtnIds.length)
        {
            Assert.assertTrue("unreachable state in program", false);
        }

        boolean b = recorder.isDaySet(i);
        recorder.setDay(!b, i);
    }

}
