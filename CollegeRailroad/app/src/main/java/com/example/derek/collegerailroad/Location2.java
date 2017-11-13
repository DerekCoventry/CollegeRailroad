package com.example.derek.collegerailroad;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class Location2 {
    private final Context mContext;
    private String state = "", city = "";
    private LatLng location;

    public Location2(Activity activity, Context context){
        this.mContext = context;
        LocationManager mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        12);
            }
            final LocationListener gpsLocationListener =new LocationListener(){

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    switch (status) {
                        case LocationProvider.AVAILABLE:
                            break;
                        case LocationProvider.OUT_OF_SERVICE:
                            break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            break;
                    }
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }

                @Override
                public void onLocationChanged(Location location) {
                }
            };
            Criteria criteria = new Criteria();
            String bestProvider = mLocationManager.getBestProvider(criteria, false);
            mLocationManager.requestLocationUpdates(bestProvider, 0, 0, gpsLocationListener);
            Location coordinates = mLocationManager.getLastKnownLocation(bestProvider);
            double lat = coordinates.getLatitude();
            double lng = coordinates.getLongitude();
            LatLng location = new LatLng(lat, lng);

            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = null;
            String city = null;
            String state = null;
            //String currentLocation = "Current Location";
            try {
                Log.d("TEST latitude", Double.toString(lat));
                Log.d("TEST longitude", Double.toString(lng));
                addresses = gcd.getFromLocation(lat, lng, 1);
                if (addresses.size() > 0) {
                    // Get the current city
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            /**if(city == null){
                if(state != null){
                    currentLocation = state;
                }
            }else{
                if (state == null) {
                    currentLocation = city;
                } else {
                    currentLocation = city + ", " + state;
                }
            }**/
            this.state = state;
            this.city = city;
            this.location = location;
        } catch (SecurityException e) {
            this.state = "";
            this.city = "";
            this.location = null;
        }
    }
    public String getCity(){
        return this.city;
    }

    public String getState(){
        return this.state;
    }
    public LatLng getLocation(){
        return this.location;
    }

    public static LatLng getStateCoordinates(Context mContext, String state) {
        List<LatLng> ll = new ArrayList<>();
        LatLng coordinates = new LatLng(0,0);
        if (Geocoder.isPresent()) {
            try {
                String location = state;
                Geocoder gc = new Geocoder(mContext);
                List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }
            } catch (IOException e) {
                // handle the exception
            }
        }
        if(!ll.isEmpty()){
            coordinates = ll.get(0);
        }
        return coordinates;
    }

    public static String getCityState(Context mContext, LatLng latLng){
        Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = null;
        String city = "Unknown City";
        String state = "Unknown State";
        //String currentLocation = "Current Location";
        try {
            addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                // Get the current city
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return city + ", " + state;
    }
}
