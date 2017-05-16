package com.forestoden.locationservices.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forestoden.locationservices.R;
import com.forestoden.locationservices.model.Prediction;
import com.forestoden.locationservices.model.PredictionRequest;
import com.forestoden.locationservices.model.Trip;
import com.forestoden.locationservices.services.GetAlertsTask;
import com.forestoden.locationservices.services.GetPredictionTask;
import com.forestoden.locationservices.services.GetTripsTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    GetTripsTask.OnAsyncRequestComplete,
    GetPredictionTask.OnAsyncRequestComplete {

    private static final String TAG = HomeFragment.class.getName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeContainer;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //GetPrediction
        double lat = 39.957167;
        double lon = -75.201836;

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String now = dateFormat.format(date);

        PredictionRequest request = new PredictionRequest(lat, lon, UDID, now, dayOfWeek);
        new GetPredictionTask(this).execute(request);
        //Get Alerts
        new GetAlertsTask(this).execute(getResources().getString(R.string.mfl_key),
                getResources().getString(R.string.bsl_key));
        //Get Past Trips
        new GetTripsTask(this).execute("udid="+UDID);
        
        View inflatedView = inflater.inflate(R.layout.fragment_home, container, false);

        CardView predictionCard = (CardView) inflatedView.findViewById(R.id.prediction_card);
        predictionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PredictionFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out,
                        R.anim.left_in, R.anim.right_out);

                fragmentTransaction.replace(R.id.fragment_container, fragment)
                        .addToBackStack("prediction");
                fragmentTransaction.commit();


                ActionBar mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

                mActionBar.setTitle(getResources().getString(R.string.prediction));
                mActionBar.setDisplayHomeAsUpEnabled(true);
            }
        });

        CardView alertsCard = (CardView) inflatedView.findViewById(R.id.alerts_card);
        alertsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ServiceAdvisoryFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out,
                        R.anim.left_in, R.anim.right_out);

                fragmentTransaction.replace(R.id.fragment_container, fragment)
                        .addToBackStack("alerts");
                fragmentTransaction.commit();

                ActionBar mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

                mActionBar.setTitle(getResources().getString(R.string.alerts));
                mActionBar.setDisplayHomeAsUpEnabled(true);
            }
        });

        CardView pastTripsCard = (CardView) inflatedView.findViewById(R.id.past_trips_card);
        pastTripsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PastTripsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out,
                        R.anim.left_in, R.anim.right_out);

                fragmentTransaction.replace(R.id.fragment_container, fragment)
                        .addToBackStack("past_trips");
                fragmentTransaction.commit();


                ActionBar mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

                mActionBar.setTitle(getResources().getString(R.string.past_trips));
                mActionBar.setDisplayHomeAsUpEnabled(true);
            }
        });


        TextView predictionText = (TextView)inflatedView.findViewById(R.id.prediction);
        predictionText.setText("Prediction will be displayed here.");

        ActionBar mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        mActionBar.setTitle(getResources().getString(R.string.title_activity_home));

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.homeSwipeContainer);

        final Fragment f = this;

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //GetPrediction
                double lat = 39.957167;
                double lon = -75.201836;

                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String now = dateFormat.format(date);

                PredictionRequest request = new PredictionRequest(lat, lon, UDID, now, dayOfWeek);
                new GetPredictionTask(f).execute(request);
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
                    if (!mflAlert.trim().isEmpty() || !bslAlert.trim().isEmpty()) {
                        alertTextView.setText(mflAlert + bslAlert);
                    } else {
                        alertTextView.setText(getResources().getString(R.string.no_alerts));
                    }
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
                /*String tripString = t.getStart().getName() + " " +
                        t.getStartTime().toString() + " \n" +
                        t.getEnd().getName() + " " +
                        t.getEndTime().toString();*/
                String tripString = t.toString();
                TextView tripTextView = (TextView)getActivity().findViewById(R.id.past_trips);
                tripTextView.setText(tripString);
            }
            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void asyncResponse(Prediction prediction) {
        if(this.isVisible()) {
            if(prediction != null) {
                TextView predictionTextView = (TextView)getActivity().findViewById(R.id.prediction);
                predictionTextView.setText("Prediction: " + prediction.getDestinationStation().getName());

                TextView predictionNearest = (TextView)getActivity().findViewById(R.id.prediction_nearest);
                predictionNearest.setText("Nearest station: " + prediction.getNearestStation().getName());

                TextView predictionNextTrain = (TextView)getActivity().findViewById(R.id.prediction_next_train);
                predictionNextTrain.setText("Next train arrives at " + prediction.getNextTrain());

                TextView predictionETA = (TextView)getActivity().findViewById(R.id.prediction_eta);
                predictionETA.setText("Your estimated time of arrival is " + prediction.getEta());

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
        void onFragmentInteraction(Uri uri);
    }
}
