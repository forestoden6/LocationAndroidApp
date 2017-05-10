package com.forestoden.locationservices.services;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;

import com.forestoden.locationservices.model.Schedule;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ForestOden on 4/19/2017.
 * Project: LocationServices.
 */

public class GetScheduleTask extends AsyncTask<Pair<Integer, Integer>, Integer, ArrayList<Schedule>> {

    private static final String TAG = GetScheduleTask.class.getName();

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private OnAsyncRequestComplete caller;

    public GetScheduleTask(Fragment f) {
        caller = (OnAsyncRequestComplete) f;
    }


    @Override
    protected ArrayList<Schedule> doInBackground(Pair<Integer, Integer>... params) {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest httpRequest) throws IOException {
                        httpRequest.getHeaders().setContentType("application/json");
                    }
                });

        HttpRequest request;
        HttpResponse response;
        String responseString = null;

        String urlString =
                "http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/schedules";

        int origin = 0;
        int destination = 0;

        for(Pair<Integer, Integer> param : params) {
            origin = param.first;
            destination = param.second;
        }

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        GenericUrl genericUrl =
                new GenericUrl(urlString + "?origin=" + origin + "&destination=" + destination
                + "&day=" + day);
        //Log.d(TAG, genericUrl.toString());

        try {
            request = requestFactory.buildGetRequest(genericUrl);
            response = request.execute();
            responseString = response.parseAsString();
            //Log.d(TAG, responseString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJSON(responseString);
    }

    private ArrayList<Schedule> parseJSON(String response) {
        ArrayList<Schedule> schedules = new ArrayList<>();

        if(response != null) {
            JSONArray schedulesJson;

            try {
                schedulesJson = new JSONArray(response);
            } catch (JSONException e) {
                e.printStackTrace();
                return schedules;
            }

            if(schedulesJson.length() > 0) {
                for(int i = 0; i < schedulesJson.length(); ++i) {
                    JSONObject schedule = null;
                    try {
                        schedule = (JSONObject) schedulesJson.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return schedules;
                    }

                    String departureTime = null;
                    String arrivalTime = null;

                    try {
                        departureTime = (String)schedule.get("Departure");
                        arrivalTime = (String)schedule.get("Arrival");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    schedules.add(new Schedule(departureTime, arrivalTime));

                }
            }
        }

        return schedules;
    }

    protected void onPostExecute(ArrayList<Schedule> schedules) {
        caller.asyncResponse(schedules);
    }

    // Interface to be implemented by calling fragment/activity
    public interface OnAsyncRequestComplete {
        void asyncResponse(ArrayList<Schedule> response);
    }
}
