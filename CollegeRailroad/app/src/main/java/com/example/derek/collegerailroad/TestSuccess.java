package com.example.derek.collegerailroad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    public String basicauth = "none";
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
            basicauth = extras.getString("BASIC_AUTH");


            //The key argument here must match that used in the other activity
        }
        if (true) {

        } else {
            user_id = savedInstanceState.getString("user_id");

            csrf = savedInstanceState.getString("csrf");

            logout = savedInstanceState.getString("logout");

            user_name = savedInstanceState.getString("user_name");
            basicauth = savedInstanceState.getString("BASIC_AUTH");



        }
        savedInstanceState.putString("user_id", user_id);
        savedInstanceState.putString("csrf", csrf);

        savedInstanceState.putString("user_name", user_name);

        savedInstanceState.putString("logout", logout);
        savedInstanceState.putString("basic_auth", basicauth);
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
        Button returnHome = findViewById(R.id.add_but);
        returnHome.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(TestSuccess.this, MainActivity.class));
            }
        });
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("user_id", user_id);
        savedInstanceState.putString("csrf", csrf);

        savedInstanceState.putString("user_name", user_name);

        savedInstanceState.putString("logout", logout);

    }
}
