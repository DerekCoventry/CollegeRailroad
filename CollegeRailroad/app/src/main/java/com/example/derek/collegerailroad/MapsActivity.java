package com.example.derek.collegerailroad;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapsActivity extends BaseAppCompatActivity implements OnMapReadyCallback {

    ArrayList<Parcelable> mBooks;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mBooks = getIntent().getParcelableArrayListExtra("BOOKS");
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
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context mContext = MapsActivity.this;
                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        List<MarkerOptions> markers= new ArrayList<>();
        try {
                Location2 mLoc = new Location2(this, getApplicationContext());
                String currentLocation = mLoc.getCity() + ", " + mLoc.getState();
                for(int i =0; i < mBooks.size(); i++) {
                    BookPost book = (BookPost) mBooks.get(i);
                    String title = book.getTitle();
                    String email = book.getEmail();
                    String author = book.getAuthor();
                    String condition = book.getCondition();
                    String bookInfo = title + " by " + author +"\n" + email+"\nCondtion: "+condition;
                    LatLng latLng = book.getLatLng();
                    if(latLng != null) {
                        LatLng location = new LatLng(latLng.latitude, latLng.longitude);
                        MarkerOptions marker = new MarkerOptions().position(location).title(currentLocation);
                        marker.snippet(bookInfo);
                        markers.add(marker);
                        mMap.addMarker(marker);
                    }
                }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MarkerOptions marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 200;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cu);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
