package com.example.derek.collegerailroad;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by arpz2004 on 11/17/2017.
 */
public class BookPostTest {
    private BookPost mBookPost;
    @Before
    public void setUp() throws Exception {
        mBookPost = new BookPost("id1", "title2", "author3", "email4", "condition5", new LatLng(27.34324, -23.2533), "http://www.url.com/image");
    }

    @Test
    public void getId() throws Exception {
        String id = mBookPost.getId();
        assertEquals("id1", id);
    }

    @Test
    public void getTitle() throws Exception {
        String title = mBookPost.getTitle();
        assertEquals("title2", title);
    }

    @Test
    public void setTitle() throws Exception {
        mBookPost.setTitle("title3");
        String title = mBookPost.getTitle();
        assertEquals("title3", title);
    }

    @Test
    public void getAuthor() throws Exception {
        String author = mBookPost.getAuthor();
        assertEquals("author3", author);
    }

    @Test
    public void setAuthor() throws Exception {
        mBookPost.setAuthor("new Author");
        String author = mBookPost.getAuthor();
        assertEquals("new Author", author);
    }

    @Test
    public void getEmail() throws Exception {
        String email = mBookPost.getEmail();
        assertEquals("email4", email);
    }

    @Test
    public void setEmail() throws Exception {
        mBookPost.setEmail("example@gmail.com");
        String email = mBookPost.getEmail();
        assertEquals("example@gmail.com", email);
    }

    @Test
    public void setCondition() throws Exception {
        mBookPost.setCondition("4");
        String condition = mBookPost.getCondition();
        assertEquals("Good", condition);
    }

    @Test
    public void getCondition() throws Exception {
        String condition = mBookPost.getCondition();
        assertEquals("None", condition);
        BookPost validBook = new BookPost("id1", "title2", "author3", "email4", "3", new LatLng(27.34324, -23.2533), "http://www.url.com/image");
        String validCondition = validBook.getCondition();
        assertEquals("New", validCondition);
    }

    @Test
    public void getLatLng() throws Exception {
        LatLng latLng = mBookPost.getLatLng();
        assertEquals(27.34324, latLng.latitude, 0.00001);
        assertEquals(-23.2533, latLng.longitude, 0.00001);
    }

    @Test
    public void setLatLng() throws Exception {
        mBookPost.setLatLng(new LatLng(42.4434, -48.344));
        LatLng latLng = mBookPost.getLatLng();
        assertEquals(42.4434, latLng.latitude, 0.00001);
        assertEquals(-48.344, latLng.longitude, 0.00001);
    }

    @Test
    public void getURL() throws Exception {
        String url = mBookPost.getURL();
        assertEquals("http://www.url.com/image", url);
    }

    @Test
    public void setURL() throws Exception {
        mBookPost.setURL("http://www.imgur.com/image");
        String url = mBookPost.getURL();
        assertEquals("http://www.imgur.com/image", url);
    }

}