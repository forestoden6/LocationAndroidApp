package com.forestoden.locationservices.services;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

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
 * Created by ForestOden on 2/25/2017.
 * Project: LocationServices.
 */

public class GetAlertsTask extends AsyncTask<String, Void, ArrayList<String>> {

    private static final String TAG = GetAlertsTask.class.getName();

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private GenericUrl url =
            new GenericUrl(
                    "https://6kodfrx8ca.execute-api.us-west-2.amazonaws.com/alerts/Septa_Alerts");


    OnAsyncRequestComplete caller;

    public GetAlertsTask(Fragment f) {
        caller = (OnAsyncRequestComplete) f;
    }

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
                String responseString = response.parseAsString();
                responseString = responseString.replace("\"", "");
                responseString = responseString.replace("\\n", "\n");
                String[] responseSplit = responseString.split("\n",2);
                String advisory = "";
                String alert = "";
                if (responseSplit.length == 2) {
                    alert = responseSplit[0];
                    advisory = responseSplit[1];
                }
                alerts.add(alert);
                alerts.add(advisory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return alerts;
    }

    protected void onPostExecute(ArrayList<String> alerts) {
        caller.asyncResponse(alerts);
    }

    // Interface to be implemented by calling fragment/activity
    public interface OnAsyncRequestComplete {
        void asyncResponse(ArrayList<String> response);
    }
}