package com.forestoden.locationservices.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by ForestOden on 11/6/2016.
 */

public class Trip {

    private Station start;
    private Station end;

    private Date startDate;
    private Date endDate;

    private int id;

    private boolean newTrip = true;

    public Trip() {

    }

    public Trip(Station start, Station end, Date startDate, Date endDate, int id) {
        this.start = start;
        this.end = end;
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
    }

    public  boolean isNewTrip() {
        return newTrip;
    }

    //TODO: Decide on how to store dates
    //Probably change how times are stored
    public void setStart(Station station, Date date, Context mContext) {
        start = station;
        startDate = date;
        newTrip = false;
        SharedPreferences tripSharedPref = mContext.getSharedPreferences("Start",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = tripSharedPref.edit();
        editor.putBoolean("New Trip", newTrip);
        editor.putString("Station", station.getName());
        editor.putString("Time", date.toString());
        editor.apply();
    }

    public void setEnd(Station station, Date date) {
        end = station;
        endDate = date;
    }

    public int getId() { return id; }

    public Station getStart() {
        return start;
    }

    public Station getEnd() {
        return end;
    }

    public Date getStartTime() {
        return startDate;
    }

    public Date getEndTime() {
        return endDate;
    }

    public long getTripDuration() {
        return getEndTime().getTime() - getStartTime().getTime();
    }

    public void setId(int id) { this.id = id; }

    public void resetTrip() {
        //setStart(null, null);
        //setEnd(null, null);
        newTrip = true;
    }

}
