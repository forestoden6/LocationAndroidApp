package com.forestoden.locationservices.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forestoden.locationservices.R;
import com.forestoden.locationservices.services.GetAlertsTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiceAdvisoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServiceAdvisoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceAdvisoryFragment extends Fragment {
    private static final String TAG = ServiceAdvisoryFragment.class.getName();

    private String mfl = "{\"key1\":\"mfl\"}";
    private String bsl = "{\"key1\":\"bsl\"}";

    private ArrayList<String> alerts = new ArrayList<>();

    private String mflAlert;
    private String bslAlert;

    private OnFragmentInteractionListener mListener;

    public ServiceAdvisoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ServiceAdvisoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceAdvisoryFragment newInstance() {
        ServiceAdvisoryFragment fragment = new ServiceAdvisoryFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetAlertsTask getAlertsTask  = new GetAlertsTask();
        try {
            alerts = getAlertsTask.execute(mfl, bsl).get();
            mflAlert = alerts.get(0);
            bslAlert = alerts.get(1);
            if(!mflAlert.isEmpty()) {
                mflAlert = mflAlert.replace("\\n", "\n");
                mflAlert = mflAlert.replace("\"", "");
            }

            //bslAlert = getAlertsTask.execute(bsl).get();
            if(!bslAlert.isEmpty()) {
                bslAlert = bslAlert.replace("\\n", "\n");
                bslAlert = bslAlert.replace("\"", "");
            }
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflateView = inflater.inflate(R.layout.fragment_service_advisory,
                container, false);

        //Set Alerts text
        //Log.d(TAG, mflAlert);
        TextView mflAlertTextView = (TextView)inflateView.findViewById(R.id.alerts_mfl);
        mflAlertTextView.setText(mflAlert);
        TextView bslAlertTextView = (TextView)inflateView.findViewById(R.id.alerts_bsl);
        bslAlertTextView.setText(bslAlert);

        //Return inflated view with updated text
        return inflateView;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
