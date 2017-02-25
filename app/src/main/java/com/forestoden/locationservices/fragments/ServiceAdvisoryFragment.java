package com.forestoden.locationservices.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forestoden.locationservices.R;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.util.ArrayList;

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

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private GenericUrl url =
            new GenericUrl(
                    "https://6kodfrx8ca.execute-api.us-west-2.amazonaws.com/alerts/Septa_Alerts");

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
        new GetAlerts().execute(mfl, bsl);

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
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Asynchronously get service alerts and update text views
                new GetAlerts().execute(mfl, bsl);
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

    private class GetAlerts extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            HttpRequestFactory requestFactory =
                    HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest httpRequest) throws IOException {
                            httpRequest.getHeaders().setContentType("application/json");
                        }
                    });

            HttpRequest request;
            HttpResponse response;
            ArrayList<String> alerts = new ArrayList<>();

            for(String s : strings) {
                try {
                    request = requestFactory.buildPostRequest(url,
                            ByteArrayContent.fromString(null, s));
                    response = request.execute();
                    String alert = response.parseAsString();
                    if(!alert.isEmpty()) {
                        alert = alert.replace("\\n", "\n");
                        alert = alert.replace("\"", "");
                    }
                    alerts.add(alert);
                    //Log.i(TAG, alert);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return alerts;
        }

        protected void onPostExecute(ArrayList<String> alerts) {
            mflAlert = alerts.get(0);
            bslAlert = alerts.get(1);

            TextView mflAlertTextView = (TextView)getActivity().findViewById(R.id.alerts_mfl);
            mflAlertTextView.setText(mflAlert);
            TextView bslAlertTextView = (TextView)getActivity().findViewById(R.id.alerts_bsl);
            bslAlertTextView.setText(bslAlert);

            swipeContainer.setRefreshing(false);
        }
    }
}
