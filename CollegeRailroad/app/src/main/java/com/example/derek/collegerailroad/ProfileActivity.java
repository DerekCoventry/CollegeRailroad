package com.example.derek.collegerailroad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends BaseAppCompatActivity {
    public String user_id = "default";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Button self_item = (Button) findViewById(R.id.self_items);
        self_item.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ProfileActivity.this, BookListActivitySelf.class);
                SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
                user_id = userInfo.getString("USER_ID", "none");
                intent.putExtra("USER_ID", user_id);
                startActivity(intent);
            }
        });



        TextView profile = (TextView) findViewById(R.id.profile);
        Button editProfileButton = (Button) findViewById(R.id.profile_but);
        Button profileButton = (Button) findViewById(R.id.profile_but);
        profileButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });
        final StringBuilder builder = new StringBuilder();
        String username = "username123";
        String email = "test123@test.com";
        String name = "My Name";
        builder.append("Username: " + username + "\n");
        builder.append("Email: " + email + "\n");
        builder.append("Name: " + name);
        profile.setText(builder.toString());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.removeItem(R.id.action_profile);
        return true;
    }

}
