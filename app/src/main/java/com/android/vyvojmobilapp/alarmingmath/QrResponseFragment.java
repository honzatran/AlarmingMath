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


public class QrResponseFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ALARM = "alarm";
    private Alarm alarm;
    private String item;
    private String corr_qr_code;
    private View _rootView;
    // todo vytahnout z kodu tento retezec nekam mimo
    private String ringingText = "To stop or snooze the alarm, find and scan the barcode from the following item:";



    private OnQrFragmentInteractionListener mListener;

    /**
     * Vytvori novou instanci fragmentu s dodanymi parametry.
     *
     * @param alarm Parametr 1.
     * @return Nova instance fragmentu NoTask.
     */
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
        // public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            alarm = getArguments().getParcelable(ARG_ALARM);
            item = alarm.getQr().getHint();
            //item = "ISIC";
            corr_qr_code = alarm.getQr().getCode();
            Log.i("qr","kod: " + corr_qr_code);
            //corr_qr_code = "FKMPHWF";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_qr_response, container, false);

        // Name and time...
        TextView nameTV = (TextView)_rootView.findViewById(R.id.qr_response_alarmName);
        if (alarm.getName() != null)      // nastavit nazev pouze v pripade, ze je nejaky zadan
            nameTV.setText(alarm.getName());
        TextView timeTV = (TextView)_rootView.findViewById(R.id.qr_response_time);
        timeTV.setText(alarm.toString());
        TextView itemTV = (TextView)_rootView.findViewById(R.id.qr_item);
        itemTV.setText(item);

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
        integrator.setPrompt(getString(R.string.qrScanPrompt) + item);
        IntentIntegrator.forFragment(this).initiateScan();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        Log.i("qr", "called onactivityresult");

        getActivity();

        if ((resultCode == Activity.RESULT_OK) && (scanResult != null)) {
            String scanContent = scanResult.getContents();
            Log.i("qr","vysledek skenu: " + scanContent);

            if(scanContent.equals(corr_qr_code)){
                Log.i("qr", "correct item");
                ((AlarmResponse)getActivity()).dismissAlarm(getView());
            } else {
                Log.i("qr", "wrong item");
                ((TextView)_rootView.findViewById(R.id.qr_ringing)).setText(
                        getString(R.string.qrScanWrong));
            }

        }
        else{
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public interface OnQrFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        ringingText = ((TextView)_rootView.findViewById(R.id.qr_ringing)).getText().toString();
        outState.putString("text", ringingText);
    }

}
