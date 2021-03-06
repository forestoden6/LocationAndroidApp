package com.forestoden.locationservices.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
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
    private boolean isSelected;



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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("h:mm:ss a");

        String tripString = getStart().getName() + " " +
                localDateFormat.format(getStartTime()) + " \n" +
                getEnd().getName() + " " +
                localDateFormat.format(getEndTime());
        return tripString;
    }

}
