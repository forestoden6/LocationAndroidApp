package com.forestoden.locationservices.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ForestOden on 11/13/2016.
 * Project: LocationServices.
 */

public class Station {

    private int ID1;
    private int ID2;
    private String name;
    private String address;
    private LatLng latLng;
    private int lineID;

    public Station(int id1, int id2, String name, String address, LatLng latLng, int lineID) {
        this.address = address;
        this.ID1 = id1;
        this.ID2 = id2;
        this.name = name;
        this.latLng = latLng;
        this.lineID = lineID;

    }

    public int getID1() {
        return ID1;
    }

    public int getID2() {
        return ID2;
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

    public int getLineID() {
        return lineID;
    }

}
