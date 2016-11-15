package com.forestoden.locationservices;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.forestoden.locationservices.Constants.STATIONS;
import static com.forestoden.locationservices.Constants.stationUrlObject;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        ResultCallback<Status> {

    private static final int REQUEST_FINE_LOCATION= 0;

    private static final String TAG = "LocationServices";

    protected GoogleApiClient mGoogleApiClient;

    protected Location mLocation;

    protected ArrayList<Geofence> mGeofenceList;

    protected static final String mLatitudeLabel = "Latitude";
    protected static final String mLongitudeLabel = "Longitude";
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;

    private Button mAddGeofencesButton;

    //Navigation Drawer Variables
    private String[] mDrawerOptions;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerOptions = getResources().getStringArray(R.array.nav_options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mActivityTitle = getTitle().toString();

        //Set adapter to the drawer options
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerOptions));

        //Set click listener to get menu selection
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Toast.makeText(MainActivity.this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
            }
        });

        //Hamburger Menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setupDrawer();

        mLatitudeText = (TextView) findViewById(R.id.latitude_text);
        mLongitudeText = (TextView) findViewById(R.id.longitude_text);
        mGeofenceList = new ArrayList<Geofence>();
        mGeofenceList = new ArrayList<>();

        try {
            createGeofenceList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        createGoogleApiClient();
    }

    //Setup Navigation Drawer
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            /** Called when drawer has been fully opened */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation Drawer");
                invalidateOptionsMenu();
            }

            /** Called when drawer has been fully closed */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            return true;
        }

        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * Creates the list of station locations obtained from the server
     * Parses JSON returned by GetStationsTask
     * Adds to HashMap
     */
    protected void createGeofenceList() throws JSONException {
        /* TODO: Ask user for Internet permission at run time */

        //Creates a new thread to get stations asynchronously
        GetStationsTask stationConnection = new GetStationsTask();
        String stations = null;
        try {
            stations = stationConnection.execute(stationUrlObject).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Parse JSON returned by server and add to list of stations
        if(stations != null){
            Log.i(TAG, stations);
            JSONArray stationJson;

            try {
                stationJson = new JSONArray(stations);
            } catch (Throwable t) {
                Log.e(TAG, "Could not parse malformed JSON: " + stations);
                return;
            }

            for(int i = 0; i < stationJson.length(); i++){
                JSONObject stationJsonObject = stationJson.getJSONObject(i);
                String name = (String) stationJsonObject.get("proper_name");
                double latitude = Double.parseDouble((String)stationJsonObject.get("latitude"));
                double longitude = Double.parseDouble((String)stationJsonObject.get("longitude"));
                /* TODO: IDs and Address are placeholders for now. MUST ADD! */
                Station station = new Station(0,1,name, "", new LatLng(latitude, longitude), 0);
                STATIONS.add(station);
            }
        }


        //Create Geofence objects
        //NOTE: Geofences will not be created here
        for(Station station : STATIONS) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(station.getName())
                    .setCircularRegion(
                            station.getLatLng().latitude,
                            station.getLatLng().longitude,
                            Constants.GEOFENCE_RADIUS_METERS)
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    public void addGeofencesButtonHandler(View view){
        if(!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google API Client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            //Catch permission error
            //Occurs when Location permission is not granted
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status) {
        if(status.isSuccess()) {
            Toast.makeText(this, "Geofences Added", Toast.LENGTH_SHORT).show();
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void createGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i(TAG, String.valueOf(mGoogleApiClient.isConnected()));
        Log.i(TAG, "Permission granted: " + String.valueOf(permissionCheck));
        if(mLocation != null) {
            mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                    mLocation.getLatitude()));
            mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                    mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "No location detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: Error code: " + result.getErrorCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            Log.i(TAG, "Received response for Location permission request.");

            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Location permission granted.");
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "Location permission denied.");
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
