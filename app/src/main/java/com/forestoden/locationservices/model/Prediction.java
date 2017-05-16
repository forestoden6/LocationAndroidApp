package com.forestoden.locationservices.model;

/**
 * Created by ForestOden on 4/9/2017.
 * Project: LocationServices.
 */

public class Prediction {

    private Station destinationStation;
    private Station nearestStation;

    private String nextTrain;
    private String eta;

    public Station getDestinationStation() {
        return destinationStation;
    }

    public Station getNearestStation() {
        return nearestStation;
    }

    public String getNextTrain() {
        return nextTrain;
    }

    public String getEta() {
        return eta;
    }

    public Prediction(Station destinationStation, Station nearestStation, String nextTrain, String eta) {
        this.destinationStation = destinationStation;
        this.nearestStation = nearestStation;
        this.nextTrain = nextTrain;
        this.eta = eta;
    }
}
