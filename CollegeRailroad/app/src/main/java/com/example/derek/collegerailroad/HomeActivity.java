package com.example.derek.collegerailroad;

import android.*;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseAppCompatActivity implements SearchFragment.OnFragmentInteractionListener{
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    11 );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_home);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.search_layout, new SearchFragment()).commit();
        TextView intro = (TextView) findViewById(R.id.intro);
        intro.setText("Welcome to College Railroad! This app allows students to buy/sell books with each other without the bookstore middleman markup. Current apps/websites exist for the sole purpose of making money. Businesses and bookstores make excessive amounts of money off of students, and for a very long time students have accepted this as a way of life. Additionally, Facebook pages, and craigslist posts try to accomplish our goal, but the market it waiting for an app to satisfy the need. Having an app be created by potential users allows for an alternative view on the textbook market.");
        Button loginButton = (Button) findViewById(R.id.log_but);
        Button signupButton = (Button) findViewById(R.id.signup_but);
        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        if(userInfo.contains("USER_ID")){
            ViewGroup loginLayout = (ViewGroup) loginButton.getParent();
            ViewGroup signupLayout = (ViewGroup) signupButton.getParent();
            loginLayout.removeView(loginButton);
            signupLayout.removeView(signupButton);
        }else {
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            });
            signupButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, web.class));
                }
            });
        }
        Button listButton = (Button) findViewById(R.id.list_but);

        listButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(HomeActivity.this, AddArticle.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.removeItem(R.id.action_home);
        return true;
    }
    @Override
    public void onFragmentInteraction(Uri uri){
    }
}
