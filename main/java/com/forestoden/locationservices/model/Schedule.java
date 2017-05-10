package com.forestoden.locationservices.model;

/**
 * Created by ForestOden on 4/19/2017.
 * Project: LocationServices.
 */

public class Schedule {

    private String departure;
    private String arrival;


    public Schedule(String departure, String arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    @Override
    public String toString() {
        return "Departure: " + departure +
                ", Arrival: " + arrival;
    }
}
