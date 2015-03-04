package com.vyvojmobilapp.alarmingmath.response.simple;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vyvojmobilapp.alarmingmath.alarm.Alarm;
import com.vyvojmobilapp.alarmingmath.R;


// fragment pro zakladni odpoved na budik
public class SimpleResponseFragment extends Fragment {
    private static final String ARG_ALARM = "alarm";

    private Alarm alarm;

    /**
     * K vytvoreni nove instance tohoto fragmentu za pouziti pripravenych parametru.
     *
     * @param alarm Budik, ktery prave zvoni.
     * @return Nova instance fragmentu SimpleFragment.
     */
    public static SimpleResponseFragment newInstance(Alarm alarm) {
        SimpleResponseFragment fragment = new SimpleResponseFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ALARM, alarm);
        fragment.setArguments(args);
        Log.i("simple", "Started simple response");
        return fragment;
    }

    public SimpleResponseFragment() {
        // je potreba prazdny public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            alarm = getArguments().getParcelable(ARG_ALARM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_simple_response, container, false);

        // zobrazit pri buzeni chci jen nazev a cas:
        TextView nameTV = (TextView)view.findViewById(R.id.simple_response_name);
        if (alarm.getName() != null)      // nastavit nazev pouze v pripade, ze je nejaky zadan
            nameTV.setText(alarm.getName());

        TextView timeTV = (TextView)view.findViewById(R.id.simple_response_time);
        timeTV.setText(alarm.toString());

//        TextView tv = (TextView)view.findViewById(R.id.fragment_text);
//        tv.setText("Alarm is ringing!\n"
//                + alarm.getName()
//                + "\n" + alarm.toString()
//                + "\nringtone id: " + alarm.getRingtoneUri()
//                + "\nsnooze delay: " + alarm.getSnoozeDelay()
//                + "\nlength of ringing: " + alarm.getLengthOfRinging()
//                + "\nmethod id: " + alarm.getMethodId()
//                + "\ndifficulty: " + alarm.getDifficulty()
//                + "\nvolume: " + alarm.getVolume()
//                + "\nvibrate: " + alarm.isVibrate());
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
