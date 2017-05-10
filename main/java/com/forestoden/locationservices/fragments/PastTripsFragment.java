package com.forestoden.locationservices.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forestoden.locationservices.R;
import com.forestoden.locationservices.model.Trip;
import com.forestoden.locationservices.model.TripAdapter;
import com.forestoden.locationservices.services.GetTripsTask;

import java.util.ArrayList;

import static com.forestoden.locationservices.globals.Constants.UDID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PastTripsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PastTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastTripsFragment extends Fragment
    implements GetTripsTask.OnAsyncRequestComplete,
        View.OnClickListener {

    private static final String TAG = PastTripsFragment.class.getName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeContainer;

    public PastTripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PastTripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PastTripsFragment newInstance() {
        PastTripsFragment fragment = new PastTripsFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GetTripsTask(this).execute("udid="+UDID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_past_trips, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.past_trips_recycler);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.tripsSwipeContainer);

        final Fragment f = this;

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Asynchronously get Past Trips  and update text views
                GetTripsTask getTripsTask = new GetTripsTask(f);
                getTripsTask.execute("udid="+UDID);
                Log.d(TAG, UDID);
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
    public void asyncTripResponse(ArrayList<Trip> response) {
        /*Trip[] trips = response.toArray(new Trip[response.size()]);
        ArrayAdapter<Trip> adapter = new ArrayAdapter<Trip>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, trips);*/

        Fragment f = this;

        mAdapter = new TripAdapter(response, f);
        mRecyclerView.setAdapter(mAdapter);

        /*ListView listView = (ListView) getActivity().findViewById(R.id.past_trips_list);
        listView.setAdapter(adapter);*/

        /*View rootView = getView();

        ImageButton menu = (ImageButton) rootView.findViewById(R.id.trip_menu);
        menu.setOnClickListener(this);*/

        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity().getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
    }

    public void showPopup(View v) {

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
