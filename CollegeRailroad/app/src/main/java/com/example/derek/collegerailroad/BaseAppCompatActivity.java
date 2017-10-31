package com.example.derek.collegerailroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //If signed in remove sign in and sign up
        //If signed out remove sign out
        return true;
    }

    public void goToHome(MenuItem item){
        startActivity(new Intent(this,MainActivity.class));
    }

    public void goToProfile(MenuItem item){
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void goToSignIn(MenuItem item){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void goToSignUp(MenuItem item){
        startActivity(new Intent(this, web.class));
    }

    public void signOut(MenuItem item){
        //Sign out
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
    }
}
