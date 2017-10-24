package com.example.derek.collegerailroad;


import android.support.v4.app.Fragment;

/**
 * Created by derek on 10/24/17.
 */

public class BookListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new BookListFragment();
    }
}