package com.forestoden.locationservices.services;

import android.os.AsyncTask;
import android.util.Log;

import com.forestoden.locationservices.model.Trip;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;

/**
 * Created by ForestOden on 4/16/2017.
 * Project: LocationServices.
 */

public class DeleteTripTask extends AsyncTask<Trip, Integer, String> {

    private static final String TAG = DeleteTripTask.class.getName();

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    @Override
    protected String doInBackground(Trip... trips) {
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
                "http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/trips/";

        for(Trip trip : trips) {
            GenericUrl url =
                    new GenericUrl(urlString + "?id=" + trip.getId());
            try {
                request = requestFactory.buildDeleteRequest(url);
                response = request.execute();
                responseString = response.parseAsString();
                Log.d(TAG, responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return responseString;
    }
}
