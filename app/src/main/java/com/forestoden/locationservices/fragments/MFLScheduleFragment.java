package com.forestoden.locationservices.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.forestoden.locationservices.R;
import com.forestoden.locationservices.model.Schedule;
import com.forestoden.locationservices.services.GetScheduleTask;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MFLScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MFLScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MFLScheduleFragment extends Fragment
    implements GetScheduleTask.OnAsyncRequestComplete {

    private static final String TAG = MFLScheduleFragment.class.getName();

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeContainer;

    public MFLScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MFLScheduleFragment.
     */
    public static MFLScheduleFragment newInstance() {
        MFLScheduleFragment fragment = new MFLScheduleFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GetScheduleTask(this).execute(new Pair<>(181, 158));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mflschedule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.scheduleSwipeContainer);

        final Fragment f = this;

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Asynchronously get schedules and update text views
                GetScheduleTask getScheduleTask = new GetScheduleTask(f);
                getScheduleTask.execute(new Pair<>(181, 158));
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void asyncResponse(ArrayList<Schedule> response) {
        if (this.isVisible()) {
            if(!response.isEmpty()) {
                ListView listView = (ListView) getActivity().findViewById(R.id.schedule_mfl);
                ArrayAdapter<Schedule> scheduleArrayAdapter =
                        new ArrayAdapter<>(getContext(), R.layout.schedule_item, response);

                listView.setAdapter(scheduleArrayAdapter);
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
