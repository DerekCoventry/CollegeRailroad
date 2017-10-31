package com.example.derek.collegerailroad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        String username = "username123";
        String email = "test123@test.com";
        String name = "My Name";
        EditText editUsername = (EditText) findViewById(R.id.username);
        EditText editEmail = (EditText) findViewById(R.id.email);
        EditText editName = (EditText) findViewById(R.id.name);
        editUsername.setText(username);
        editEmail.setText(email);
        editName.setText(name);
    }
}
