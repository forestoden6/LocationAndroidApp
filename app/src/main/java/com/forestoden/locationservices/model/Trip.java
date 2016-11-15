package com.forestoden.locationservices.model;

import com.google.android.gms.location.Geofence;

/**
 * Created by ForestOden on 11/6/2016.
 */

public class Trip {

    private Geofence start;
    private Geofence end;

    private boolean newTrip = true;

    public Trip() {

    }

    public  boolean isNewTrip() {
        return newTrip;
    }

    public void setStart(Geofence geofence) {
        start = geofence;
        newTrip = false;
    }

    public void setEnd(Geofence geofence) {
        end = geofence;
    }

    public Geofence getStart() {
        return start;
    }

    public Geofence getEnd() {
        return end;
    }

    public void resetTrip() {
        setStart(null);
        setEnd(null);
        newTrip = true;
    }

}
