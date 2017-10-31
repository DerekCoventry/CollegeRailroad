package com.example.derek.collegerailroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfileActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Button updateProfile = (Button) findViewById(R.id.update_profile_but);
        updateProfile.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // If profile was updated
                Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
            }
        });
        String username = "username123";
        String email = "test123@test.com";
        String name = "My Name";
        EditText editUsername = (EditText) findViewById(R.id.username);
        EditText editEmail = (EditText) findViewById(R.id.email);
        EditText editName = (EditText) findViewById(R.id.name);
        editUsername.setText(username);
        editEmail.setText(email);
        editName.setText(name);
        Button changePassword = (Button) findViewById(R.id.change_password_but);
        changePassword.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                EditText currentPassword = (EditText) findViewById(R.id.current_password);
                EditText newPassword = (EditText) findViewById(R.id.new_password);
                EditText confirmPassword = (EditText) findViewById(R.id.confirm_password);
                // If password was changed
                Toast.makeText(EditProfileActivity.this, "Password Changed!", Toast.LENGTH_SHORT).show();
                currentPassword.setText("");
                newPassword.setText("");
                confirmPassword.setText("");

            }
        });
    }
}
