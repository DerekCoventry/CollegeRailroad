package com.example.derek.collegerailroad;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.security.Security;
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
            android.location.Location coordinates = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            double lat = coordinates.getLatitude();
            double lng = coordinates.getLongitude();
            LatLng location = new LatLng(lat, lng);

            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = null;
            String city = null;
            String state = null;
            //String currentLocation = "Current Location";
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
}
