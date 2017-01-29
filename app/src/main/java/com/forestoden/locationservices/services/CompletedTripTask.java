package com.forestoden.locationservices.services;

import android.util.Log;

import com.forestoden.locationservices.model.Trip;

import java.util.TimerTask;

/**
 * Created by ForestOden on 1/28/2017.
 * Project: LocationServices.
 */

class CompletedTripTask extends TimerTask {

    private static final String TAG = CompletedTripTask.class.getName();

    //public boolean hasRun = false;

    @Override
    public void run() {
        //hasRun = true;
        Trip trip = GeofenceTransitionsIntentService.trip;
        //Log.d(TAG, "Sending trip" + GeofenceTransitionsIntentService.trip.getStart().toString());
        Log.i(TAG, "Trip: " + trip.getStart().getRequestId() + " to " +
                trip.getEnd().getRequestId() + ". Duration: " + trip.getTripDuration());
        trip.resetTrip();
        //TODO: Implement API calls to save
        //Reset hasRun to allow for new trip
        //hasRun = false;
    }
}
