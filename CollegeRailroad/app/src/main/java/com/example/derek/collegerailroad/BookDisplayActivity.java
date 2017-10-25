package com.example.derek.collegerailroad;
import android.app.Activity;
import android.content.Intent;
import android.content.Intent.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by derek on 10/25/17.
 */

public class BookDisplayActivity extends Activity {
    public String book_id = "26";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_display);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            book_id = extras.getString("BOOK_ID");
            //The key argument here must match that used in the other activity
        }
        new FetchBook().execute();
    }
    private class FetchBook extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... params) {


            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet("http://collegerailroad.com/node/"+book_id+"?_format=json");
            //set header to tell REST endpoint the request and response content types
            //httpget.setHeader("Accept", "application/json");
            //httpget.setHeader("Content-type", "application/json");

            JSONObject json = new JSONObject();

            try {

                HttpResponse response = httpclient.execute(httpget);

                //read the response and convert it into JSON array
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                //return the JSON array for post processing to onPostExecute function
                return json;



            }catch (Exception e) {
                Log.v("Error adding article",e.getMessage());
            }



            return json;
        }


        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONObject result) {
            TextView mTitleTextView;
            TextView mEmailTextView;
            TextView mLocationTextView;
            TextView mSubjectTextView;
            TextView mConditionTextView;

            BookPost currentBook;
            String curId;
            String curTitle;
            String curEmail;
            String curCondition;
            String curSubject;

            //iterate through JSON to read the title of nodes
            for(int i=0;i<result.length();i++){
                try {
                    JSONObject item = result;
                    JSONArray title = (JSONArray) item.get("title");
                    JSONArray vid = (JSONArray) item.get("vid");
                    JSONArray email = (JSONArray) item.get("field_email");
                    JSONArray condition = (JSONArray) item.get("field_condition");
                    JSONArray subject = (JSONArray) item.get("field_subject");
                    JSONObject valueVid = (JSONObject) vid.get(0);
                    if (title.length() > 0){
                        JSONObject valueTitle = (JSONObject) title.get(0);
                        curTitle = valueTitle.get("value").toString();
                        mTitleTextView = (TextView) findViewById(R.id.book_title);
                        if(curTitle.length() > 0 ){
                            mTitleTextView.setText(curTitle);
                        }
                    }
                    if (email.length() > 0 ){
                        JSONObject valueEmail = (JSONObject) email.get(0);
                        curEmail = valueEmail.get("value").toString();
                        mEmailTextView = (TextView) findViewById(R.id.book_email);
                        if(curEmail.length() > 0 ) {
                            mEmailTextView.setText(curEmail);
                        }

                    }
                    if (condition.length() > 0) {
                        JSONObject valueCondition = (JSONObject) condition.get(0);
                        curCondition = valueCondition.get("value").toString();
                        if(curCondition.length() > 0 ) {
                            mConditionTextView = (TextView) findViewById(R.id.book_condition);
                            mConditionTextView.setText(curCondition);
                        }
                    }
                    if (subject.length() > 0) {
                        JSONObject valueSubject = (JSONObject) subject.get(0);
                        curSubject = valueSubject.get("value").toString();
                        if(curSubject.length() > 0 ) {
                            mSubjectTextView = (TextView) findViewById(R.id.book_subject);
                            mSubjectTextView.setText(curSubject);
                        }

                    }
                    curId = valueVid.get("value").toString();





                } catch (Exception e) {
                    Log.v("Error adding database", e.getMessage());
                }
            }

        }
    }


}
