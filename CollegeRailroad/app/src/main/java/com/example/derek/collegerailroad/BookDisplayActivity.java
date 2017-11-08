package com.example.derek.collegerailroad;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by derek on 10/25/17.
 */

public class BookDisplayActivity extends Activity {
    public String book_id = "26";
    public String[] states = new String[]{"Alabama","Alaska","Alaska Fairbanks","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"};
    public String user_id = "none";
    public String[] cond = new String[]{"New", "Good",  "Worn","Damaged"};

    public ImageView img;

    public String imageURL;

    public Button delete_button;
    public Button edit_button;
    public String basicauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_display);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            book_id = extras.getString("BOOK_ID");
            //The key argument here must match that used in the other activity
        }
        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        if (userInfo.contains("USER_ID")) {
            basicauth = userInfo.getString("BASIC_AUTH", "none");
            user_id = userInfo.getString("USER_ID", "none");
        }
        delete_button = (Button) findViewById(R.id.delete_but);
        delete_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                new DeleteBook().execute();
                Intent intent = new Intent(BookDisplayActivity.this, BookListActivitySelf.class);

                startActivity(intent);
            }
        });
        edit_button = (Button) findViewById(R.id.edit_but);
        edit_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(BookDisplayActivity.this, EditArticle.class);
                intent.putExtra("BOOK_ID", book_id);
                startActivity(intent);
            }
        });
        img = (ImageView) findViewById(R.id.book_photo);

        new FetchBook().execute();
    }
    class loadImage extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(BookDisplayActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading Image...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final ImageView mURLImageView = (ImageView) findViewById(R.id.book_photo);
            final TextView mNoImageView = (TextView) findViewById(R.id.no_photo);
            try {
                InputStream i = (InputStream)new URL(imageURL).getContent();
                final Bitmap bitmap = BitmapFactory.decodeStream(i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mURLImageView.setImageBitmap(bitmap);
                    }
                });
            }catch(Exception e){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mURLImageView.setVisibility(View.GONE);
                        mNoImageView.setVisibility(View.VISIBLE);
                    }
                });
                e.printStackTrace();
            }
            pdLoading.dismiss();
            return null;
        }
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
            TextView mAuthorTextView;
            ImageView mURLImageView;
            TextView mNoImageView;


            BookPost currentBook;
            String curId;
            String curTitle;
            String curEmail;
            String curCondition;
            String curSubject;
            String curAuthor;
            String curLocation;
            String curUID;
            String curURL;

            //iterate through JSON to read the title of nodes
                try {
                    JSONObject item = result;
                    JSONArray title = (JSONArray) item.get("title");
                    JSONArray vid = (JSONArray) item.get("vid");
                    JSONArray email = (JSONArray) item.get("field_email");
                    JSONArray author = (JSONArray) item.get("field_author");
                    JSONArray condition = (JSONArray) item.get("field_condition");
                    JSONArray subject = (JSONArray) item.get("field_subject");
                    JSONArray location = (JSONArray) item.get("field_state");
                    JSONArray url = (JSONArray) item.get("field_title");
                    JSONArray user= (JSONArray) item.get("uid");
                    JSONObject valueUID = (JSONObject) user.get(0);
                    curUID = valueUID.get("target_id").toString();
                    if (curUID.equals(user_id)){
                        edit_button.setVisibility(View.VISIBLE);
                        delete_button.setVisibility(View.VISIBLE);
                    }
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
                    if (author.length() > 0 ){
                        JSONObject valueAuthor = (JSONObject) author.get(0);
                        curAuthor = valueAuthor.get("value").toString();
                        mAuthorTextView = (TextView) findViewById(R.id.book_author);
                        if(curAuthor.length() > 0 ) {
                            mAuthorTextView.setText(curAuthor);
                        }

                    }
                    if (condition.length() > 0) {
                        JSONObject valueCondition = (JSONObject) condition.get(0);
                        curCondition = valueCondition.get("target_id").toString();
                        if(curCondition.length() > 0 ) {
                            mConditionTextView = (TextView) findViewById(R.id.book_condition);
                            mConditionTextView.setText(cond[Integer.parseInt(curCondition)-3]);
                        }
                    }
                    mURLImageView = (ImageView) findViewById(R.id.book_photo);
                    mNoImageView = (TextView) findViewById(R.id.no_photo);
                    if (url.length() > 0 ){
                        JSONObject valueURL = (JSONObject) url.get(0);
                        curURL = valueURL.get("value").toString();
                        if(curURL.length() > 0 && curURL.contains("imgur")) {
                            imageURL = curURL;
                            new loadImage().execute();
                        }else{
                            mURLImageView.setVisibility(View.GONE);
                            mNoImageView.setVisibility(View.VISIBLE);
                            imageURL = "none";
                        }
                    }else{
                        mURLImageView.setVisibility(View.GONE);
                        mNoImageView.setVisibility(View.VISIBLE);
                        imageURL = "none";
                    }

                    Log.d("loc", location.toString());
                    if (location.length() > 0) {
                        JSONObject locationSubject = (JSONObject) location.get(0);
                        curLocation = locationSubject.get("target_id").toString();
                        if(curLocation.length() > 0 ) {
                            mLocationTextView = (TextView) findViewById(R.id.book_location);
                            mLocationTextView.setText(states[Integer.parseInt(curLocation)-12]);
                        }

                    }
                    curId = valueVid.get("value").toString();





                } catch (Exception e) {
                    Log.v("Error adding database", e.getMessage());
                }

        }
    }

    private class DeleteBook extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... params) {


            HttpClient httpclient = new DefaultHttpClient();

            HttpDelete httpget = new HttpDelete("http://collegerailroad.com/node/"+book_id+"?_format=hal_json");
            //set header to tell REST endpoint the request and response content types
            //httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-Type", "application/hal_json");
            httpget.setHeader("Authorization", "basic " + basicauth);

            JSONObject json = new JSONObject();

            try {

                httpclient.execute(httpget);

                //read the response and convert it into JSON array
                //return the JSON array for post processing to onPostExecute function
                return json;



            }catch (Exception e) {
                Log.v("Error adding article",e.getMessage());
            }



            return json;
        }


        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONObject result) {



        }
    }

}
