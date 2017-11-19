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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditArticle extends Activity implements AdapterView.OnItemSelectedListener {
    public File _file;
    public File _dir;
    public Bitmap bitmap;
    ImageView imageView;
    public String session_id;
    public String session_name;
    public String location = "Alabama";
    public String condition = "New";
    public String basicauth = "none";
    public String imageURL;
    public double latitude = 0, longitude = 0;
    private String uploadedImageUrl;
    private boolean usedCurLoc = false;
    Spinner locationSpin;
    Spinner conditionSpin;
    ArrayAdapter<CharSequence> adapter;
    ArrayAdapter<CharSequence> adapterc;
    public String user_id;
    public String book_id;
    private final String DIALOG = "dialog";
    private boolean showDialog = false;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            basicauth = savedInstanceState.getString("basic_auth");
            showDialog = savedInstanceState.getBoolean(DIALOG, false);
        }
        if(showDialog){
            mProgressDialog = mProgressDialog.show(EditArticle.this, "Loading", "Editing book...");
            showDialog = true;
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            book_id = extras.getString("BOOK_ID");
            //The key argument here must match that used in the other activity
        }
        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        if (userInfo.contains("USER_ID")) {
            user_id = userInfo.getString("USER_ID", "none");
            basicauth = userInfo.getString("BASIC_AUTH", "none");
        }
        Log.i("basic auth", basicauth);
        setContentView(R.layout.activity_add_article);
        new FetchBook(this).execute();
        locationSpin = (Spinner) findViewById((R.id.editlocation));
        adapter = ArrayAdapter.createFromResource(this,
                R.array.locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpin.setAdapter(adapter);
        locationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                location = parent.getItemAtPosition(position).toString();
                if(!usedCurLoc) {
                    usedCurLoc = false;
                    LatLng latLng = Location2.getStateCoordinates(EditArticle.this, location);
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        conditionSpin = (Spinner) findViewById((R.id.editcondition));
        adapterc = ArrayAdapter.createFromResource(this,
                R.array.conditions_array, android.R.layout.simple_spinner_item);
        adapterc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpin.setAdapter(adapterc);
        conditionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                condition = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // Camera functionality
        ImageButton cameraButton = (ImageButton) findViewById(R.id.book_camera);
        imageView = (ImageView) findViewById(R.id.book_photo);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                _dir = new File(Environment.getExternalStorageDirectory() + "/bookPhoto");
                if(!_dir.exists()){
                    _dir.mkdir();
                }
                _file = new File(Environment.getExternalStorageDirectory(), "bookPhoto/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(_file));

                startActivityForResult(intent, 0);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });
        Button locButton = (Button) findViewById(R.id.loc_but);

        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Location2 location2 = new Location2(EditArticle.this, getApplicationContext());
                    location = location2.getState();
                    latitude = location2.getLocation().latitude;
                    longitude = location2.getLocation().longitude;
                    if (!location.equals("")) {
                        usedCurLoc = true;
                        locationSpin.setSelection(adapter.getPosition(location));
                    }
                }catch(Exception e){
                    Toast.makeText(EditArticle.this, "Error getting location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (extras != null) {
            //retrieve the session_id and session_id passed by the previous activity
            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");
        }
    }

    public void showImage() {
        int newImageWidth = imageView.getWidth() * 2;
        int newImageHeight = imageView.getHeight() * 2;
        if(_file != null) {
            try {
                Bitmap bitmap2 = LoadAndResizeBitmap(_file.getAbsolutePath(), newImageWidth, newImageHeight);
                ImageView imageView2 = new ImageView(this);
                imageView2.setImageBitmap(bitmap2);
                Dialog builder = new Dialog(this);
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {}
                });
                builder.addContentView(imageView2, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                builder.show();
            }catch(Exception e){
            }
        }
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        location =  parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        location = "Alabama";
    }

    //click listener for edit button
    public void addArticleButton_click(View view){
        TextView txtTitle = (TextView) findViewById(R.id.editTitle);
        TextView txtAuthor = (TextView) findViewById(R.id.editAuthor);
        TextView txtEmail = (TextView) findViewById(
                R.id.editEmail);
        if(txtTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show();
        }else if(txtAuthor.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Author can't be empty", Toast.LENGTH_SHORT).show();
        }else if(txtEmail.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Email can't be empty", Toast.LENGTH_SHORT).show();
        }else if(!txtEmail.getText().toString().trim().matches("[^\\s]+@[^\\s]+")){
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
        }else {
            if (latitude == 0 && longitude == 0) {
                LatLng latLng = Location2.getStateCoordinates(EditArticle.this, location);
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
            new DeleteBook().execute();
            String fileName = "";
            if (_file != null) {
                fileName = _file.toString();
            }
            new addArticleTask().execute(session_name, session_id, fileName);
        }
    }


    //asynchronous task to add the article into Drupal
    private class addArticleTask extends AsyncTask<String, Void, Integer> {
        ProgressDialog mProgressDialog;
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(EditArticle.this, "Loading", "Editing book...");
            showDialog = true;
        }
        protected Integer doInBackground(String... params) {
            final String upload_to = "https://api.imgur.com/3/upload";

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(upload_to);
            try {
                HttpEntity entity = MultipartEntityBuilder.create()
                        .addPart("image", new FileBody(new File(params[2])))
                        .build();
                httpPost.setHeader("Authorization", "Client-ID 677813e7b6b7d5e");
                httpPost.setEntity(entity);

                final HttpResponse response = httpClient.execute(httpPost,
                        localContext);

                final String response_string = EntityUtils.toString(response
                        .getEntity());

                final JSONObject json = new JSONObject(response_string);

                Log.d("tag", json.toString());

                JSONObject data = json.optJSONObject("data");
                uploadedImageUrl = data.optString("link");
                Log.d("tag", "uploaded image url : " + uploadedImageUrl);
            } catch (Exception e) {
                uploadedImageUrl = imageURL;
                e.printStackTrace();
            }
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://collegerailroad.com/entity/node?_format=hal_json");



            try {
                //get title and body UI elements
                TextView txtTitle = (TextView) findViewById(R.id.editTitle);
                TextView txtAuthor = (TextView) findViewById(R.id.editAuthor);
                TextView txtEmail = (TextView) findViewById(R.id.editEmail);

                //extract text from UI elements and remove extra spaces
                String title=txtTitle.getText().toString().trim();
                String email=txtEmail.getText().toString().trim();
                String author = txtAuthor.getText().toString().trim();
                StringEntity se = new StringEntity("{\n" +
                        "\"_links\": {\n" +
                        "\"type\":{\n" +
                        "\"href\": \"http://collegerailroad.com/rest/type/node/basic_post\"\n" +
                        "}\n" +
                        "},\n" +
                        "\"title\": [\n" +
                        "{\n" +
                        "\"value\": \""+title+"\"\n" +
                        "}\n" +
                        "],\n" +
                        "\"type\": [\n" +
                        "{\n" +
                        "\"target_id\": \"basic_post\"\n" +
                        "}\n" +
                        "],\n" +
                        "\"field_author\": ["+
                        "{"+
                        " \"value\": \""+author+"\""+
                        "}"+
                        "],"+
                        "\"field_lat\": ["+
                        "{"+
                        " \"value\": \""+latitude+"\""+
                        "}"+
                        "],"+
                        "\"field_long\": ["+
                        "{"+
                        " \"value\": \""+longitude+"\""+
                        "}"+
                        "],"+
                        "\"field_title\": ["+
                        "{"+
                        " \"value\": \""+uploadedImageUrl+"\""+
                        "}"+
                        "],"+
                        "\"field_email\": [\n" +
                        "{\n" +
                        "\"value\": \""+email+"\"\n" +
                        "}\n" +
                        "],\n\"field_state\": ["+
                        "{\"target_id\":"+ getTaxLocation(location)+","+
                        "\"target_type\": \"taxonomy_term\","+
                        "\"url\": \"/taxonomy/term/"+getTaxLocation(location)+"\""+
                        " }],\"field_condition\": ["+
                        "{\"target_id\":"+ getTaxCondition(condition)+","+
                        "\"target_type\": \"taxonomy_term\","+
                        "\"url\": \"/taxonomy/term/"+getTaxCondition(condition)+"\""+
                        " }]"+
                        "}");
                httppost.setEntity(se);
                httppost.setHeader("Accept", "application/hal+json");
                httppost.setHeader("Content-Type", "application/hal+json");
                httppost.setHeader("Authorization", "basic " + basicauth);
                httpclient.execute(httppost);
                return 0;

            }catch (Exception e) {
                Log.v("Error adding article",e.getMessage());
            }

            return 0;
        }
        protected String getTaxLocation(String location) {
            String[] states = new String[]{"Alabama","Alaska","Alaska Fairbanks","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"};
            for (int i = 0; i< 51; i++) {
                if (location.equals(states[i])) {
                    return Integer.toString((i + 12));
                }
            }
            return Integer.toString(12);
        }
        protected String getTaxCondition(String location) {
            String[] conditions = new String[]{"New", "Good",  "Worn", "Damaged"};
            for (int i = 0; i < 4; i++) {
                if (location.equals(conditions[i])) {
                    return Integer.toString((i + 3));
                }
            }
            return Integer.toString(3);
        }
        protected void onPostExecute(Integer result) {

            //start the List Activity and pass back the session_id and session_name
            Intent intent = new Intent(EditArticle.this, BookListActivitySelf.class);
            try{
                mProgressDialog.dismiss();
            }catch (Exception e){

            }
            showDialog = false;
            Toast.makeText(EditArticle.this, "Book updated!", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(_file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
            int height = imageView.getWidth();
            int width = imageView.getHeight();
            bitmap = LoadAndResizeBitmap(_file.getAbsolutePath(), width, height);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                bitmap = null;
            }
        }
    }

    public static Bitmap LoadAndResizeBitmap(String fileName, int width, int height)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int inSampleSize = 1;
        if (outHeight > height || outWidth > width)
        {
            inSampleSize = outWidth > outHeight ? outHeight / height : outWidth / width;
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap resizedBitmap = BitmapFactory.decodeFile(fileName, options);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix, true);
    }

    private class FetchBook extends AsyncTask<String, Void, JSONObject> {
        EditArticle mActivity;
        public FetchBook(EditArticle a){
            this.mActivity = a;
        }

        protected JSONObject doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://collegerailroad.com/node/"+book_id+"?_format=json");
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
            EditText mTitleTextView;
            EditText mEmailTextView;
            EditText mAuthorTextView;
            Spinner mConditionTextView;

            String curTitle;
            String curAuthor;
            String curEmail;
            String curCondition;
            String curURL;
            String curLatitude;
            String curLongitude;
            try {
                JSONObject item = result;
                JSONArray title = (JSONArray) item.get("title");
                JSONArray email = (JSONArray) item.get("field_email");
                JSONArray author = (JSONArray) item.get("field_author");
                JSONArray condition = (JSONArray) item.get("field_condition");
                JSONArray url = (JSONArray) item.get("field_title");
                JSONArray latitude = (JSONArray) item.get("field_lat");
                JSONArray longitude = (JSONArray) item.get("field_long");
                if (title.length() > 0){
                    JSONObject valueTitle = (JSONObject) title.get(0);
                    curTitle = valueTitle.get("value").toString();
                    mTitleTextView = (EditText) findViewById(R.id.editTitle);
                    if(curTitle.length() > 0 ){
                        mTitleTextView.setText(curTitle);
                    }
                }
                if (author.length() > 0){
                    JSONObject valueAuthor = (JSONObject) author.get(0);
                    curAuthor = valueAuthor.get("value").toString();
                    mAuthorTextView = (EditText) findViewById(R.id.editAuthor);
                    if(curAuthor.length() > 0 ){
                        mAuthorTextView.setText(curAuthor);
                    }
                }
                if (email.length() > 0 ){
                    JSONObject valueEmail = (JSONObject) email.get(0);
                    curEmail = valueEmail.get("value").toString();
                    mEmailTextView = (EditText) findViewById(R.id.editEmail);
                    if(curEmail.length() > 0 ) {
                        mEmailTextView.setText(curEmail);
                    }

                }
                if (condition.length() > 0) {
                    JSONObject valueCondition = (JSONObject) condition.get(0);
                    curCondition = valueCondition.get("target_id").toString();
                    String[] conditions = new String[]{"New", "Good",  "Worn","Damaged"};
                    if(curCondition.length() > 0 ) {
                        conditionSpin.setSelection(adapterc.getPosition(conditions[Integer.parseInt(curCondition) - 3]));
                    }
                }
                if(latitude.length() > 0 && longitude.length() > 0){
                    JSONObject valueLatitude = (JSONObject) latitude.get(0);
                    JSONObject valueLongitude = (JSONObject) longitude.get(0);
                    curLatitude = valueLatitude.get("value").toString();
                    curLongitude = valueLongitude.get("value").toString();
                    String cityState = Location2.getCityState(EditArticle.this, new LatLng(Double.parseDouble(curLatitude), Double.parseDouble(curLongitude)));
                    String state = cityState.substring(cityState.indexOf(",")+2, cityState.length());
                    usedCurLoc = true;
                    locationSpin.setSelection(adapter.getPosition(state));
                    try{
                        mActivity.setLatLng(new LatLng(Double.parseDouble(curLatitude), Double.parseDouble(curLongitude)));
                    }catch (Exception e){

                    }
                }

                if (url.length() > 0 ){
                    JSONObject valueURL = (JSONObject) url.get(0);
                    curURL = valueURL.get("value").toString();
                    if(curURL.length() > 0 && curURL.contains("imgur")) {
                        imageURL = curURL;
                        new loadImage().execute();
                    }else{
                        imageURL = "none";
                    }
                }else{
                    imageURL = "none";
                }


            } catch (Exception e) {
                Log.v("Error adding database", e.getMessage());
            }

        }
    }
    private class DeleteBook extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpget = new HttpDelete("http://collegerailroad.com/node/"+book_id+"?_format=hal_json");
            httpget.setHeader("Content-Type", "application/hal+json");
            httpget.setHeader("Authorization", "basic " + basicauth);
            JSONObject json = new JSONObject();
            try {
                httpclient.execute(httpget);
                return json;
            }catch (Exception e) {
                Log.v("Error adding article",e.getMessage());
            }
            return json;
        }

        protected void onPostExecute(JSONObject result) {}
    }

    class loadImage extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(EditArticle.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading Image...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final ImageView mURLImageView = (ImageView) findViewById(R.id.book_photo);
            try {
                InputStream i = (InputStream)new URL(imageURL).getContent();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                try {
                    bitmap = BitmapFactory.decodeStream(i, null, options);
                }catch(OutOfMemoryError e){
                    Toast.makeText(EditArticle.this, "Out of memory", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mURLImageView.setImageBitmap(bitmap);
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
            pdLoading.dismiss();
            return null;
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mProgressDialog != null){
            try {
                mProgressDialog.dismiss();
            }catch(Exception e){

            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(DIALOG, showDialog);
    }

    public void setLatLng(LatLng latLng){
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }
}