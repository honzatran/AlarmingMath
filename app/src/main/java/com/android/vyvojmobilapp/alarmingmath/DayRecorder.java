package com.android.vyvojmobilapp.alarmingmath;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by honza on 1/22/15.
 * udrzuje prehled o aktivovanych dnech
 */

/**
 * udrzuje informace o dnech v tydnu v kterych zvoni budik
 */
public class DayRecorder implements Parcelable {
    // zakodovany do bitovy masky, at je to jednoduchy zapsat do databaze
    // dny: Su, Mo, Tu, We, Th, Fr, Sa
    // bity: 0, 1, 2, 3, 4, 5, 6
    byte mask;


    public DayRecorder() {
        this.mask = 0;
    }

    public DayRecorder(byte mask) {
        this.mask = mask;
    }

    /**
     * dle b aktivuje nebo deaktivuje dany den
     * 0 Nedele, 1 Pondeli ... 6 Sobota
     * @param b aktivace -> true, deaktivace -> false
     * @param i poradi dne v tydny od, 0 <= i < 7
     */
    void setDay(boolean b, int i) {
        if (b) {
            mask |= (1 << i);
        } else {
            mask &= ~(1 << i);
        }
    }

    /**
     * zjisti jestli je dany den aktivovany
     * 0 Nedele, 1 Pondeli ... 6 Sobota
     * @param i poradi dne v tydny od, 0 <= i < 7
     * @return true iff je den i aktivovany jinak false
     */
    boolean isDaySet(int i) {
        int tmp = 1 << i;
        return ((mask & tmp) != 0);
    }

    /**
     *
     * @return vrati masku recordu
     */
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

