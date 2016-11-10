package com.forestoden.locationservices;

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

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
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

        for(Geofence geofence : event.getTriggeringGeofences()) {
            triggeringIDs.add(geofence.getRequestId());
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                addToPath(geofence);
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

    private static void addToPath(Geofence geofence) {
        if(trip.isNewTrip()) {
            trip.setStart(geofence);
        } else {
            trip.setEnd(geofence);
            Log.i(TAG, "Trip: " + trip.getStart().getRequestId() + " to " +
                                    trip.getEnd().getRequestId());
            trip.resetTrip();
        }

    }

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
