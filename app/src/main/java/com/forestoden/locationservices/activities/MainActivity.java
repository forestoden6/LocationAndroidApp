package com.forestoden.locationservices.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.forestoden.locationservices.R;
import com.forestoden.locationservices.fragments.HomeFragment;
import com.forestoden.locationservices.fragments.MFLScheduleFragment;
import com.forestoden.locationservices.fragments.Mapfragment;
import com.forestoden.locationservices.fragments.PastTripsFragment;
import com.forestoden.locationservices.fragments.PredictionFragment;
import com.forestoden.locationservices.fragments.ScheduleFragment;
import com.forestoden.locationservices.fragments.ServiceAdvisoryFragment;
import com.forestoden.locationservices.fragments.SettingsFragment;
import com.forestoden.locationservices.globals.Constants;
import com.forestoden.locationservices.globals.GeofenceErrorMessages;
import com.forestoden.locationservices.model.Station;
import com.forestoden.locationservices.services.GeofenceTransitionsIntentService;
import com.forestoden.locationservices.services.GetStationsTask;
import com.forestoden.locationservices.services.UserTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.forestoden.locationservices.globals.Constants.STATIONS;
import static com.forestoden.locationservices.globals.Constants.StationIDMap;
import static com.forestoden.locationservices.globals.Constants.UDID;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        ResultCallback<Status>,
        HomeFragment.OnFragmentInteractionListener,
        Mapfragment.OnFragmentInteractionListener,
        ScheduleFragment.OnFragmentInteractionListener,
        ServiceAdvisoryFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        PredictionFragment.OnFragmentInteractionListener,
        PastTripsFragment.OnFragmentInteractionListener ,
        android.support.v4.app.FragmentManager.OnBackStackChangedListener{

    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_INTERNET = 1;

    private static final String TAG = MainActivity.class.getName();

    protected GoogleApiClient mGoogleApiClient;

    protected Location mLocation;

    protected ArrayList<Geofence> mGeofenceList;

    private Mapfragment Mapfragment;

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
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //Listen for changes in the backstack
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //shouldDisplayHomeUp();

        createGoogleApiClient();

        //Hamburger Menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setupDrawer();

        Mapfragment = Mapfragment.newInstance();

        //Create and display Home Fragment
        HomeFragment homeFragment = HomeFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .commit();

        String udid = InstanceID.getInstance(this).getId();
        UDID = udid;
        Log.d(TAG, udid);

        //First Run detection, create user
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("newUser", true)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("newUser",false);
            editor.apply();

            //Create User in Back-end
            //String uuid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            //String udid = InstanceID.getInstance(this).getId();
            Log.d(TAG, udid);
            udid = "udid=" + udid;
            new UserTask().execute(udid);
        }

        //Create Geofence List (but not initialize them)
        mGeofenceList = new ArrayList<Geofence>();

        try {
            createGeofenceList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shouldDisplayHomeUp() {
        //Enable up button if there are items in back stack
        boolean back = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(back);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //This can be empty unless we need to communicate between fragments.
    }

    @Override
    public void onBackStackChanged() {
        //shouldDisplayHomeUp();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    private void selectItem(int position) {
        //TODO: Want to refactor for 5.0 Menu maybe, how to keep backwards compat?
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                try {
                    fragment = HomeFragment.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                        .commit();

                mActivityTitle = this.getString(R.string.home);
                break;
            case 1:
                try {
                    fragment = PredictionFragment.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                        .commit();

                mActivityTitle = this.getString(R.string.prediction);
                break;
            case 2:
                Mapfragment mapfragment = null;
                try {
                    mapfragment = Mapfragment.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //I believe this is reduntant as the app should ask for this permission when it opens
                //but intellij gave an error, so I put it in for now
                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                }

                Log.d(TAG, "Getting location...");
                mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                //Mapfragment.setUserMarker(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));

                Bundle latLngBundle = new Bundle();
                if (mLocation != null) {
                    latLngBundle.putDouble("lat", mLocation.getLatitude());
                    latLngBundle.putDouble("long", mLocation.getLongitude());
                } else {
                    latLngBundle.putDouble("lat", 39.954821);
                    latLngBundle.putDouble("long", -75.183123);
                }

                mapfragment.setArguments(latLngBundle);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out,
                        R.anim.left_in, R.anim.right_out);

                fragmentTransaction.replace(R.id.fragment_container, mapfragment);
                fragmentTransaction.commit();

                mActivityTitle = this.getString(R.string.map);

                Log.d(TAG, "User marker set");


                //Mapfragment.setUserMarker(new LatLng(location.getLatitude(),location.getLongitude()));
                /*fragment = fragmentManager.findFragmentById(R.id.fragment_container);

                if (fragment == null){
                    fragment = new Mapfragment();
                    fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit();
                }*/

                //Toast.makeText(MainActivity.this, "This should be a map!", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                try {
                    fragment = MFLScheduleFragment.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out,
                        R.anim.left_in, R.anim.right_out);

                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();

                mActivityTitle = this.getString(R.string.schedule);
                break;
            case 4:
                try {
                    fragment = ServiceAdvisoryFragment.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out,
                        R.anim.left_in, R.anim.right_out);

                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();

                mActivityTitle = this.getString(R.string.service_advisories);
                break;
            case 5:
                try {
                    fragment = PastTripsFragment.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out,
                        R.anim.left_in, R.anim.right_out);

                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();

                mActivityTitle = this.getString(R.string.past_trips);
                break;
            case 6:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .commit();

                mActivityTitle = this.getString(R.string.settings);
                break;
            default:
                break;
        }

        getSupportActionBar().setTitle(mActivityTitle);


    }
