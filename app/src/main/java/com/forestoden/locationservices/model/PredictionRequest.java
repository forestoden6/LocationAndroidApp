package com.forestoden.locationservices.model;

/**
 * Created by ForestOden on 5/16/2017.
 * Project: LocationServices.
 */

public class PredictionRequest {

    private double lat;
    private double lon;

    private String udid;
    private String time;

    private int dayOfWeek;

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getUdid() {
        return udid;
    }

    public String getTime() {
        return time;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public PredictionRequest(double lat, double lon, String udid, String time, int dayOfWeek) {

        this.lat = lat;
        this.lon = lon;
        this.udid = udid;
        this.time = time;
        this.dayOfWeek = dayOfWeek;
    }
}
