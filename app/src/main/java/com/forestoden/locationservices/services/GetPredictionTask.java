package com.forestoden.locationservices.services;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.forestoden.locationservices.model.Prediction;
import com.forestoden.locationservices.model.PredictionRequest;
import com.forestoden.locationservices.model.Schedule;
import com.forestoden.locationservices.model.Station;
import com.google.api.client.http.ByteArrayContent;
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
import java.util.Calendar;
import java.util.Date;

import static com.forestoden.locationservices.globals.Constants.StationIDMap;

/**
 * Created by ForestOden on 4/9/2017.
 * Project: LocationServices.
 */

public class GetPredictionTask extends AsyncTask<PredictionRequest, Integer, Prediction>{

    private static final String TAG = GetPredictionTask.class.getName();

    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private String stationUrlString =
            "http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/stations/nearest/";

    private GenericUrl url =
            new GenericUrl("https://6kodfrx8ca.execute-api.us-west-2.amazonaws.com/prod/Prediction_Call");

    OnAsyncRequestComplete caller;

    public GetPredictionTask(Fragment f) {
        caller  = (OnAsyncRequestComplete) f;
    }

    @Override
    protected Prediction doInBackground(PredictionRequest... params) {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest httpRequest) throws IOException {
                        httpRequest.getHeaders().setContentType("application/json");
                    }
                });

        HttpRequest request;
        HttpResponse response;
        //Prediction prediction; //= new Prediction(StationIDMap.get(173), StationIDMap.get(173), new Date(), new Date());

        for(PredictionRequest predictionRequest : params) {
            //get nearest station
            stationUrlString = stationUrlString
                    + "?lat=" + predictionRequest.getLat() + "&lon=" + predictionRequest.getLon()
                    + "&radius=9999";
            //Log.d(TAG, stationUrlString);
            int nearestStation = 0;
            GenericUrl stationUrl = new GenericUrl(stationUrlString);
            try {
                request = requestFactory.buildGetRequest(stationUrl);
                response = request.execute();
                String responseString = response.parseAsString();
                //parse json to get first station
                //Log.d(TAG, responseString);
                JSONArray stationsJson = new JSONArray(responseString);
                JSONObject stationJsonObject = stationsJson.getJSONObject(0);
                nearestStation = Integer.valueOf((String)stationJsonObject.get("id_station"));
            } catch (JSONException|IOException e) {
                e.printStackTrace();
            }
            //prediction request to json
            String requestJSON = "{\"key1\":\"" + predictionRequest.getUdid()
                    + "\", \"key2\":\"" + predictionRequest.getDayOfWeek()
                    + "\", \"key3\":\"" + predictionRequest.getTime()
                    + "\", \"key4\":\"" + nearestStation + "\"}";

            //Log.d(TAG, requestJSON);
            //request
            String responseString = null;
            try {
                request = requestFactory.buildPostRequest(url,
                        ByteArrayContent.fromString(null, requestJSON));
                response = request.execute();
                responseString = response.parseAsString();
                //Log.d(TAG, responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //create prediction
            Station predictedStation = StationIDMap.get(Integer.valueOf(responseString));

            //get schedules from here to there
            Schedule latestSchedule = getNextSchedule(nearestStation, predictedStation.getID());

            return new Prediction(predictedStation, StationIDMap.get(nearestStation),
                    latestSchedule.getDeparture(), latestSchedule.getArrival());
            //return it
        }

        return null;
    }

    private Schedule getNextSchedule(int origin, int destination) {
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

            if(responseString != null) {
                JSONArray schedulesJson = new JSONArray(responseString);
                JSONObject latestSchedule = (JSONObject) schedulesJson.get(0);

                return new Schedule((String)latestSchedule.get("Departure"),
                        (String)latestSchedule.get("Arrival"));
            }
            //Log.d(TAG, responseString);
        } catch (JSONException|IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(Prediction prediction) {
        caller.asyncResponse(prediction);
    }

    // Interface to be implemented by calling fragment/activity
    public interface OnAsyncRequestComplete {
        void asyncResponse(Prediction prediction);
    }
}
