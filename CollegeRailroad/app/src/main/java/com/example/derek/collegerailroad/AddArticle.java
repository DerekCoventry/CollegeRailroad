package com.example.derek.collegerailroad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

public class AddArticle extends Activity implements AdapterView.OnItemSelectedListener {
    public String session_id;
    public String session_name;
    public String location = "Alabama";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);
        Bundle extras = getIntent().getExtras();
        Spinner locationSpin = (Spinner) findViewById((R.id.editlocation));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpin.setAdapter(adapter);
        locationSpin.setOnItemSelectedListener(this);


        if (extras != null) {
            //retrieve the session_id and session_id passed by the previous activity
            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");
        }
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
        //initiate the background process to post the article to the Drupal endpoint.
        //pass session_name and session_id
        new addArticleTask().execute(session_name,session_id);
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
                TextView txtEmail = (TextView) findViewById(R.id.editEmail);
                TextView txtBody = (TextView) findViewById(R.id.editBody);

                //extract text from UI elements and remove extra spaces
                String title=txtTitle.getText().toString().trim();
                String body=txtBody.getText().toString().trim();
                String email=txtEmail.getText().toString().trim();
                //add raw json to be sent along with the HTTP POST request
                //StringEntity se = new StringEntity( "{\"_links\": {\"type\":{"+
                //        "\"href\": \"http://collegerailroad.com/rest/type/node/basic_post\"}},\"title\": [{\"value\": \""+ title + "\""+
                //        "}],\"type\": [{\"target_id\": \"basic_post\"}],\"field_email\": [{\"value\": \""+email+"\"}]}");
                StringEntity se = new StringEntity("{\n" +
                        "\t\"_links\": {\n" +
                        "\t\t\"type\":{\n" +
                        "\t\t\t\"href\": \"http://collegerailroad.com/rest/type/node/basic_post\"\n" +
                        "\t\t}\n" +
                        "\t},\n" +
                        "\t\"title\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"value\": \""+title+"\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +
                        "\t\"type\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"target_id\": \"basic_post\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +
                        "\t\"field_email\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"value\": \""+email+"\"\n" +
                        "\t\t}\n" +
                        "\t],\n" +  "      \"field_state\": ["+
                        "{\"target_id\":"+ getTaxLocation(location)+","+
                        "\"target_type\": \"taxonomy_term\","+
                        "\"target_uuid\": \"8b2b4cba-41b6-4f18-a155-a9159e099b89\","+
                        "\"url\": \"/taxonomy/term/"+getTaxLocation(location)+"\""+
                        " }],"+
                        "}");
                httppost.setEntity(se);
                httppost.setHeader("Accept", "application/hal+json");
                httppost.setHeader("Content-Type", "application/hal+json");



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
                if (location == states[i]) {
                    return Integer.toString((i + 12));
                }
            }
            return Integer.toString(12);
        }

        protected void onPostExecute(Integer result) {

            //start the List Activity and pass back the session_id and session_name
            Intent intent = new Intent(AddArticle.this, ListActivity.class);
            //intent.putExtra("SESSION_ID", session_id);
            //intent.putExtra("SESSION_NAME", session_name);
            startActivity(intent);

            //stop the current activity
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_article, menu);
        return true;
    }

}