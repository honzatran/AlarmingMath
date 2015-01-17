package com.android.vyvojmobilapp.alarmingmath;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.android.vyvojmobilapp.alarmingmath.QrResponseFragment.OnQrFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QrResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QrResponseFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ALARM = "alarm";


    // TODO: Rename and change types of parameters
    private Alarm alarm;
    private String item;
    private String corr_qr_code;
    private View _rootView;
    private String ringingText = "To stop or snooze the alarm, find and scan the barcode from the following item:";



    private OnQrFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param alarm Parameter 1.
     * @return A new instance of fragment NoTask.
     */
    // TODO: Rename and change types and number of parameters
    public static QrResponseFragment newInstance(Alarm alarm) {
        QrResponseFragment fragment = new QrResponseFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ALARM, alarm);
        fragment.setArguments(args);
        Log.i("qr", "Started qr response");
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            ringingText = savedInstanceState.getString("text");
        }
    }

    public QrResponseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            alarm = getArguments().getParcelable(ARG_ALARM);
            //item = alarm.getQRItem();
            item = "ISIC";
            //corr_qr_code = alarm.getCorrQRCode();
            corr_qr_code = "FKMPHWF";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        _rootView = inflater.inflate(R.layout.fragment_qr_response, container, false);
        // Find and setup subviews

        ((TextView)_rootView.findViewById(R.id.qr_ringing)).setText(ringingText);
        ((TextView)_rootView.findViewById(R.id.qr_item)).setText(item);

        Button b = (Button) _rootView.findViewById(R.id.scan_button);
        b.setOnClickListener(this);

        return _rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnQrFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnQrFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_button:
                scan();
                break;
        }
    }

    public void scan() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setPrompt("Naskenujte kód z předmětu: " + item);
        IntentIntegrator.forFragment(this).initiateScan();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        Log.i("qr", "called onactivityresult");

        getActivity();

        if ((resultCode == Activity.RESULT_OK) && (scanResult != null)) {
            String scanContent = scanResult.getContents();
            String scanFormat = scanResult.getFormatName();
            if(scanContent.equals(corr_qr_code)){
                Log.i("qr", "correct item");
                //findViewById(R.id.buttons).setVisibility(View.VISIBLE);
            }
            else{
                Log.i("qr", "wrong item");
                ((TextView)_rootView.findViewById(R.id.qr_ringing)).setText("Špatný předmět! Zkuste to znova.");
            }

        }
        else{
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
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
    public interface OnQrFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
        ringingText = ((TextView)_rootView.findViewById(R.id.qr_ringing)).getText().toString();
        outState.putString("text", ringingText);
    }

}
