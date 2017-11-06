package com.example.derek.collegerailroad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by derek on 10/24/17.
 */

public class BookListFragment extends Fragment {
    private RecyclerView mBookRecyclerView;
    private BookAdapter mAdapter;
    private ArrayList<BookPost> mBooks = new ArrayList<BookPost>();
    public String[] states = new String[]{"Alabama","Alaska","Alaska Fairbanks","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"};
    public String[] conditions = new String[]{"New", "Good",  "Worn","Damaged"};
    public boolean initial = true;
    private String option = "", titleFilter = null, authorFilter = null;
    public String session_id;
    public String session_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        mBookRecyclerView = (RecyclerView) view
                .findViewById(R.id.book_recycler_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Button addButton = (Button) view.findViewById(R.id.add_but);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddArticle.class));
            }
        });
        Button mapButton = (Button) view.findViewById(R.id.map_but);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("BOOKS", mBooks);
                startActivity(intent);
            }
        });
        Spinner conditionSpin = (Spinner) view.findViewById((R.id.editcondition));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.conditionsSearch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpin.setAdapter(adapter);
        conditionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor userInfoEditor = userInfo.edit();
                String condition = parent.getItemAtPosition(position).toString();
                if(condition.equals("All")){
                    userInfoEditor.putString("CONDITION", "none");

                }else {
                    for (int i = 0; i < 4; i++) {
                        if (condition.equals(conditions[i])) {
                            userInfoEditor.putString("CONDITION", Integer.toString(i + 3));
                        }
                    }
                }
                if(!initial) {
                    new FetchBooks().execute();
                    updateUI();
                    Log.d("sizembooks", Integer.toString(mBooks.size()));
                }
                userInfoEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor userInfoEditor = userInfo.edit();
                userInfoEditor.putString("CONDITION", "none");
                if(!initial) {
                    new FetchBooks().execute();
                }
                userInfoEditor.commit();


            }
        });
        Spinner locationSpin = (Spinner) view.findViewById((R.id.editlocation));
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.locationsSearch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpin.setAdapter(adapter);
        locationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor userInfoEditor = userInfo.edit();
                String location = parent.getItemAtPosition(position).toString();
                Log.d("loc", location);
                if(location.equals("All")){
                    userInfoEditor.putString("LOCATION", "none");

                }else {
                    for (int i = 0; i < 51; i++) {
                        if (location.equals(states[i])) {
                            userInfoEditor.putString("LOCATION", Integer.toString(i + 12));
                            Log.d("location", Integer.toString(i + 12));
                        }
                    }
                }
                if(!initial) {
                    new FetchBooks().execute();
                    updateUI();
                    Log.d("sizembooks", Integer.toString(mBooks.size()));
                }
                userInfoEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor userInfoEditor = userInfo.edit();
                userInfoEditor.putString("LOCATION", "none");
                if(!initial) {
                    new FetchBooks().execute();
                    //mBookRecyclerView.getAdapter().notifyDataSetChanged();
                }
                userInfoEditor.commit();


            }
        });
        initial = false;
        new FetchBooks().execute();
        return view;
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {


        public BookAdapter(ArrayList<BookPost> books) {
            mBooks = books;
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new BookHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            BookPost book = mBooks.get(position);
            holder.bind(book);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        public void updateList(ArrayList<BookPost> books) {
            mBooks = books;
            notifyDataSetChanged();
        }
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mEmailTextView;
        private BookPost mBook;
        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.book_title);
            mEmailTextView = (TextView) itemView.findViewById(R.id.book_email);

        }
        public void bind(BookPost book) {
            mBook = book;
            mTitleTextView.setText(mBook.getTitle()+" by " + mBook.getAuthor());
            mEmailTextView.setText(mBook.getEmail());
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), BookDisplayActivity.class);
            intent.putExtra("BOOK_ID", mBook.getId());
            startActivity(intent);
        }
    }

    private void updateUI() {

        //BookLab bookLab = BookLab.get(getActivity());
        //List<BookPost> books = bookLab.getBooks();

        mAdapter = new BookAdapter(mBooks);
        mBookRecyclerView.setAdapter(mAdapter);

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
            String curAuthor;
            String curEmail;
            String curLoc;
            String curCondition;

            String locCheck = "none";
            String condCheck = "none";
            SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
            Log.d("contains", Boolean.toString(userInfo.contains("LOCATION")));
            if (userInfo.contains("LOCATION")) {
                locCheck = userInfo.getString("LOCATION", "none");
                Log.d("locCheck", locCheck);
            }
            Log.d("locCheck", locCheck);
            if(userInfo.contains("CONDITION")){
                condCheck = userInfo.getString("CONDITION", "none");
            }

            //iterate through JSON to read the title of nodes
            for(int i=0;i<result.length();i++){
                try {
                    JSONObject item = (JSONObject) result.get(i);
                    JSONArray title = (JSONArray) item.get("title");
                    JSONArray vid = (JSONArray) item.get("vid");
                    JSONArray author = (JSONArray) item.get("field_author");
                    JSONArray email = (JSONArray) item.get("field_email");
                    JSONArray condition = (JSONArray) item.get("field_condition");
                    JSONObject valueVid = (JSONObject) vid.get(0);
                    JSONObject valueTitle = (JSONObject) title.get(0);
                    JSONObject valueAuthor = (JSONObject) author.get(0);
                    JSONObject valueEmail = (JSONObject) email.get(0);
                    JSONArray loc = (JSONArray) item.get("field_state");
                    JSONObject valueLoc = (JSONObject) loc.get(0);
                    JSONObject valueCondition = (JSONObject) condition.get(0);
                    curLoc = valueLoc.get("target_id").toString();
                    curId = valueVid.get("value").toString();
                    curTitle = valueTitle.get("value").toString();
                    curAuthor = valueAuthor.get("value").toString();
                    curEmail = valueEmail.get("value").toString();
                    curCondition = valueCondition.get("target_id").toString();
                    currentBook = new BookPost(curId, curTitle, curAuthor, curEmail, curCondition);
                    updateUI();
                    if ((curLoc.equals(locCheck) || locCheck.equals("none")) && (curCondition.equals(condCheck) || condCheck.equals("none"))) {
                        boolean addBook = false;
                        switch(BookListFragment.this.option){
                            case "ISBN":
                                if(curTitle.toLowerCase().contains(BookListFragment.this.titleFilter.toLowerCase())&&curAuthor.toLowerCase().contains(BookListFragment.this.authorFilter.toLowerCase())){
                                    addBook = true;
                                }
                                break;
                            case "Title":
                                if(curTitle.toLowerCase().contains(BookListFragment.this.titleFilter.toLowerCase())){
                                    addBook = true;
                                }
                                break;
                            case "Author":
                                if(curAuthor.toLowerCase().contains(BookListFragment.this.authorFilter.toLowerCase())){
                                    addBook = true;
                                }
                                break;
                            default:
                                addBook = true;
                                break;
                        }
                        if(addBook) {
                            mBooks.add(currentBook);
                        }
                    }
                    TextView numberOfResults = getActivity().findViewById(R.id.numberOfResults);
                    int numOfBooks = mBooks.size();
                    if(numOfBooks == 1){
                        numberOfResults.setText(Integer.toString(numOfBooks) + " result found");
                    }else {
                        numberOfResults.setText(Integer.toString(numOfBooks) + " results found");
                    }
                } catch (Exception e) {
                    Log.v("Error adding database", e.getMessage());
                }

            }

        }
    }

    public void setFilter(String opt, String search){
        this.option = opt;
        if(opt.equals("Title")){
            this.titleFilter = search;
        }else if(opt.equals("Author")){
            this.authorFilter = search;
        }
    }

    public void setFilter(String opt, String title, String author){
        this.option = opt;
        if(opt.equals("ISBN")) {
            this.titleFilter = title;
            this.authorFilter = author;
        }
    }


}

