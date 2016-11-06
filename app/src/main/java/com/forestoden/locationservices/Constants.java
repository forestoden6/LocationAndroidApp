package com.forestoden.locationservices;

import android.provider.Settings;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

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

    public static final HashMap<String, LatLng> LOCATIONS = new HashMap<>();

    static {
        LOCATIONS.put("30th Street Station", new LatLng(39.954906, -75.183252));
        LOCATIONS.put("40th Street Station", new LatLng(39.957126, -75.201936));
    }

    public static String uuid = Settings.Secure.ANDROID_ID;

    private Constants() {
    }
}
