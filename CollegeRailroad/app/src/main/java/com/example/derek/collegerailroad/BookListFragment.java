package com.example.derek.collegerailroad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

/**
 * Created by derek on 10/24/17.
 */

public class BookListFragment extends Fragment {
    private RecyclerView mBookRecyclerView;
    private BookAdapter mAdapter;
    private List<BookPost> mBooks;
    public String session_id;
    public String session_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        mBookRecyclerView = (RecyclerView) view
                .findViewById(R.id.book_recycler_view);
        Button addButton = (Button) view.findViewById(R.id.add_but);
        addButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(getActivity(), AddArticle.class));
            }
        });
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new FetchBooks().execute();
        return view;
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
            mTitleTextView.setText(mBook.getTitle());
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
        String curEmail;

        //iterate through JSON to read the title of nodes
        for(int i=0;i<result.length();i++){
            try {
                JSONObject item = (JSONObject) result.get(i);
                JSONArray title = (JSONArray) item.get("title");
                JSONArray vid = (JSONArray) item.get("vid");
                JSONArray email = (JSONArray) item.get("field_email");
                JSONObject valueVid = (JSONObject) vid.get(0);
                JSONObject valueTitle = (JSONObject) title.get(0);
                JSONObject valueEmail = (JSONObject) email.get(0);
                curId = valueVid.get("value").toString();
                curTitle = valueTitle.get("value").toString();
                curEmail = valueEmail.get("value").toString();
                currentBook = new BookPost(curId, curTitle, curEmail);

                mBooks.add(currentBook);
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
            String curEmail;

            //iterate through JSON to read the title of nodes
            for(int i=0;i<result.length();i++){
                try {
                    JSONObject item = (JSONObject) result.get(i);
                    JSONArray title = (JSONArray) item.get("title");
                    JSONArray vid = (JSONArray) item.get("vid");
                    JSONArray email = (JSONArray) item.get("field_email");
                    JSONObject valueVid = (JSONObject) vid.get(0);
                    JSONObject valueTitle = (JSONObject) title.get(0);
                    JSONObject valueEmail = (JSONObject) email.get(0);
                    curId = valueVid.get("value").toString();
                    curTitle = valueTitle.get("value").toString();
                    curEmail = valueEmail.get("value").toString();
                    currentBook = new BookPost(curId, curTitle, curEmail);
                    updateUI();

                    mBooks.add(currentBook);
                } catch (Exception e) {
                    Log.v("Error adding database", e.getMessage());
                }
            }

        }
    }


}

