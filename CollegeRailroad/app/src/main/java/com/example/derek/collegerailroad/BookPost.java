package com.example.derek.collegerailroad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by derek on 10/10/17.
 */

public class BookPost implements Parcelable{
    private String bId;
    private String bTitle;
    private String bAuthor;
    private String bEmail;
    private String bCondition;

    public BookPost(String id, String title, String author, String email, String condition) {
        bId = id;
        bTitle = title;
        bAuthor = author;
        bEmail = email;
        bCondition = condition;
    }

    protected BookPost(Parcel in) {
        bId = in.readString();
        bTitle = in.readString();
        bAuthor = in.readString();
        bEmail = in.readString();
        bCondition = in.readString();
    }

    public static final Creator<BookPost> CREATOR = new Creator<BookPost>() {
        @Override
        public BookPost createFromParcel(Parcel in) {
            return new BookPost(in);
        }

        @Override
        public BookPost[] newArray(int size) {
            return new BookPost[size];
        }
    };

    public String getId() {
        return bId;
    }

    public String getTitle() {
        return bTitle;
    }

    public void setTitle(String title) {
        bTitle = title;
    }
    public String getAuthor() {
        return bAuthor;
    }

    public void setAuthor(String author) {
        bAuthor = author;
    }


    public String getEmail() {
        return bEmail;
    }

    public void setEmail(String email) {
        bEmail = email;
    }
    public void setCondition(String condition) {
        bCondition = condition;
    }
    public String getCondition() {

        String[] conditions = new String[]{"New", "Good",  "Worn","Damaged"};
        return conditions[Integer.parseInt(bCondition)-3];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bId);
        parcel.writeString(bTitle);
        parcel.writeString(bAuthor);
        parcel.writeString(bEmail);
        parcel.writeString(bCondition);
    }
}
