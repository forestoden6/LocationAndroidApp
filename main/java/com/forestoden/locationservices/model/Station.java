package com.forestoden.locationservices.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ForestOden on 11/13/2016.
 * Project: LocationServices.
 */

public class Station {

    private int ID;
    private String name;
    private String address;
    private LatLng latLng;
    private String line;

    public Station(int id, String name, String address, LatLng latLng, String line) {
        this.address = address;
        this.ID = id;
        this.name = name;
        this.latLng = latLng;
        this.line = line;

    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getLine() {
        return line;
    }

    @Override
    public String toString() {
        return name + '\n' + address;
    }
}
