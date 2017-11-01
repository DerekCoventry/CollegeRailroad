package com.example.derek.collegerailroad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        if(userInfo.contains("USER_ID")){
            menu.removeItem(R.id.action_sign_up);
            menu.removeItem(R.id.action_sign_in);
        }else{
            menu.removeItem(R.id.action_sign_out);
        }
        return true;
    }

    public void goToHome(MenuItem item){
        startActivity(new Intent(this,HomeActivity.class));
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
        // If logout preferences exist
        //Sign out GET to collegerailsroad.com/user/logout
        //Set preferences to null
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
    }
}
