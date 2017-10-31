package com.example.derek.collegerailroad;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
