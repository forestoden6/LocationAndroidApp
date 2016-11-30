package com.forestoden.locationservices.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.forestoden.locationservices.model.TripContract.SQL_CREATE_ENTRIES;
import static com.forestoden.locationservices.model.TripContract.SQL_DELETE_ENTRIES;

/**
 * Created by ForestOden on 11/27/2016.
 * Project: LocationServices.
 */

//TODO: Using SharedPrefs for Trip, keeping this to refactor into stationDb

public class TripDbHelper extends SQLiteOpenHelper {

    //If database schema is changed, database version MUST be incremented.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Trip.db";

    private static TripDbHelper sInstance;

    /**
     * Constructor is private to prevent direct instantiation.
     * To use, call static method getInstance().
     */
    private TripDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TripDbHelper getInstance(Context context) {
        //Use application context to prevent Activity context leaks
        //Also ensures singleton instance over application lifecycle
        if (sInstance == null) {
            sInstance = new TripDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //This database is only for storing intermediate trip data, so we can
        //just discard the data because we'd have no use for it
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
