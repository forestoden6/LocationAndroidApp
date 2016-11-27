package com.forestoden.locationservices.model;

import android.provider.BaseColumns;

/**
 * Created by ForestOden on 11/25/2016.
 * Project: LocationServices.
 */

public final class StationContract {
    private StationContract() {}

    //TODO: Implement Station Database

    public static class StationEntry implements BaseColumns {
        public static final String DATABASE_NAME = "stations";
        public static final int DATABASE_VERSION = 1;
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LAT = "latitude";
        public static final String COLUMN_NAME_LONG = "longitude";
    }
}
