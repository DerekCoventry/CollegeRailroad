package com.example.derek.collegerailroad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by derek on 10/10/17.
 */

public class BookPost implements Parcelable{
    private String bId;
    private String bTitle;
    private String bEmail;

    public BookPost(String id, String title, String email) {
        bId = id;
        bTitle = title;
        bEmail = email;
    }

    protected BookPost(Parcel in) {
        bId = in.readString();
        bTitle = in.readString();
        bEmail = in.readString();
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

    public String getEmail() {
        return bEmail;
    }

    public void setEmail(String email) {
        bEmail = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bId);
        parcel.writeString(bTitle);
        parcel.writeString(bEmail);
    }
}
