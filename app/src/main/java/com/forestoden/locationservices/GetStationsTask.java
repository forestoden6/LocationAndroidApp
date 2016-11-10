package com.forestoden.locationservices;

import android.os.AsyncTask;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Created by ForestOden on 11/9/2016.
 */

public class GetStationsTask extends AsyncTask<URL, Integer, String> {

    private static final String TAG = GetStationsTask.class.getName();

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();




    public GetStationsTask(){

    }

    @Override
    protected String doInBackground(URL... urls) {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest httpRequest) throws IOException {
                        httpRequest.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });
        //Use the URL passed to the method
        GenericUrl genericStation = new GenericUrl(urls[0]);
        HttpRequest request;
        //String stations = null;
        HttpResponse response;
        String stations = null;
        try {
            request = requestFactory.buildGetRequest(genericStation);
            response = request.execute();
            stations = response.parseAsString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stations;

    }
}
