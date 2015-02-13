package com.android.vyvojmobilapp.alarmingmath;

/**
 * Pomocna trida pro ulozeni QR nebo barcode scanu
 * Created by Petr on 29.1.2015.
 */
public class QR {
    private String hint;
    private String code;

    /**
     * Konstruktor.
     * @param hint Textovy hint, ktery se zobrazi pri zvoneni budiku a identifikuje predmet, ktery ma byt naskenovan (napr. "zubni pasta")
     * @param code Textová hodnota scanu.
     */
    public QR(String hint, String code){
        this.hint = hint;
        this.code = code;
    }

    /**
     *
     * @return Textovy hint pro prislusny scan.
     */
    public String getHint(){
        return hint;
    }

    /**
     *
     * @return Textova hodnota prislusneho scanu.
     */
    public String getCode(){
        return code;
    }

    @Override
    public String toString() {
        return hint;
    }
}
