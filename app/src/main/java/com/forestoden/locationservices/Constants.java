package com.forestoden.locationservices;

import android.provider.Settings;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by ForestOden on 10/26/2016.
 */
class Constants {
    private static Constants ourInstance = new Constants();

    public static Constants getInstance() {
        return ourInstance;
    }

    static final long GEOFENCE_EXPIRATION = Geofence.NEVER_EXPIRE;
    static final float GEOFENCE_RADIUS_METERS = 100;

    static final String stationUrl =
            "http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/septa_stations.php";
    static URL stationUrlObject;

    static HashMap<String, LatLng> LOCATIONS = new HashMap<>();

    static {
        try {
            stationUrlObject = new URL(stationUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static String uuid = Settings.Secure.ANDROID_ID;

    private Constants() {
    }
}
