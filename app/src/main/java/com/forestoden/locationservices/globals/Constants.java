package com.forestoden.locationservices.globals;

import android.provider.Settings;

import com.forestoden.locationservices.model.Station;
import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ForestOden on 10/26/2016.
 */
public class Constants {
    private static Constants ourInstance = new Constants();

    public static Constants getInstance() {
        return ourInstance;
    }

    public static final long GEOFENCE_EXPIRATION = Geofence.NEVER_EXPIRE;
    public static final float GEOFENCE_RADIUS_METERS = 100;

    public static final String stationUrl =
            "http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/septa_stations.php";
    //static URL stationUrlObject;

    public static List<Station> STATIONS = new ArrayList<>();

    /*static {
        try {
            stationUrlObject = new URL(stationUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }*/

    public static final int SECONDS_PER_MINUTE = 60;
    public static final int TRIP_TIMEOUT = 5 * SECONDS_PER_MINUTE;

    public static String uuid = Settings.Secure.ANDROID_ID;

    private Constants() {
    }
}