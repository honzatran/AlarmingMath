package com.android.vyvojmobilapp.alarmingmath;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by honza on 1/22/15.
 * keeps track of actived days
 */
public class DayRecorder implements Parcelable {
    // order of bit in mask,
    // Su, Mo, Tu, We, Th, Fr, Sa, not used
    // 0, 1, 2 ...
    byte mask;

    static public int [] tgnButtleIds =  {
            R.id.sunday_toggle_btn, R.id.monday_toggle_btn,
            R.id.tuesday_toggle_btn, R.id.wednesday_toggle_btn,
            R.id.thursday_toggle_btn, R.id.friday_toggle_btn,
            R.id.saturday_toggle_btn
    };

    public DayRecorder() {
        this.mask = 0;
    }

    public DayRecorder(byte mask) {
        this.mask = mask;
    }

    void setDay(boolean b, int i) {
        if (b) {
            mask |= (1 << i);
        } else {
            mask &= ~(1 << i);
        }
    }

    boolean isDaySet(int i) {
        int tmp = 1 << i;
        return ((mask & tmp) != 0);
    }

    public byte getMask() {
        return mask;
    }

    protected DayRecorder(Parcel in) {
        mask = in.readByte();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(mask);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DayRecorder> CREATOR = new Parcelable.Creator<DayRecorder>() {
        @Override
        public DayRecorder createFromParcel(Parcel in) {
            return new DayRecorder(in);
        }

        @Override
        public DayRecorder[] newArray(int size) {
            return new DayRecorder[size];
        }
    };
}

