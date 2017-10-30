package com.example.derek.collegerailroad;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private OnFragmentInteractionListener mListener;
    ProgressDialog mProgressDialog;
    public SearchFragment() {}

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
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
                                Toast.makeText(getActivity(), "Enter ISBN before searching", Toast.LENGTH_LONG).show();
                                return false;
                            } else {
                                String number = search.replaceAll("-", "");
                                if ((number.length() == 10 || number.length() == 13) && number.matches("\\d+")) {
                                    mProgressDialog = ProgressDialog.show(getActivity(), "Loading", "Searching for book...");
                                    getWebsite(number);
                                    return true;
                                } else {
                                    Toast.makeText(getActivity(), "ISBN must be 10 or 13 digits", Toast.LENGTH_LONG).show();
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
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                book_details.clear();
                String title = "", author = "", ISBN10 = "", ISBN13 = "", publisher = "", edition = "", language = "";
                try {
                    Document doc = Jsoup.connect("https://www.bookfinder.com/search/?isbn="+search+"+&mode=isbn&st=sr&ac=qr").get();
                    Element error = doc.select("#bd").first();
                    if(error == null) {
                        Elements titleAuthor = doc.select("div.attributes strong span");
                        if(titleAuthor != null) {
                            title = titleAuthor.get(0).text();
                            author = titleAuthor.get(1).text();
                        }
                        Elements ISBNelts = doc.select("div.attributes h1") ;
                        if(ISBNelts != null) {
                            String ISBN = ISBNelts.text();
                            if (ISBN.contains("/")) {
                                ISBN13 = ISBN.substring(0, ISBN.lastIndexOf("/")).replaceAll("[\\s+-]", "");
                                ISBN10 = ISBN.substring(ISBN.lastIndexOf("/") + 1).replaceAll("[\\s+-]", "");
                            }
                        }
                        Elements pubEditLang = doc.select("div.attributes p span.describe-isbn");
                        if(pubEditLang != null) {
                            publisher = pubEditLang.get(0).text();
                            if(pubEditLang.size() > 1) {
                                edition = pubEditLang.get(1).text();
                                if(pubEditLang.size() > 2) {
                                    language = pubEditLang.get(2).text();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
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
                        Intent goToList = new Intent(getActivity(), BookListActivity.class);
                        goToList.putExtra("TITLE", book_details.get(0));
                        goToList.putExtra("AUTHOR", book_details.get(1));
                        goToList.putExtra("ISBN10", book_details.get(2));
                        goToList.putExtra("ISBN13", book_details.get(3));
                        goToList.putExtra("PUBLISHER", book_details.get(4));
                        goToList.putExtra("EDITION", book_details.get(5));
                        goToList.putExtra("LANGUAGE", book_details.get(6));
                        mProgressDialog.dismiss();
                        startActivity(goToList);
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
}
