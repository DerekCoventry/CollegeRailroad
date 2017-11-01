package com.example.derek.collegerailroad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
            menu.removeItem(R.id.action_profile);
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
        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        final String logoutUrl = "http://www.collegerailroad.com/user/logout";
        WebView webView = new WebView(this);
        webView.setVisibility(View.GONE);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                startActivity(new Intent(BaseAppCompatActivity.this, HomeActivity.class));
                return false;
            }
        });
        //you can load an html code
        webView.loadData("yourCode Html to load on the webView " , "text/html" , "utf-8");
        // you can load an URL
        webView.loadUrl(logoutUrl);
        if(userInfo.contains("USER_ID")) {
            SharedPreferences.Editor userInfoEditor = userInfo.edit();
            userInfoEditor.putString("USER_ID", null);
            userInfoEditor.putString("USER_NAME", null);
            userInfoEditor.putString("CSRF", null);
            userInfoEditor.putString("LOGOUT", null);
            userInfoEditor.putString("RAW", null);
            userInfoEditor.putString("BASIC_AUTH", null);
            userInfoEditor.commit();
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
        }else{
            Toast.makeText(this, "Already signed out", Toast.LENGTH_SHORT).show();
        }
    }
}
