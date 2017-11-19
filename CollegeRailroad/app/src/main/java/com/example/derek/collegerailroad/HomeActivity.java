package com.example.derek.collegerailroad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends BaseAppCompatActivity implements SearchFragment.OnFragmentInteractionListener{
    private static String OPTION = "option";
    private String starting_opt;
    private String search_opt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            starting_opt = savedInstanceState.getString(OPTION, "ISBN");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_home);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.search_layout, new SearchFragment()).commit();
        Spinner opt = (Spinner) findViewById((R.id.editcondition));
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.search_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opt.setAdapter(adapter);
        opt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                search_opt = parent.getItemAtPosition(position).toString();
                ((SearchFragment)getSupportFragmentManager().findFragmentById(R.id.search_layout)).changeSearch(search_opt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                search_opt = starting_opt;
            }
        });
        if(starting_opt != null) {
            int position = adapter.getPosition(starting_opt);
            opt.setSelection(position);
        }
        TextView intro = (TextView) findViewById(R.id.intro);
        intro.setText("Welcome to College Railroad! This app allows students to buy/sell books with each other without the bookstore middleman markup. Current apps/websites exist for the sole purpose of making money. Businesses and bookstores make excessive amounts of money off of students, and for a very long time students have accepted this as a way of life. Additionally, Facebook pages, and craigslist posts try to accomplish our goal, but the market it waiting for an app to satisfy the need. Having an app be created by potential users allows for an alternative view on the textbook market.");
        Button loginButton = (Button) findViewById(R.id.log_but);
        Button signupButton = (Button) findViewById(R.id.signup_but);
        final SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        if(userInfo.contains("USER_ID")){
            ViewGroup loginLayout = (ViewGroup) loginButton.getParent();
            ViewGroup signupLayout = (ViewGroup) signupButton.getParent();
            loginLayout.removeView(loginButton);
            signupLayout.removeView(signupButton);
        }else {
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            });
            signupButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, web.class));
                }
            });
        }
        Button listButton = (Button) findViewById(R.id.list_but);

        listButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(userInfo.contains("USER_ID")) {
                    startActivity(new Intent(HomeActivity.this, AddArticle.class));
                }else{
                    Toast.makeText(HomeActivity.this, "You must be signed in to sell books", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.removeItem(R.id.action_home);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(OPTION, search_opt);
    }

    @Override
    public void onFragmentInteraction(Uri uri){
    }
}
