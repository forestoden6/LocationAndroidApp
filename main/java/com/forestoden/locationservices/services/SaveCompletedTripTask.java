package com.forestoden.locationservices.services;

import android.os.AsyncTask;
import android.util.Log;

import com.forestoden.locationservices.model.Station;
import com.forestoden.locationservices.model.Trip;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.forestoden.locationservices.globals.Constants.UDID;

/**
 * Created by ForestOden on 3/5/2017.
 * Project: LocationServices.
 */

public class SaveCompletedTripTask extends AsyncTask<Trip, Integer, String> {

    private static final String TAG = SaveCompletedTripTask.class.getName();

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private GenericUrl url =
            new GenericUrl(
                    "http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/trips/");

    @Override
    protected String doInBackground(Trip... trips) {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest httpRequest) throws IOException {
                        httpRequest.getHeaders().setContentType("application/x-www-form-urlencoded");
                    }
                });

        HttpRequest request;
        HttpResponse response;
        String responseString = "";

        for(Trip t : trips) {
            try {
                Station originStation = t.getStart();
                int origin = originStation.getID();
                Date departDate = t.getStartTime();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String depart = dateFormat.format(departDate);
                Station destinationStation = t.getEnd();
                int destination = destinationStation.getID();
                Date arriveDate = t.getEndTime();
                String arrive = dateFormat.format(arriveDate);

                Calendar c = Calendar.getInstance();
                c.setTime(departDate);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                String s = "udid=" + UDID +
                           "&weekday=" + String.valueOf(dayOfWeek) +
                           "&origin=" + String.valueOf(origin) +
                           "&depart=" + depart +
                           "&destination=" + String.valueOf(destination) +
                           "&arrival=" + arrive;

                Log.d(TAG, s);

                request = requestFactory.buildPostRequest(url,
                        ByteArrayContent.fromString(null, s));
                response = request.execute();
                responseString = response.parseAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return responseString;
    }
}
