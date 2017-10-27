package com.example.derek.collegerailroad;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SearchISBNActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button mButton;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_isbn);
        mEditText = (EditText) findViewById(R.id.search);
        mTextView = (TextView) findViewById(R.id.result);
        mButton = (Button) findViewById(R.id.search_for_book);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextView.setText("Loading...");
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
                getWebsite(mEditText.getText().toString());
            }

        });
    }

    private void getWebsite(final String search) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                try {
                    Document doc = Jsoup.connect("https://www.bookfinder.com/search/?isbn="+search+"+&mode=isbn&st=sr&ac=qr").get();
                    Element error = doc.select("#bd").first();
                    if(error == null) {
                        Elements titleAuthor = doc.select("div.attributes strong span");
                        if(titleAuthor != null) {
                            builder.append("Title: ").append(titleAuthor.get(0).text()).append("\n");
                            builder.append("Author: ").append(titleAuthor.get(1).text()).append("\n");
                        }
                        Elements ISBNelts = doc.select("div.attributes h1") ;
                        if(ISBNelts != null) {
                            String ISBN = ISBNelts.text();
                            if (ISBN.contains("/")) {
                                String ISBN13 = ISBN.substring(0, ISBN.lastIndexOf("/")).replaceAll("\\s+", "");
                                String ISBN10 = ISBN.substring(ISBN.lastIndexOf("/") + 1).replaceAll("\\s+", "");
                                if (ISBN13.length() == 13) {
                                    builder.append("ISBN13: ").append(ISBN13).append("\n");
                                }
                                if (ISBN10.length() == 10) {
                                    builder.append("ISBN10: ").append(ISBN10).append("\n");
                                }
                            }
                        }
                        Elements pubEditLang = doc.select("div.attributes p span.describe-isbn");
                        if(pubEditLang != null) {
                            builder.append("Publisher: ").append(pubEditLang.get(0).text()).append("\n");
                            if(pubEditLang.size() > 1) {
                                builder.append("Edition: ").append(pubEditLang.get(1).text()).append("\n");
                                if(pubEditLang.size() > 2) {
                                    builder.append("Language: ").append(pubEditLang.get(2).text());
                                }
                            }
                        }
                    }else{
                        builder.append("No Results Found");
                    }
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(builder.toString());
                    }
                });
            }
        }).start();
    }
}
