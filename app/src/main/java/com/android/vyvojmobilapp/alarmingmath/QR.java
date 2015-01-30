package com.android.vyvojmobilapp.alarmingmath;

/**
 * Created by Petr on 29.1.2015.
 */
public class QR {
    private String hint;
    private String code;
    public QR(String hint, String code){
        this.hint = hint;
        this.code = code;
    }
    public String getHint(){
        return hint;
    }
    public String getCode(){
        return code;
    }

    @Override
    public String toString() {
        return hint;
    }
}
