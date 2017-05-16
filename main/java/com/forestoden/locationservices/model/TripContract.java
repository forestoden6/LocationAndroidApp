package com.forestoden.locationservices.model;

import android.provider.BaseColumns;

/**
 * Created by ForestOden on 11/27/2016.
 * Project: LocationServices.
 */

public final class TripContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " +
        TripEntry.TABLE_NAME + " (" +
        TripEntry._ID + " INTEGER PRIMARY KEY," +
        TripEntry.COLUMN_NAME_START_STATION + TEXT_TYPE + COMMA_SEP +
        TripEntry.COLUMN_NAME_START_TIME + TEXT_TYPE + COMMA_SEP +
        TripEntry.COLUMN_NAME_END_STATION + TEXT_TYPE + COMMA_SEP +
        TripEntry.COLUMN_NAME_END_TIME + TEXT_TYPE + " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TripEntry.TABLE_NAME;

    private TripContract() {}

    public static class TripEntry implements BaseColumns {
        public static final String TABLE_NAME = "trip";
        public static final String COLUMN_NAME_START_STATION = "start_station";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_END_STATION = "end_station";
        public static final String COLUMN_NAME_END_TIME = "end_time";
    }
}
