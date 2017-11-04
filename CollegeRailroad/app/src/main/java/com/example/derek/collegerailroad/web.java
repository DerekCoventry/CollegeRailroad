package com.example.derek.collegerailroad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class web extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        final String regUrl = "http://www.collegerailroad.com/user/register";
        WebView webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.matches("^.*?(zymph|password|login).*$")) {
                    Toast.makeText(web.this, "You can only sign up here", Toast.LENGTH_SHORT).show();
                    return true;
                }else if(url.contains("register")){
                    return true;
                }else {
                    startActivity(new Intent(web.this, HomeActivity.class));
                    return false;
                }
            }
        });
        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        if(userInfo.contains("USER_ID")){
            Toast.makeText(web.this, "User signed in, can't sign up", Toast.LENGTH_SHORT).show();
            this.finish();
        }else{
            //you can load an html code
            webView.loadData("yourCode Html to load on the webView " , "text/html" , "utf-8");
            // you can load an URL
            webView.loadUrl(regUrl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_sign_up);
        menu.removeItem(R.id.action_sign_out);
        menu.removeItem(R.id.action_profile);
        return true;
    }
}