//        Fragment fragement = new Mapfragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragement.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//        mDrawerList.setItemChecked(position, true);
//        mDrawerLayout.closeDrawer(mDrawerList);
//


    //Setup Navigation Drawer
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            /** Called when drawer has been fully opened */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("Navigation Drawer");
                invalidateOptionsMenu();
            }

            /** Called when drawer has been fully closed */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
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

    @Override
    public void onLocationChanged(Location location) {
        //Log.v(TAG, "Long:" + location.getLongitude() + "Lat:" + location.getLatitude());
        //Mapfragment.setUserMarker(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    /**
     * Creates the list of station locations obtained from the server
     * Parses JSON returned by GetStationsTask
     * Adds to HashMap
     */
    protected void createGeofenceList() throws JSONException, MalformedURLException {
        //URL stationUrlObject = new URL(stationUrl);
        //URL bsl = new URL("http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/stations/bsl");
        URL mfl = new URL("http://lowcost-env.r8dpz7s6b2.us-west-2.elasticbeanstalk.com/stations");
        //Creates a new thread to get stations asynchronously
        GetStationsTask stationConnection = new GetStationsTask();
        String stations = null;
        try {
            stations = stationConnection.execute(mfl).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //Parse JSON returned by server and add to list of stations
        if (stations != null) {
            JSONArray stationJson;

            try {
                stationJson = new JSONArray(stations);
            } catch (Throwable t) {
                Log.e(TAG, "Could not parse malformed JSON: " + stations);
                return;
            }

            if (stationJson.length() > 0) {
                for (int i = 0; i < stationJson.length(); i++) {
                    JSONObject stationJsonObject = stationJson.getJSONObject(i);
                    //String line = (String)stationJsonObject.get("line");
                    /*if(line.contains("owl")){
                        Log.d(TAG, "test");
                        continue;
                    }*/
                    String name = (String) stationJsonObject.get("name_long");
                    double latitude = Double.parseDouble((String) stationJsonObject.get("latitude"));
                    double longitude = Double.parseDouble((String) stationJsonObject.get("longitude"));
                    int id = Integer.parseInt((String) stationJsonObject.get("id_station"));
                    String address = stationJsonObject.getString("address");
                    String line = stationJsonObject.getString("line");
                    Station station = new Station(id, name, address, new LatLng(latitude, longitude), line);
                    STATIONS.add(station);
                    //StationMap.put(name, station);
                    StationIDMap.put(id, station);
                }
            } else {
                Log.e(TAG, "Empty JSON returned by server");
                Toast.makeText(this, "Error connecting to server", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "No response from server");
            Toast.makeText(this, "Could not connect to server", Toast.LENGTH_LONG).show();
        }


        //Create Geofence objects
        //NOTE: Geofences will not be activated here
        for (Station station : STATIONS) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(station.getID()))
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

    public void launchPredictionFragment(View view) {

    }

    //public void addGeofencesButtonHandler(View view){

    /**
     * Removed this method and integrated functionality into onStart()
     * We are using a ConnectionCallback to create the geofences when the GoogleApiClient is connected
     * and this functionality was migrated to onConnected.

     public void createGeofences() {
     Log.i(TAG, String.valueOf(mGoogleApiClient.isConnected()));
     if(mGoogleApiClient.isConnecting()){
     Toast.makeText(this, "Connecting to Google API Client.", Toast.LENGTH_SHORT).show();
     }
     if(!mGoogleApiClient.isConnected()) {
     mGoogleApiClient.connect();
     Toast.makeText(this, "Google API Client not connected!", Toast.LENGTH_SHORT).show();
     //return;
     }
     Log.i(TAG, String.valueOf(mGoogleApiClient.isConnected()));
     //Toast.makeText(this, "Test", Toast.LENGTH_LONG).show();
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
     */

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
        if (status.isSuccess()) {
            Toast.makeText(this, "Geofences Added", Toast.LENGTH_SHORT).show();
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //I believe this is reduntant as the app should ask for this permission when it opens
        //but intellij gave an error, so I put it in for now
/*        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
        if(mGoogleApiClient.isConnected()) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Mapfragment.setUserMarker(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
        }*/
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }*/

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
            /*mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                    mLocation.getLatitude()));
            mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                    mLocation.getLongitude()));*/
        } else {
            Toast.makeText(this, "No location detected", Toast.LENGTH_LONG).show();
        }

        try {
            if (!mGeofenceList.isEmpty()) {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                );
            } else {
                Toast.makeText(this, "Failed to add Geofences.", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException securityException) {
            //Catch permission error
            //Occurs when Location permission is not granted
            Log.e(TAG, "Location Permission not granted!");
        } finally {
            Log.i(MainActivity.TAG, "Geofences added");
        }

        startLocationServices();
    }

    public void startLocationServices(){
        Log.v(TAG, "Starting Location Services Called");

        try {
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, req, this);
            Log.v(TAG, "Requesting location updates");
        }
        catch (SecurityException exception) {
            //Show dialog to user saying we can't get location unless they give app permission
            Log.v(TAG, exception.toString());
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
        } else if(requestCode == REQUEST_INTERNET) {
            Log.i(TAG, "Received response for Internet permission request.");
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Internet permission granted.");
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "Internet permission denied.");
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
