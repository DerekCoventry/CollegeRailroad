package com.example.derek.collegerailroad;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private TextView mTextView;
    boolean searched = false;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final EditText mEditText= (EditText) findViewById(R.id.search);
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
                                Toast.makeText(HomeActivity.this, "Enter ISBN before searching", Toast.LENGTH_LONG).show();
                                return false;
                            } else {
                                String number = search.replaceAll("-", "");
                                if ((number.length() == 10 || number.length() == 13) && number.matches("\\d+")) {
                                    mProgressDialog = ProgressDialog.show(HomeActivity.this, "Loading", "Searching for book...");
                                    getWebsite(number);
                                    return true;
                                } else {
                                    Toast.makeText(HomeActivity.this, "ISBN must be 10 or 13 digits", Toast.LENGTH_LONG).show();
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
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    } catch (Exception e) {

                    }
                }
            }
        });
        TextView intro = (TextView) findViewById(R.id.intro);
        intro.setText("Welcome to College Railroad! This app allows students to buy/sell books with each other without the bookstore middleman markup. Current apps/websites exist for the sole purpose of making money. Businesses and bookstores make excessive amounts of money off of students, and for a very long time students have accepted this as a way of life. Additionally, Facebook pages, and craigslist posts try to accomplish our goal, but the market it waiting for an app to satisfy the need. Having an app be created by potential users allows for an alternative view on the textbook market.");
        Button loginButton = (Button) findViewById(R.id.log_but);
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
        Button listButton = (Button) findViewById(R.id.list_but);

        listButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(HomeActivity.this, AddArticle.class));
            }
        });
    }

    @Override
    public void onBackPressed(){
        searched = false;
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
                        Intent goToList = new Intent(HomeActivity.this, BookListActivity.class);
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
}
