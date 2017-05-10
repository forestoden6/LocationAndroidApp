package com.forestoden.locationservices.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * {@link MFLServiceAdvisoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MFLServiceAdvisoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MFLServiceAdvisoryFragment extends Fragment
        implements GetAlertsTask.OnAsyncRequestComplete {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = MFLServiceAdvisoryFragment.class.getName();

    private String mflAlert;
    private String mflAdvisory;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeContainer;


    public MFLServiceAdvisoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MFLServiceAdvisoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MFLServiceAdvisoryFragment newInstance(String param1, String param2) {
        MFLServiceAdvisoryFragment fragment = new MFLServiceAdvisoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GetAlertsTask(this).execute(getResources().getString(R.string.mfl_key));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mflservice_advisory, container, false);
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
                getAlertsTask.execute(getResources().getString(R.string.mfl_key));
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
    public void asyncResponse(ArrayList<String> response) {
        if (this.isVisible()) {
            if (response.size() == 2) {
                mflAlert = response.get(0);
                mflAdvisory = response.get(1);

                TextView mflAlertTextView = (TextView)getActivity().findViewById(R.id.alerts_mfl);
                if (!mflAlert.trim().isEmpty() || !mflAdvisory.trim().isEmpty()) {
                    mflAlertTextView.setText(mflAlert + mflAdvisory);
                } else {
                    mflAlertTextView.setText(getResources().getString(R.string.no_alerts));
                }
            }
            else {
                TextView mflAlertTextView = (TextView)getActivity().findViewById(R.id.alerts_mfl);
                mflAlertTextView.setText(R.string.advisory_failed);
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
