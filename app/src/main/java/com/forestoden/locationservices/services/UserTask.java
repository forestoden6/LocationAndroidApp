package com.forestoden.locationservices.services;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;

/**
 * Created by ForestOden on 2/25/2017.
 * Project: LocationServices.
 */

public class UserTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = UserTask.class.getName();

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();


    private GenericUrl url =
            new GenericUrl(
            "http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/users.php");

    @Override
    protected String doInBackground(String... strings) {
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

        for(String s : strings) {
            try {
                request = requestFactory.buildPostRequest(url,
                        ByteArrayContent.fromString("application/x-www-form-urlencoded",
                                s));

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
