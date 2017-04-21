package com.forestoden.locationservices.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.forestoden.locationservices.globals.Constants;
import com.forestoden.locationservices.model.Station;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.forestoden.locationservices.globals.Constants.STATIONS;
import static com.forestoden.locationservices.globals.Constants.StationIDMap;
import static com.forestoden.locationservices.globals.Constants.StationMap;
import static com.forestoden.locationservices.globals.Constants.stationUrl;

/**
 * Created by ForestOden on 4/16/2017.
 * Project: LocationServices.
 */

public class GeofencesOnBootService extends BroadcastReceiver implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GeofencesOnBootService.class.getName();


    protected GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList = new ArrayList<>();

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        try {
            createGeofenceList();
        } catch (MalformedURLException | JSONException e) {
            e.printStackTrace();
        }

        mGoogleApiClient.connect();
    }

    protected void createGeofenceList() throws JSONException, MalformedURLException {
        URL stationUrlObject = new URL(stationUrl);
        //Creates a new thread to get stations asynchronously
        GetStationsTask stationConnection = new GetStationsTask();
        String stations = null;
        try {
            stations = stationConnection.execute(stationUrlObject).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //Parse JSON returned by server and add to list of stations
        if (stations != null) {
            JSONArray stationJson;

            try {
                stationJson = new JSONArray(stations);
            } catch (Throwable t) {
                Log.e(TAG, "Could not parse malformed JSON: " + stations);
                return;
            }

            if (stationJson.length() > 0) {
                for (int i = 0; i < stationJson.length(); i++) {
                    JSONObject stationJsonObject = stationJson.getJSONObject(i);
                    String name = (String) stationJsonObject.get("name_long");
                    double latitude = Double.parseDouble((String) stationJsonObject.get("latitude"));
                    double longitude = Double.parseDouble((String) stationJsonObject.get("longitude"));
                    int id = Integer.parseInt((String) stationJsonObject.get("id_station"));
                    String address = stationJsonObject.getString("address");
                    String line = stationJsonObject.getString("line");
                    Station station = new Station(id, name, address, new LatLng(latitude, longitude), line);
                    STATIONS.add(station);
                    StationMap.put(name, station);
                    StationIDMap.put(id, station);
                }
            } else {
                Log.e(TAG, "Empty JSON returned by server");
                Toast.makeText(mContext, "Error connecting to server", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "No response from server");
            Toast.makeText(mContext, "Could not connect to server", Toast.LENGTH_LONG).show();
        }


        //Create Geofence objects
        //NOTE: Geofences will not be activated here
        for (Station station : STATIONS) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(station.getName())
                    .setCircularRegion(
                            station.getLatLng().latitude,
                            station.getLatLng().longitude,
                            Constants.GEOFENCE_RADIUS_METERS)
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, String.valueOf(mGoogleApiClient.isConnected()));

        try {
            if (!mGeofenceList.isEmpty()) {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                );
            } else {
                Toast.makeText(mContext, "Failed to add Geofences.", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException securityException) {
            //Catch permission error
            //Occurs when Location permission is not granted
            Log.e(TAG, "Location Permission not granted!");
        } finally {
            Log.i(TAG, "Geofences added");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended.");
        mGoogleApiClient.connect();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: Error code: " + connectionResult.getErrorCode());
    }
}
