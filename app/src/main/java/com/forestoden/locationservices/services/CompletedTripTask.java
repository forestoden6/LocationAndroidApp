package com.forestoden.locationservices.services;

import android.util.Log;

import com.forestoden.locationservices.model.Trip;

import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by ForestOden on 1/28/2017.
 * Project: LocationServices.
 */

class CompletedTripTask extends TimerTask {

    private static final String TAG = CompletedTripTask.class.getName();

    @Override
    public void run() {
        Trip trip = GeofenceTransitionsIntentService.trip;
        //Log.d(TAG, "Sending trip" + GeofenceTransitionsIntentService.trip.getStart().toString());
        Log.i(TAG, "Trip: " + trip.getStart().getRequestId() + " to " +
                trip.getEnd().getRequestId() + ". Duration: " + trip.getTripDuration());
        trip.resetTrip();
        //TODO: Implement API calls to save
        //Will Create new class that extends ASyncTask, SaveCompletedTripTask
        SaveCompletedTripTask saveCompletedTripTask = new SaveCompletedTripTask();
        String response = null;
        try {
            response = saveCompletedTripTask.execute(trip).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG, response);
    }
}
