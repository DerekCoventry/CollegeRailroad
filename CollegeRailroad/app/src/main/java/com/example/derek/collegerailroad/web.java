package com.example.derek.collegerailroad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

public class web extends BaseAppCompatActivity {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_sign_up);
        return true;
    }
}
