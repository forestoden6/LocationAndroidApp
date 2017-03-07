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
import com.forestoden.locationservices.model.Trip;
import com.forestoden.locationservices.services.GetAlertsTask;
import com.forestoden.locationservices.services.GetTripsTask;

import java.util.ArrayList;

import static com.forestoden.locationservices.globals.Constants.UDID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment
    implements GetAlertsTask.OnAsyncRequestComplete ,
    GetTripsTask.OnAsyncRequestComplete {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeContainer;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(/*String param1, String param2*/) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        new GetAlertsTask(this).execute(getResources().getString(R.string.mfl_key),
                getResources().getString(R.string.bsl_key));
        new GetTripsTask(this).execute("udid="+UDID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_home, container, false);

        TextView predictionText = (TextView)inflatedView.findViewById(R.id.prediction);
        predictionText.setText("Prediction will be displayed here.");
        /*TextView alertText = (TextView)inflatedView.findViewById(R.id.alerts);
        alertText.setText("Not implemented.");
        TextView tripText = (TextView)inflatedView.findViewById(R.id.past_trips);
        tripText.setText("Not implemented.");*/
        // Inflate the layout for this fragment
        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.homeSwipeContainer);

        final Fragment f = this;

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Asynchronously get service alerts and update text views
                GetAlertsTask getAlertsTask = new GetAlertsTask(f);
                getAlertsTask.execute(getResources().getString(R.string.mfl_key),
                        getResources().getString(R.string.bsl_key));
                GetTripsTask getTripsTask = new GetTripsTask(f);
                getTripsTask.execute("udid="+UDID);
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
        if (this.isVisible()) {
            if(response.size() == 4) {
                String mflAlert = response.get(0);
                String bslAlert = response.get(2);

                if (mflAlert.isEmpty() && bslAlert.isEmpty()) {
                    TextView alertTextView = (TextView)getActivity().findViewById(R.id.alerts);
                    alertTextView.setText(R.string.no_alerts);
                } else {
                    TextView alertTextView = (TextView)getActivity().findViewById(R.id.alerts);
                    alertTextView.setText(mflAlert + bslAlert);
                }
            } else {
                TextView alertTextView = (TextView)getActivity().findViewById(R.id.alerts);
                alertTextView.setText(R.string.advisory_failed);
            }

            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void asyncTripResponse(ArrayList<Trip> response) {
        if(this.isVisible()) {
            if(response.size() > 0) {
                Trip t = response.get(0);
                String tripString = t.getStart().getName() + " " +
                        t.getStartTime().toString() + " \n" +
                        t.getEnd().getName() + " " +
                        t.getEndTime().toString();
                TextView tripTextView = (TextView)getActivity().findViewById(R.id.past_trips);
                tripTextView.setText(tripString);
            }
            swipeContainer.setRefreshing(false);
        }
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
