package com.android.vyvojmobilapp.alarmingmath;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link SimpleResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimpleResponseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ALARM = "alarm";


    // TODO: Rename and change types of parameters
    private Alarm alarm;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param alarm Parameter 1.
     * @return A new instance of fragment NoTask.
     */
    // TODO: Rename and change types and number of parameters
    public static SimpleResponseFragment newInstance(Alarm alarm) {
        SimpleResponseFragment fragment = new SimpleResponseFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ALARM, alarm);
        fragment.setArguments(args);
        Log.i("simple", "Started simple response");
        return fragment;
    }

    public SimpleResponseFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_simple_response, container, false);
        TextView tv = (TextView)view.findViewById(R.id.fragment_text);
        tv.setText("Alarm is ringing!\n"
                + alarm.getName()
                + "\n" + alarm.toString()
                + "\nringtone id: " + alarm.getRingtoneUri()
                + "\nsnooze delay: " + alarm.getSnoozeDelay()
                + "\nlength of ringing: " + alarm.getLengthOfRinging()
                + "\nmethod id: " + alarm.getMethodId()
                + "\ndifficulty: " + alarm.getDifficulty()
                + "\nvolume: " + alarm.getVolume()
                + "\nvibrate: " + alarm.isVibrate());
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
