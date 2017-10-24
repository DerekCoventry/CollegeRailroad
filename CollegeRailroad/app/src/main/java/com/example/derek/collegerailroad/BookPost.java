package com.example.derek.collegerailroad;

/**
 * Created by derek on 10/10/17.
 */

public class BookPost {
    private String bId;
    private String bTitle;
    private String bEmail;

    public BookPost(String id, String title, String email) {
        bId = id;
        bTitle = title;
        bEmail = email;
    }
    public String getId() {
        return bId;
    }

    public String getTitle() {
        return bTitle;
    }

    public void setTitle(String title) {
        bTitle = title;
    }

    public String getEmail() {
        return bEmail;
    }

    public void setEmail(String email) {
        bEmail = email;
    }
}
