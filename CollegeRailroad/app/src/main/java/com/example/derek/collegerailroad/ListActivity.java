package com.example.derek.collegerailroad;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.os.*;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.*;
import android.content.*;
import android.widget.*;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.*;

public class ListActivity extends  SingleFragmentActivity {

    public String session_id;
    public String session_name;
    @Override
    protected Fragment createFragment() {
        return new BookListFragment();
    }
    private class FetchItems extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... params) {


            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet("http://collegerailroad.com/appview?_format=json");
            //set header to tell REST endpoint the request and response content types
            //httpget.setHeader("Accept", "application/json");
            //httpget.setHeader("Content-type", "application/json");

            JSONArray json = new JSONArray();

            try {

                HttpResponse response = httpclient.execute(httpget);

                //read the response and convert it into JSON array
                json = new JSONArray(EntityUtils.toString(response.getEntity()));
                //return the JSON array for post processing to onPostExecute function
                return json;



            }catch (Exception e) {
                Log.v("Error adding article",e.getMessage());
            }



            return json;
        }


        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONArray result) {
            List<BookPost> books = new ArrayList<BookPost>();
            BookPost currentBook;
            String curId;
            String curTitle;
            String curAuthor;
            String curEmail;
            String curCondition;
            LatLng latLng;
            String curURL;
            //get the ListView UI element
            //ListView lst = (ListView)  findViewById(R.id.listView);

            //create the ArrayList to store the titles of nodes
            ArrayList<String> listItems=new ArrayList<String>();

            //iterate through JSON to read the title of nodes
            for(int i=0;i<result.length();i++){
                try {
                    JSONObject item = (JSONObject) result.get(i);
                    JSONArray title = (JSONArray) item.get("title");
                    JSONArray author = (JSONArray) item.get("field_author");
                    JSONArray vid = (JSONArray) item.get("vid");
                    JSONArray email = (JSONArray) item.get("field_email");
                    JSONArray condition = (JSONArray) item.get("field_condition");
                    JSONArray latitude = (JSONArray) item.get("field_lat");
                    JSONArray longitude = (JSONArray) item.get("field_long");
                    JSONArray url = (JSONArray) item.get("field_title");
                    JSONObject valueVid = (JSONObject) vid.get(0);
                    JSONObject valueTitle = (JSONObject) title.get(0);
                    JSONObject valueAuthor = (JSONObject) author.get(0);
                    JSONObject valueEmail = (JSONObject) email.get(0);
                    JSONObject valueCondition = (JSONObject) condition.get(0);
                    JSONObject valueLatitude = (JSONObject) latitude.get(0);
                    JSONObject valueLongitude = (JSONObject) longitude.get(0);
                    JSONObject valueURL = (JSONObject) url.get(0);
                    curId = valueVid.get("value").toString();
                    curTitle = valueTitle.get("value").toString();
                    curAuthor = valueAuthor.get("value").toString();
                    curEmail = valueEmail.get("value").toString();
                    curURL = valueURL.get("value").toString();
                    curCondition = valueCondition.get("target_id").toString();
                    latLng = new LatLng(Double.parseDouble(valueLatitude.get("value").toString()), Double.parseDouble(valueLongitude.get("value").toString()));
                    currentBook = new BookPost(curId, curTitle, curAuthor, curEmail, curCondition, latLng, curURL);
                    listItems.add(curTitle);

                    books.add(currentBook);
                } catch (Exception e) {
                    Log.v("Error adding article", e.getMessage());
                }
            }

            //create array adapter and give it our list of nodes, pass context, layout and list of items
            ArrayAdapter ad= new ArrayAdapter(ListActivity.this, android.R.layout.simple_list_item_1,listItems);

            //give adapter to ListView UI element to render
            //lst.setAdapter(ad);
        }
    }

    //click listener for addArticleButton
    public void addArticleButton_click(View view){

        //create intent to start AddArticle activity
        Intent intent = new Intent(this, AddArticle.class);
        //pass the session information
        intent.putExtra("SESSION_ID", session_id);
        intent.putExtra("SESSION_NAME", session_name);
        //start the AddArticle activity
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        if(userInfo.contains("USER_ID")){
            menu.removeItem(R.id.action_sign_up);
            menu.removeItem(R.id.action_sign_in);
        }else{
            menu.removeItem(R.id.action_profile);
            menu.removeItem(R.id.action_sign_out);
        }
        return true;
    }

}