package com.forestoden.locationservices.globals;

import com.forestoden.locationservices.model.Station;
import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.HashMap;
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
            "http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/stations/";
    //static URL stationUrlObject;

    public static List<Station> STATIONS = new ArrayList<>();

    public static HashMap<String, Station> StationMap = new HashMap<>();
    public static HashMap<Integer, Station> StationIDMap = new HashMap<>();

    public List<Station> getStationLocations(){
        return STATIONS;
    }

    private static final int MS_PER_SECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60 * MS_PER_SECOND;
    public static final int TRIP_TIMEOUT = 1 * SECONDS_PER_MINUTE;

    public static String UDID;

    private Constants() {
    }
}
