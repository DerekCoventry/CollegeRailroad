package com.example.derek.collegerailroad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loginButton = (Button) findViewById(R.id.log_but);

        loginButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        Button bookButton = (Button) findViewById(R.id.book_but);

        bookButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, BookDisplayActivity.class));
            }
        });
        Button listButton = (Button) findViewById(R.id.list_but);

        listButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, BookListActivity.class));
            }
        });
        Button webButton = (Button) findViewById(R.id.web_but);

        webButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, web.class));
            }
        });

        Button mapButton = (Button) findViewById(R.id.map_but);

        mapButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        Button cameraButton = (Button) findViewById(R.id.camera_but);

        cameraButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });

        Button searchButton = (Button) findViewById(R.id.search_but);
        searchButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, SearchISBNActivity.class));
            }
        });
    }

}
