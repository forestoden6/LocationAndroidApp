package com.forestoden.locationservices.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.forestoden.locationservices.R;
import com.forestoden.locationservices.activities.MainActivity;
import com.forestoden.locationservices.globals.Constants;
import com.forestoden.locationservices.model.Trip;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ForestOden on 10/26/2016.
 */

public class GeofenceTransitionsIntentService extends IntentService {
    protected static final String TAG = GeofenceTransitionsIntentService.class.getName();

    private static Trip trip;

    static {
        trip = new Trip();
    }

    public GeofenceTransitionsIntentService() {
        super(TAG);

    }

    /**
     * Intent was caused from Geofence Transition. Handler will get the transition details,
     * i.e. did user enter or exit and the station name (requestID). A notification will be
     * generated in the handling as well.
     * @param intent Intent generated from Play Services when user interacted with geofence
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event.hasError()) {
            Log.e(TAG, "GeofencingEvent Error: " + event.getErrorCode());
            return;
        }

        String description = getGeofenceTransitionDetails(event);
        Log.i(TAG, description);
    }

    /**
     * Extracts Information from Geofenceing Event. Adds station to user's trip and generates
     * notification string.
     * @param event GeofencingEvent that triggered intent
     * @return String value with enter/exit info and Geofence request ID (station name)
     */
    private String getGeofenceTransitionDetails(GeofencingEvent event) {
        /*
        TODO: Clean up method. Deal with Entry and Exit separately. No need for return here
        TODO: Method can be void and will be called from onHandleIntent
        */
        if(event.hasError()){
            Log.e(TAG, "Error");
            Log.e(TAG, String.valueOf(event.getErrorCode()));
        }

        int geofenceTransition = event.getGeofenceTransition();

        List triggeringIDs = new ArrayList();

        /*
         * In theory, stations should far enough that only one Geofence is
         * returned by getTriggeringGeofences()
         */
        for(Geofence geofence : event.getTriggeringGeofences()) {
            triggeringIDs.add(geofence.getRequestId());
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                addToTrip(geofence);
            }
        }

        String geofenceWelcome = null;

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            geofenceWelcome = String.format("%s: %s", "Welcome to",
                    TextUtils.join(", ", triggeringIDs));
            sendNotification(geofenceWelcome);
        }
        else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            geofenceWelcome = String.format("%s: %s", "Exiting ",
                    TextUtils.join(", ", triggeringIDs));
            sendNotification(geofenceWelcome);
        }

        return geofenceWelcome;
    }

    /**
     * Algorithm to determine user's trip.
     * TODO: Determine if on Subway
     * TODO: Trip object could get deleted. Refactor to store in SQLite or similar
     * @param geofence Geofence that user  entered/exited
     */
    private static void addToTrip(Geofence geofence) {
        Date currentTime = new Date();
        if(trip.isNewTrip()) {
            trip.setStart(geofence, currentTime);
        } else {
            if(currentTime.getTime() - trip.getStartTime() < Constants.TRIP_TIMEOUT &&
                    !geofence.getRequestId().equals(trip.getStart().getRequestId())) {
                trip.setEnd(geofence, currentTime);
                Log.i(TAG, "Trip: " + trip.getStart().getRequestId() + " to " +
                        trip.getEnd().getRequestId() + ". Duration: " + trip.getTripDuration());
                trip.resetTrip();
            }

        }

    }

    /**
     * Sends notification to user when Geofence is entered or exited.
     * Clicking notification returns user to app.
     * @param notificationDetails Text to display in notification
     */
    private void sendNotification(String notificationDetails) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class).addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Click Notification to return to App")
                .setContentIntent(notificationPendingIntent)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }


}