/*    public void FetchBookList(){


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

        }catch (Exception e) {
            Log.v("Error adding article",e.getMessage());
        }

        JSONArray result = json;
        mBooks = new ArrayList<BookPost>();
        BookPost currentBook;
        String curId;
        String curTitle;
        String curEmail;
        String curLoc;
        String locCheck = "none";
        SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        if (userInfo.contains("LOCATION")) {
            locCheck = userInfo.getString("LOCATION", "none");
            Log.d("locCehck", locCheck);
        }
        Log.d("locCheck", locCheck);

        //iterate through JSON to read the title of nodes
        for(int i=0;i<result.length();i++){
            try {
                JSONObject item = (JSONObject) result.get(i);
                JSONArray title = (JSONArray) item.get("title");
                JSONArray vid = (JSONArray) item.get("vid");
                JSONArray email = (JSONArray) item.get("field_email");
                JSONArray loc = (JSONArray) item.get("field_state");
                JSONObject valueLoc = (JSONObject) loc.get(0);
                JSONObject valueVid = (JSONObject) vid.get(0);
                JSONObject valueTitle = (JSONObject) title.get(0);
                JSONObject valueEmail = (JSONObject) email.get(0);
                curLoc = valueVid.get("target_id").toString();
                curId = valueVid.get("value").toString();
                curTitle = valueTitle.get("value").toString();
                curEmail = valueEmail.get("value").toString();
                currentBook = new BookPost(curId, curTitle, curEmail);
                if (curLoc.equals(locCheck) || locCheck.equals("none")) {
                    mBooks.add(currentBook);
                }
            } catch (Exception e) {
                Log.v("Error adding database", e.getMessage());
            }
        }

    }*/

