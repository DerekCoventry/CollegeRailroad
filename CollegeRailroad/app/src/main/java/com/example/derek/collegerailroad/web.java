package com.example.derek.collegerailroad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class web extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webView = (WebView)findViewById(R.id.webView);
    //you can load an html code
        webView.loadData("yourCode Html to load on the webView " , "text/html" , "utf-8");
    // you can load an URL
        webView.loadUrl("http://www.collegerailroad.com/user/login");
    }
}
