package com.example.derek.collegerailroad;

import android.app.Activity;
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
import android.widget.Button;
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

public class BookListFragmentSelf extends Fragment {
    private RecyclerView mBookRecyclerView;
    private BookAdapter mAdapter;
    private List<BookPost> mBooks;
    public String session_id;
    public String session_name;
    public String user_id = "none";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        if (userInfo.contains("USER_ID")) {
            user_id = userInfo.getString("USER_ID", "none");
        }
        View view = inflater.inflate(R.layout.fragment_book_list_self, container, false);
        mBookRecyclerView = (RecyclerView) view
                .findViewById(R.id.book_recycler_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        new FetchBooks().execute();
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {


        public BookAdapter(List<BookPost> books) {
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
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
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
            mTitleTextView.setText(mBook.getTitle() + " by " + mBook.getAuthor());
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
    public void FetchBookList(){


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
        String curAuthor;
        String curEmail;
        String curUID;
        String curCondition;

        //iterate through JSON to read the title of nodes
        for(int i=0;i<result.length();i++){
            try {
                JSONObject item = (JSONObject) result.get(i);
                JSONArray title = (JSONArray) item.get("title");
                JSONArray vid = (JSONArray) item.get("vid");
                JSONArray email = (JSONArray) item.get("field_email");
                JSONArray author = (JSONArray) item.get("field_author");
                JSONArray condition = (JSONArray) item.get("field_condition");
                JSONArray user= (JSONArray) item.get("uid");
                JSONObject valueUID = (JSONObject) user.get(0);
                JSONObject valueVid = (JSONObject) vid.get(0);
                JSONObject valueTitle = (JSONObject) title.get(0);
                JSONObject valueAuthor = (JSONObject) author.get(0);
                JSONObject valueEmail = (JSONObject) email.get(0);
                JSONObject valueCondition = (JSONObject) condition.get(0);
                curUID = valueUID.get("target_id").toString();
                curId = valueVid.get("value").toString();
                curTitle = valueTitle.get("value").toString();
                curAuthor = valueAuthor.get("value").toString();
                curEmail = valueEmail.get("value").toString();
                curCondition = valueCondition.get("target_id").toString();
                currentBook = new BookPost(curId, curTitle, curAuthor, curEmail, curCondition);
                if(curUID.equals(user_id)) {
                    mBooks.add(currentBook);
                }
            } catch (Exception e) {
                Log.v("Error adding database", e.getMessage());
            }
        }

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
            String curUID;
            String curCondition;

            //iterate through JSON to read the title of nodes
            for(int i=0;i<result.length();i++){
                try {
                    JSONObject item = (JSONObject) result.get(i);
                    JSONArray title = (JSONArray) item.get("title");
                    JSONArray vid = (JSONArray) item.get("vid");
                    JSONArray email = (JSONArray) item.get("field_email");
                    JSONArray author = (JSONArray) item.get("field_author");
                    JSONArray user= (JSONArray) item.get("uid");
                    JSONArray condition = (JSONArray) item.get("field_condition");
                    JSONObject valueUID = (JSONObject) user.get(0);
                    JSONObject valueVid = (JSONObject) vid.get(0);
                    JSONObject valueTitle = (JSONObject) title.get(0);
                    JSONObject valueEmail = (JSONObject) email.get(0);
                    JSONObject valueAuthor = (JSONObject) author.get(0);
                    JSONObject valueCondition = (JSONObject) condition.get(0);
                    curUID = valueUID.get("target_id").toString();
                    curId = valueVid.get("value").toString();
                    curTitle = valueTitle.get("value").toString();
                    curEmail = valueEmail.get("value").toString();
                    curAuthor = valueAuthor.get("value").toString();
                    curCondition = valueCondition.get("target_id").toString();
                    currentBook = new BookPost(curId, curTitle, curAuthor, curEmail, curCondition);
                    updateUI();
                    if(curUID.equals(user_id)) {
                        mBooks.add(currentBook);
                    }
                } catch (Exception e) {
                    Log.v("Error adding database", e.getMessage());
                }
            }

        }
    }


}

