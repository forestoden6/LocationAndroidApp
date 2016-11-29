package com.forestoden.locationservices.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.forestoden.locationservices.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class Mapfragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions userMarker;


    public Mapfragment() {
        // Required empty public constructor
    }


    public static Mapfragment newInstance() {
        Mapfragment fragment = new Mapfragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapfragment, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final EditText zipText = (EditText)view.findViewById(R.id.zip_text);
        zipText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER){
                    //Be sure to validate zipcode - check total count and characters
                    String text = zipText.getText().toString();
                    int zip = Integer.parseInt(text);

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(zipText.getWindowToken(), 0);

                    // TODO: Check if we still want to use ZIPCODE
                    //updateMapForStations(zip);
                    return true;
                }
                return false;
            }
        });

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public void setUserMarker(LatLng latlng){

        if (userMarker == null){
            userMarker = new MarkerOptions().position(latlng).title("Current Location");
            mMap.addMarker(userMarker);
            Log.v("DONKEY", "Current Location:" + latlng.latitude + " long:" + latlng.longitude);
        }

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
            int zip = Integer.parseInt(addresses.get(0).getPostalCode());
            // TODO: Check if we still want to use ZIPCODE
            //updateMapForStations(zip);
        }
        catch (IOException exception){

        }
        // TODO: Check if we still want to use ZIPCODE
        //updateMapForStations(19104);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

    }

//    private void updateMapForStations(int zipcode){
//
//        ArrayList<Stations> locations = DataService.getInstance().getStationLocations(zipcode);
//
//        for( int x = 0; x < locations.size(); x++){
//            Stations loc = locations.get(x);
//            MarkerOptions marker = new MarkerOptions().position(new LatLng(loc.getLatitude(),loc.getLongitude()));
//            marker.title(loc.getLocationName());
//            marker.snippet(loc.getLocationAddress());
//            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
//            mMap.addMarker(marker);
//        }
//    }







}
