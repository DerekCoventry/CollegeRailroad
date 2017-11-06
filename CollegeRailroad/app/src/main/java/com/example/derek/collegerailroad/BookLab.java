package com.example.derek.collegerailroad;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by derek on 10/24/17.
 */

public class BookLab {
    private static BookLab sBookLab;
    private List<BookPost> mBooks;

    public static BookLab get(Context context) {
        if (sBookLab == null) {
            sBookLab = new BookLab(context);
        }
        return sBookLab;
    }

    private BookLab(Context context) {
    }

    public List<BookPost> getBooks() {
        return mBooks;
    }

    public BookPost getBook(UUID id) {
        for (BookPost book : mBooks) {
            if (book.getId().equals(id)) {
                return book;
            }
        }

        return null;
    }
    private class FetchBooks extends AsyncTask<String, Void, JSONArray> {

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
            mBooks = new ArrayList<BookPost>();
            BookPost currentBook;
            String curId;
            String curTitle;
            String curEmail;
            String curCondition;
            String curAuthor;
            LatLng latLng;

            //iterate through JSON to read the title of nodes
            for(int i=0;i<result.length();i++){
                try {
                    JSONObject item = (JSONObject) result.get(i);
                    JSONArray title = (JSONArray) item.get("title");
                    JSONArray vid = (JSONArray) item.get("vid");
                    JSONArray email = (JSONArray) item.get("field_email");
                    JSONArray condition = (JSONArray) item.get("field_condition");
                    JSONArray author = (JSONArray) item.get("field_author");
                    JSONArray latitude = (JSONArray) item.get("field_lat");
                    JSONArray longitude = (JSONArray) item.get("field_long");
                    JSONObject valueCondition = (JSONObject) condition.get(0);
                    JSONObject valueVid = (JSONObject) vid.get(0);
                    JSONObject valueTitle = (JSONObject) title.get(0);
                    JSONObject valueEmail = (JSONObject) email.get(0);
                    JSONObject valueAuthor = (JSONObject) author.get(0);
                    JSONObject valueLatitude = (JSONObject) latitude.get(0);
                    JSONObject valueLongitude = (JSONObject) longitude.get(0);
                    curId = valueVid.get("value").toString();
                    curTitle = valueTitle.get("value").toString();
                    curEmail = valueEmail.get("value").toString();
                    curAuthor = valueAuthor.get("value").toString();
                    latLng = new LatLng(Double.parseDouble(valueLatitude.get("value").toString()), Double.parseDouble(valueLongitude.get("value").toString()));
                    curCondition = valueCondition.get("target_condition").toString();
                    currentBook = new BookPost(curId, curTitle, curAuthor, curEmail, curCondition, latLng);
                    mBooks.add(currentBook);
                } catch (Exception e) {
                    Log.v("Error adding database", e.getMessage());
                }
            }

        }
    }
}
