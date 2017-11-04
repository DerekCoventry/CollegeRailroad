package com.example.derek.collegerailroad;


import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by derek on 10/24/17.
 */

public class BookListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        BookListFragment bookListFrag = new BookListFragment();
        Intent intent = getIntent();
        String opt, title = "", author = "";
        try {
            opt = intent.getExtras().getString("OPTION");
            if(opt.equals("ISBN") || opt.equals("Title")){
                title = intent.getExtras().getString("TITLE");
            }
            if(opt.equals("ISBN") || opt.equals("Author")){
                author = intent.getExtras().getString("AUTHOR");
            }
            if(opt.equals("ISBN")) {
                bookListFrag.setFilter(opt, title, author);
            } else if(opt.equals("Title")) {
                bookListFrag.setFilter(opt, title);
            } else if(opt.equals("Author")) {
                bookListFrag.setFilter(opt, author);
            }
        }catch(Exception e){
        }
        return bookListFrag;
    }
}