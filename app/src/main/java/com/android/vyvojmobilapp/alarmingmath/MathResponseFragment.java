package com.android.vyvojmobilapp.alarmingmath;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MathResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MathResponseFragment
        extends Fragment
        implements View.OnClickListener {

    private static final String ARG_ALARM = "alarm";

    private Alarm alarm;
    private MathExpression mathExpression = new MathExpression();       // priklad v ruznych podobach (infix, postfix, vysledek)
    private View _rootView;

    private OnMathFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param alarm
     * @return A new instance of fragment MathResponseFragment.
     */
    public static MathResponseFragment newInstance(Alarm alarm) {
        MathResponseFragment fragment = new MathResponseFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ALARM, alarm);
        fragment.setArguments(args);
        Log.i("math", "Started math response");
        return fragment;
    }

    public MathResponseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            alarm = getArguments().getParcelable(ARG_ALARM);
            // vygenerovani prikladu
            mathExpression.setDifficulty(alarm.getDifficulty());
            mathExpression.generateExpression();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _rootView = inflater.inflate(R.layout.fragment_math_response, container, false);

        // zakladni info budiku
        TextView nameTV = (TextView)_rootView.findViewById(R.id.math_response_alarmName);
        if (alarm.getName() != null)      // nastavit nazev pouze v pripade, ze je nejaky zadan
            nameTV.setText(alarm.getName());
        TextView timeTV = (TextView)_rootView.findViewById(R.id.math_response_time);
        timeTV.setText(alarm.toString());

        // zobrazeni prikladu
        TextView tv = (TextView)_rootView.findViewById(R.id.math_example);
        tv.setText(mathExpression.exprInfix + " = ");

        Button b = (Button)_rootView.findViewById(R.id.math_check_result_button);
        b.setOnClickListener(this);

        return _rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMathFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMathFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.math_check_result_button:
                checkResult();
                break;
        }
    }

    public void checkResult() {
        // vysledek od uzivatele
        EditText et = (EditText)_rootView.findViewById(R.id.math_result);
        String userResult = et.getText().toString();

        Log.i("math", "vysledek od uzivatele: " + userResult);
        Log.i("math", "ocekavany vysledek: " + mathExpression.result);

        if (userResult != null && userResult.equals(mathExpression.result)) {        // ok
            Log.i("math", "correct answer");
            ((AlarmResponse)getActivity()).dismissAlarm(getView());
            Toast toast = Toast.makeText(getActivity(), "Jsi vzhuru!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {      // spatny vysledek
            Log.i("math", "wrong answer: ");
            et.setText("");     // vyprazdnit vysledek
            Toast toast = Toast.makeText(getActivity(), "Wrong answer!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMathFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}