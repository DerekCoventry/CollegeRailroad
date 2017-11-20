package com.example.derek.collegerailroad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.google.android.gms.internal.zzahg.runOnUiThread;

public class SearchFragment extends Fragment {
    boolean searched = false;
    public static final String SEARCH = "saved_search";
    private String saved_search;
    String option = "ISBN";
    private OnFragmentInteractionListener mListener;
    ProgressDialog mProgressDialog;
    Activity activity;
    public SearchFragment() {}

    public static SearchFragment newInstance(String search) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH, search);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        final EditText mEditText= (EditText) view.findViewById(R.id.search);
        if(getArguments() != null){
            saved_search = getArguments().getString(SEARCH);
        }
        if(saved_search != null){
            mEditText.setText(saved_search);
        }
        activity = getActivity();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!searched) {
                    final int DRAWABLE_RIGHT = 2;
                    String search = mEditText.getText().toString();
                    int search_length = search.length();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (mEditText.getRight() - mEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (search_length == 0) {
                                Toast.makeText(activity, "Search can't be empty", Toast.LENGTH_LONG).show();
                                return false;
                            } else {
                                if(option.contains("ISBN")) {
                                    search = search.replaceAll("-", "");
                                }
                                if (!option.contains("ISBN") || ((search.length() == 10 || search.length() == 13) && search.matches("\\d+X?"))) {
                                    mProgressDialog = ProgressDialog.show(activity, "Loading", "Searching for book...");
                                    getWebsite(search);
                                    return true;
                                } else {
                                    Toast.makeText(activity, "ISBN must be 10 or 13 digits", Toast.LENGTH_LONG).show();
                                    return false;
                                }
                            }
                        }
                    }
                }
                return false;
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try  {
                        InputMethodManager imm = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    } catch (Exception e) {

                    }
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private List<String> getWebsite(final String search) {
        final List<String> book_details = new ArrayList<>(7);
        final String option = this.option;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    book_details.clear();
                    final Intent goToList = new Intent(activity, BookListActivity.class);
                    String title = "", author = "", ISBN10 = "", ISBN13 = "", publisher = "", edition = "", language = "";
                    if(option.contains("ISBN")) {
                        try {
                            Document doc = Jsoup.connect("https://www.bookfinder.com/search/?isbn=" + search + "+&mode=isbn&st=sr&ac=qr").get();
                            Element error = doc.select("#bd").first();
                            if (error == null) {
                                Elements titleAuthor = doc.select("div.attributes strong span");
                                if (titleAuthor != null) {
                                    if (titleAuthor.size() >= 2) {
                                        title = titleAuthor.get(0).text();
                                        author = titleAuthor.get(1).text();
                                    }
                                }
                                Elements ISBNelts = doc.select("div.attributes h1");
                                if (ISBNelts != null) {
                                    String ISBN = ISBNelts.text();
                                    if (ISBN.contains("/")) {
                                        ISBN13 = ISBN.substring(0, ISBN.lastIndexOf("/")).replaceAll("[\\s+-]", "");
                                        ISBN10 = ISBN.substring(ISBN.lastIndexOf("/") + 1).replaceAll("[\\s+-]", "");
                                    }
                                }
                                Elements pubEditLang = doc.select("div.attributes p span.describe-isbn");
                                if (pubEditLang != null && pubEditLang.size() > 0) {
                                    publisher = pubEditLang.get(0).text();
                                    if (pubEditLang.size() > 1) {
                                        edition = pubEditLang.get(1).text();
                                        if (pubEditLang.size() > 2) {
                                            language = pubEditLang.get(2).text();
                                        }
                                    }
                                }
                            }
                        } catch (IOException e) {
                        }
                    }else if(option.equals("Title")){
                        title = search;
                        goToList.putExtra("TITLE", title);
                        goToList.putExtra("OPTION", "Title");
                    }else if(option.equals("Author")){
                        author = search;
                        goToList.putExtra("AUTHOR", author);
                        goToList.putExtra("OPTION", "AUTHOR");
                    }
                    book_details.add(title);
                    book_details.add(author);
                    book_details.add(ISBN10);
                    book_details.add(ISBN13);
                    book_details.add(publisher);
                    book_details.add(edition);
                    book_details.add(language);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goToList.putExtra("OPTION", "ISBN");
                            goToList.putExtra("TITLE", book_details.get(0));
                            goToList.putExtra("AUTHOR", book_details.get(1));
                            goToList.putExtra("ISBN10", book_details.get(2));
                            goToList.putExtra("ISBN13", book_details.get(3));
                            goToList.putExtra("PUBLISHER", book_details.get(4));
                            goToList.putExtra("EDITION", book_details.get(5));
                            goToList.putExtra("LANGUAGE", book_details.get(6));
                            try {
                                mProgressDialog.dismiss();
                            }catch (Exception e){

                            }
                            if(option.equals("ISBN") || option.equals("Title") || option.equals("Author")) {
                                if(book_details.get(0).isEmpty() && book_details.get(1).isEmpty()) {
                                    Toast.makeText(activity, "No book found with that ISBN", Toast.LENGTH_LONG).show();
                                }else {
                                    try {
                                        startActivity(goToList);
                                    }catch (Exception e){

                                    }
                                }
                            }else{
                                ((AddArticle)activity).setTitleAndAuthor(book_details.get(0), book_details.get(1));
                            }
                            searched = false;
                        }
                    });
                }
            }).start();
        return book_details;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void changeSearch(String opt){
        this.option = opt;
        String search;
        EditText mEditText= (EditText) getView().findViewById(R.id.search);
        switch (opt) {
            case "ISBN":
                search = "Search book by " + opt;
                break;
            case "Title":
            case "Author":
                search = "Search book by " + opt.toLowerCase();
                break;
            default:
                search = opt;
                break;
        }
        mEditText.setHint(search);
    }
}
