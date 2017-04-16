package com.forestoden.locationservices.services;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.forestoden.locationservices.model.Station;
import com.forestoden.locationservices.model.Trip;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.forestoden.locationservices.globals.Constants.StationIDMap;
import static com.forestoden.locationservices.globals.Constants.StationMap;

/**
 * Created by ForestOden on 3/5/2017.
 * Project: LocationServices.
 */

public class GetTripsTask extends AsyncTask<String, Integer, ArrayList<Trip>> {

    private static final String TAG = GetTripsTask.class.getName();

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    OnAsyncRequestComplete caller;

    public GetTripsTask(Fragment f) { caller = (OnAsyncRequestComplete) f; }

    @Override
    protected ArrayList<Trip> doInBackground(String... strings) {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest httpRequest) throws IOException {
                        httpRequest.getHeaders().setContentType("application/json");
                    }
                });

        HttpRequest request;
        HttpResponse response;
        ArrayList<Trip> trips = new ArrayList<>();

        String urlString =
                        "http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/trips/";

        for(String s : strings) {
            try {
                GenericUrl url =
                        new GenericUrl(urlString + "?" + s);
                request = requestFactory.buildGetRequest(url);
                response = request.execute();
                String responseString = response.parseAsString();

                //Log.d(TAG, responseString);

                if(responseString != null) {
                    JSONArray tripJson = new JSONArray(responseString);
                    if(tripJson.length() > 0) {
                        for(int i = 0; i < tripJson.length(); ++i) {
                            JSONObject tripJsonObject = tripJson.getJSONObject(i);
                            int id = Integer.parseInt((String) tripJsonObject.get("id_trip"));
                            int startID = Integer.parseInt((String) tripJsonObject.get("id_station_origin"));
                            int endID = Integer.parseInt((String) tripJsonObject.get("id_station_destination"));
                            Station start = StationMap.get(StationIDMap.get(startID));
                            //Log.d(TAG, start.getName());
                            Station end = StationMap.get(StationIDMap.get(endID));
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date startDate = dateFormat.parse((String)tripJsonObject.get("time_departure"));
                            Date endDate = dateFormat.parse((String)tripJsonObject.get("time_arrival"));
                            trips.add(new Trip(start, end, startDate, endDate, id));
                        }
                    }
                }
            } catch (ParseException | JSONException | IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to parse Trip JSON");
            }
        }
        return trips;
    }

    protected void onPostExecute(ArrayList<Trip> trips) { caller.asyncTripResponse(trips); }

    // Interface to be implemented by calling fragment/activity
    public interface OnAsyncRequestComplete {
        void asyncTripResponse(ArrayList<Trip> response);
    }
}
