package com.example.derek.collegerailroad;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by derek on 10/28/17.
 */

public class TestSuccess extends Activity {
    public String session_name;
    public String session_id;
    public String user_id = "none";
    public String csrf = "none";
    public String logout = "none";
    public String user_name = "none";
    public String raw = "none";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_success);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user_id = extras.getString("USER_ID");
            user_name = extras.getString("USER_NAME");

            csrf = extras.getString("CSRF");

            logout = extras.getString("LOGOUT");
            raw = extras.getString("RAW");

            //The key argument here must match that used in the other activity
        }
        if (true) {

        } else {
            user_id = savedInstanceState.getString("user_id");

            csrf = savedInstanceState.getString("csrf");

            logout = savedInstanceState.getString("logout");

            user_name = savedInstanceState.getString("user_name");



        }
        TextView userName = (TextView) findViewById(R.id.user_name);
        userName.setText(user_name);
        TextView logoutText = (TextView) findViewById(R.id.logout);
        logoutText.setText(logout);
        TextView csrfText = (TextView) findViewById(R.id.csrf);
        csrfText.setText(csrf);
        TextView user = (TextView) findViewById(R.id.user_id);
        user.setText(user_id);
        TextView rawText = (TextView) findViewById(R.id.raw);
        rawText.setText(raw);
    }
}
