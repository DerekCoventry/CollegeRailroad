package com.example.derek.collegerailroad;

import android.*;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddArticle extends FragmentActivity implements AdapterView.OnItemSelectedListener, SearchFragment.OnFragmentInteractionListener {
    public static File _file;
    public static File _dir;
    public static Bitmap bitmap;
    ImageView imageView;
    public String session_id;
    public String session_name;
    public String location = "Alabama";
    public String condition = "New";
    public String basicauth = "none";
    public double latitude = 0, longitude = 0;
    public String link;
    Spinner locationSpin;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            basicauth = savedInstanceState.getString("basic_auth");
        }
        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        basicauth = userInfo.getString("BASIC_AUTH", "none");
        Log.i("basic auth", basicauth);
        setContentView(R.layout.activity_add_article);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.autofill_layout, new SearchFragment()).commit();
        Bundle extras = getIntent().getExtras();
        locationSpin = (Spinner) findViewById((R.id.editlocation));
        adapter = ArrayAdapter.createFromResource(this,
                R.array.locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpin.setAdapter(adapter);
        locationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                location = parent.getItemAtPosition(position).toString();
                LatLng latLng = Location2.getStateCoordinates(AddArticle.this, location);
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner conditionSpin = (Spinner) findViewById((R.id.editcondition));
        ArrayAdapter<CharSequence> adapterc = ArrayAdapter.createFromResource(this,
                R.array.conditions_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpin.setAdapter(adapterc);
        conditionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                condition = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Camera functionality
        ImageButton cameraButton = (ImageButton) findViewById(R.id.book_camera);
        imageView = (ImageView) findViewById(R.id.book_photo);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(AddArticle.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( AddArticle.this, new String[] {  android.Manifest.permission.WRITE_EXTERNAL_STORAGE  },
                            11 );
                }
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    _dir = new File(
                            Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES), "AddArticle");

                    _file = new File(_dir, "bookPhoto/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(_file));

                    startActivityForResult(intent, 0);
                }catch(Exception e){
                    Toast.makeText(AddArticle.this, "Error uploading photo", Toast.LENGTH_SHORT).show();
                }
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
                Location2 location2 = new Location2(AddArticle.this, getApplicationContext());
                location = location2.getState();
                latitude = location2.getLocation().latitude;
                longitude = location2.getLocation().longitude;
                if(location != "") {
                    locationSpin.setSelection(adapter.getPosition(location));
                }
            }
        });

        if (extras != null) {
            //retrieve the session_id and session_id passed by the previous activity
            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        FragmentManager fm = getSupportFragmentManager();

        SearchFragment fragment = (SearchFragment) fm.findFragmentById(R.id.autofill_layout);
        fragment.changeSearch("Enter ISBN to autofill");
    }

    public void setTitleAndAuthor(String title, String author){
        EditText txtTitle = (EditText) findViewById(R.id.editTitle);
        EditText txtAuthor = (EditText) findViewById(R.id.editAuthor);
        if(title.isEmpty() && author.isEmpty()) {
            Toast.makeText(this, "No book found with that ISBN", Toast.LENGTH_SHORT).show();
        }else {
            txtTitle.setText(title);
            txtAuthor.setText(author);
        }
    }

    public void showImage() {
        //ImageView imageView2 = new ImageView(this);
        //imageView2.setImageURI(Uri.fromFile(_file));
        WindowManager mWinMgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        float displayWidth = mWinMgr.getDefaultDisplay().getWidth();
        float displayHeight = mWinMgr.getDefaultDisplay().getHeight();
        int newImageWidth = imageView.getWidth() * 2;
        int newImageHeight = imageView.getHeight() * 2;;
        Bitmap bitmap2 = LoadAndResizeBitmap(_file.getAbsolutePath(), newImageWidth, newImageHeight);
        ImageView imageView2 = new ImageView(this);
        imageView2.setImageBitmap(bitmap2);
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });
        builder.addContentView(imageView2, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        location =  parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        location = "Alabama";
    }

    //click listener for addArticle button
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
        }else {
            if(latitude == 0 && longitude == 0){
                LatLng latLng = Location2.getStateCoordinates(AddArticle.this, location);
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
            //initiate the background process to post the article to the Drupal endpoint.
            //pass session_name and session_id
            new addArticleTask().execute(session_name, session_id);
        }
    }


    //asynchronous task to add the article into Drupal
    private class addArticleTask extends AsyncTask<String, Void, Integer> {

        protected Integer doInBackground(String... params) {

            //read session_name and session_id from passed parameters
            String session_name=params[0];
            String session_id=params[1];


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
                //add raw json to be sent along with the HTTP POST request
                //StringEntity se = new StringEntity( "{\"_links\": {\"type\":{"+
                //        "\"href\": \"http://collegerailroad.com/rest/type/node/basic_post\"}},\"title\": [{\"value\": \""+ title + "\""+
                //        "}],\"type\": [{\"target_id\": \"basic_post\"}],\"field_email\": [{\"value\": \""+email+"\"}]}");
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
                        "\"field_link\": ["+
                        "{"+
                        " \"value\": \""+link+"\""+
                        "}"+
                        "],"+
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



                BasicHttpContext mHttpContext = new BasicHttpContext();
                CookieStore mCookieStore      = new BasicCookieStore();

                //create the session cookie
                /*BasicClientCookie cookie = new BasicClientCookie(session_name, session_id);
                cookie.setVersion(0);
                cookie.setDomain(".collegerailroad.com");
                cookie.setPath("/");
                mCookieStore.addCookie(cookie);
                cookie = new BasicClientCookie("has_js", "1");
                mCookieStore.addCookie(cookie);
                mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);*/
                httpclient.execute(httppost);
                //httpclient.execute(httppost,mHttpContext);
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
            String[] states = new String[]{"New", "Good",  "Worn","Damaged"};
            for (int i = 0; i< 3; i++) {
                if (location.equals(states[i])) {
                    return Integer.toString((i + 3));
                }
            }
            return Integer.toString(3);
        }
        protected void onPostExecute(Integer result) {

            //start the List Activity and pass back the session_id and session_name
            Intent intent = new Intent(AddArticle.this, HomeActivity.class);
            Toast.makeText(AddArticle.this, "Book listed!", Toast.LENGTH_SHORT).show();
            //intent.putExtra("SESSION_ID", session_id);
            //intent.putExtra("SESSION_NAME", session_name);
            startActivity(intent);

            //stop the current activity
            finish();
        }
    }
    private class PostToImgur extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... params) {

            //read session_name and session_id from passed parameters
            String session_name=params[0];
            String session_id=params[1];


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://api.imgur.com/3/image");

            JSONObject json = new JSONObject();


            try {

                StringEntity se = new StringEntity("{\n" +
                        "\"data\": {\n" +
                        "\"image\""+ bitmap+
                        "}}");
                httppost.setEntity(se);
                httppost.setHeader("Authorization", "Client-ID 677813e7b6b7d5e");



                BasicHttpContext mHttpContext = new BasicHttpContext();
                CookieStore mCookieStore      = new BasicCookieStore();
                HttpResponse response = httpclient.execute(httppost);

                //read the response and convert it into JSON array
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                //return the JSON array for post processing to onPostExecute function
                return json;

                //httpclient.execute(httppost,mHttpContext);


            }catch (Exception e) {
                Log.v("Error adding article",e.getMessage());
            }
            return json;


        }
        protected void onPostExecute(JSONObject result) {

                try {
                    JSONObject item = result;
                    JSONObject title = (JSONObject) item.get("data");
                    if (title.length() > 0) {
                        link = title.get("link").toString();
                    }


                } catch (Exception e) {
                    Log.v("Error adding database", e.getMessage());
                }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
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
        }catch (Exception e){
            Toast.makeText(AddArticle.this, "Error uploading photo", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onFragmentInteraction(Uri uri){
    }
}
