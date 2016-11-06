package com.forestoden.locationservices;

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

    protected  boolean isNewTrip() {
        return newTrip;
    }

    protected void setStart(Geofence geofence) {
        start = geofence;
        newTrip = false;
    }

    protected void setEnd(Geofence geofence) {
        end = geofence;
    }

    protected Geofence getStart() {
        return start;
    }

    protected Geofence getEnd() {
        return end;
    }

    protected void resetTrip() {
        setStart(null);
        setEnd(null);
        newTrip = true;
    }

}
