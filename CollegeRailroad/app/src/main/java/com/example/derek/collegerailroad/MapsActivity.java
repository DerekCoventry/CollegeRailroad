package com.example.derek.collegerailroad;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends BaseAppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    protected LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    11 );
        }
        Location coordinates = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double lat = coordinates.getLatitude();
        double lng = coordinates.getLongitude();
        LatLng location = new LatLng(lat, lng);

        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        String city = null;
        String state = null;
        String currentLocation = "Current Location";
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                // Get the current city
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(city == null){
            if(state != null){
                currentLocation = state;
            }
        }else{
            if (state == null) {
                currentLocation = city;
            } else {
                currentLocation = city + ", " + state;
            }
        }
        mMap.addMarker(new MarkerOptions().position(location).title(currentLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }
}
