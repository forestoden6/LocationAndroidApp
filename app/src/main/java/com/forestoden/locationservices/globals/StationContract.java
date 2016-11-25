package com.forestoden.locationservices.globals;

import android.provider.BaseColumns;

/**
 * Created by ForestOden on 11/25/2016.
 * Project: LocationServices.
 */

public final class StationContract {
    private StationContract() {}

    public static class StationEntry implements BaseColumns {
        public static final String TABLE_NAME = "stations";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LAT = "latitude";
        public static final String COLUMN_NAME_LONG = "longitude";
    }
}
