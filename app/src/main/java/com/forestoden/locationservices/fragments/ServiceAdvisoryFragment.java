package com.forestoden.locationservices.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forestoden.locationservices.R;
import com.forestoden.locationservices.services.GetAlertsTask;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiceAdvisoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServiceAdvisoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceAdvisoryFragment extends Fragment
    implements GetAlertsTask.OnAsyncRequestComplete {
    private static final String TAG = ServiceAdvisoryFragment.class.getName();

    private String mflAlert;
    private String mflAdvisory;
    private String bslAlert;
    private String bslAdvisory;

    private OnFragmentInteractionListener mListener;


    private SwipeRefreshLayout swipeContainer;

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

        //Asynchronously get service alerts and update text views
        new GetAlertsTask(this).execute(String.valueOf(R.string.mfl_key),
                String.valueOf(R.string.bsl_key));

        //getActivity().setContentView(R.layout.activity_main);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflateView = inflater.inflate(R.layout.fragment_service_advisory,
               container, false);



        return inflater.inflate(R.layout.fragment_service_advisory,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.advisorySwipeContainer);

        final Fragment f = this;

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Asynchronously get service alerts and update text views
                GetAlertsTask getAlertsTask = new GetAlertsTask(f);
                getAlertsTask.execute(String.valueOf(R.string.mfl_key),
                        String.valueOf(R.string.bsl_key));
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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

    @Override
    public void asyncResponse(ArrayList<String> response) {
        if (response.size() == 4) {
            mflAlert = response.get(0);
            mflAdvisory = response.get(1);
            bslAlert = response.get(2);
            bslAdvisory = response.get(3);

            TextView mflAlertTextView = (TextView)getActivity().findViewById(R.id.alerts_mfl);
            mflAlertTextView.setText(mflAlert + mflAdvisory);
            TextView bslAlertTextView = (TextView)getActivity().findViewById(R.id.alerts_bsl);
            bslAlertTextView.setText(bslAlert + bslAdvisory);
        }
        else {
            TextView mflAlertTextView = (TextView)getActivity().findViewById(R.id.alerts_mfl);
            mflAlertTextView.setText(R.string.advisory_failed);
            TextView bslAlertTextView = (TextView)getActivity().findViewById(R.id.alerts_bsl);
            bslAlertTextView.setText(R.string.advisory_failed);
        }

        swipeContainer.setRefreshing(false);
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
