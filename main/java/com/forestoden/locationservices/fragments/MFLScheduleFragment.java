package com.forestoden.locationservices.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.forestoden.locationservices.R;
import com.forestoden.locationservices.model.Schedule;
import com.forestoden.locationservices.model.Station;
import com.forestoden.locationservices.services.GetScheduleTask;

import java.util.ArrayList;

import static com.forestoden.locationservices.globals.Constants.StationIDMap;

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

        //new GetScheduleTask(this).execute(new Pair<>(181, 158));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mflschedule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ArrayList<Station> stations = new ArrayList<>();
        ArrayList<String> stationNames = new ArrayList<>();


        stations.addAll(StationIDMap.values());

        for(Station s : stations) {
            stationNames.add(s.getName());
        }

        /*Log.d(TAG, String.valueOf(StationMap.keySet().size()));
        Log.d(TAG, String.valueOf(StationIDMap.keySet().size()));*/

        final ArrayAdapter<Station> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, stations);
        final AutoCompleteTextView originText = (AutoCompleteTextView)
                getActivity().findViewById(R.id.origin_text);
        originText.setAdapter(adapter);
        final AutoCompleteTextView destinationText =
                (AutoCompleteTextView) getActivity().findViewById(R.id.destination_text);
        destinationText.setAdapter(adapter);

        final Fragment f = this;

        Button searchSchedule = (Button) getActivity().findViewById(R.id.schedule_button);
        searchSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin = originText.getText().toString();
                String destination = destinationText.getText().toString();
                Integer originID = isValidStation(origin);
                Integer destinationID = isValidStation(destination);
                /*if (StationMap.get(origin) != null && StationMap.get(destination) != null) {
                    originID = StationMap.get(origin).getID();
                    destinationID = StationMap.get(destination).getID();

                    new GetScheduleTask(f).execute(new Pair<>(originID, destinationID));

                } else {
                    if(StationMap.get(origin) == null) {
                        originText.setError(getResources().getString(R.string.invalid_station));
                    }
                    if(StationMap.get(destination) == null) {
                        destinationText.setError(getResources().getString(R.string.invalid_station));
                    }
                }*/

                if(originID > 0 && destinationID > 0) {
                    Log.d(TAG, String.valueOf(originID) + " " + String.valueOf(destinationID));
                    new GetScheduleTask(f).execute(new Pair<>(originID, destinationID));
                } else {
                    if(originID == -1) {
                        originText.setError(getResources().getString(R.string.invalid_station));
                    }
                    if(destinationID == -1) {
                        destinationText.setError(getResources().getString(R.string.invalid_station));
                    }
                }

            }
        });

    }

    private int isValidStation(String input) {
        for(Station s : StationIDMap.values()) {
            if (s.toString().equals(input)) {
                return s.getID();
            }
        }

        return -1;
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
