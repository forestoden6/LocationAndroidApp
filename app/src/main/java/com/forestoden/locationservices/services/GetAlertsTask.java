package com.forestoden.locationservices.services;

import android.os.AsyncTask;

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
 * Created by ForestOden on 2/18/2017.
 * Project: LocationServices.
 */

public class GetAlertsTask extends AsyncTask<String, Integer, ArrayList<String>> {

    private static final String TAG = GetAlertsTask.class.getName();

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    GenericUrl url =
            new GenericUrl(
                    "https://6kodfrx8ca.execute-api.us-west-2.amazonaws.com/alerts/Septa_Alerts");

    //TODO: Refactor so this uses onPostExecute and we don't drop frames
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
                alerts.add(response.parseAsString());
                //Log.i(TAG, alert);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return alerts;
    }
}
