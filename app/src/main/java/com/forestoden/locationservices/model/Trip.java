package com.forestoden.locationservices.model;

import com.google.android.gms.location.Geofence;

import java.util.Date;

/**
 * Created by ForestOden on 11/6/2016.
 */

public class Trip {

    private Geofence start;
    private Geofence end;

    private Date startDate;
    private Date endDate;

    private boolean newTrip = true;

    public Trip() {

    }

    public  boolean isNewTrip() {
        return newTrip;
    }

    //TODO: Decide on how to store dates
    //Probably change how times are stored
    public void setStart(Geofence geofence, Date date) {
        start = geofence;
        startDate = date;
        newTrip = false;
    }

    public void setEnd(Geofence geofence, Date date) {
        end = geofence;
        endDate = date;
    }

    public Geofence getStart() {
        return start;
    }

    public Geofence getEnd() {
        return end;
    }

    public long getStartTime() {
        return startDate.getTime();
    }

    public long getEndTime() {
        return endDate.getTime();
    }

    public long getTripDuration() {
        return getEndTime() - getStartTime();
    }

    public void resetTrip() {
        //setStart(null, null);
        //setEnd(null, null);
        newTrip = true;
    }

}
