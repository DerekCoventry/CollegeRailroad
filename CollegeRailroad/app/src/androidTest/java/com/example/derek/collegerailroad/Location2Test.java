package com.example.derek.collegerailroad;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Location2Test {
    private Context appContext;
    @Before
    public void runBeforeTestMethod() {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void getCityStateColumbusOhioTest() throws Exception {
        String location = Location2.getCityState(appContext, new LatLng(39.9612, -82.9988));
        assertEquals("Columbus, Ohio", location);
    }

    @Test
    public void getCityStateCincinnatiOhioTest() throws Exception {
        String location = Location2.getCityState(appContext, new LatLng(39.1031, -84.5120));
        assertEquals("Cincinnati, Ohio", location);
    }

    @Test
    public void getCityStatePensacolaFloridaTest() throws Exception {
        String location = Location2.getCityState(appContext, new LatLng(30.4213, -87.2169));
        assertEquals("Pensacola, Florida", location);
    }

    @Test
    public void getCityStateInvalidTest() throws Exception {
        String location = Location2.getCityState(appContext, new LatLng(6787630.4213, -678677.2169));
        assertEquals("Unknown City, Unknown State", location);
    }

    @Test
    public void getStateCoordinatesOhioTest() throws Exception {
        LatLng coordinates = Location2.getStateCoordinates(appContext, "Ohio");
        assertEquals(40.4173, coordinates.latitude, .001);
        assertEquals(-82.9071, coordinates.longitude, .001);
    }

    @Test
    public void getStateCoordinatesFloridaTest() throws Exception {
        LatLng coordinates = Location2.getStateCoordinates(appContext, "Florida");
        assertEquals(27.6648, coordinates.latitude, .001);
        assertEquals(-81.5158, coordinates.longitude, .001);
    }

    @Test
    public void getStateCoordinatesInvalidTest() throws Exception {
        LatLng coordinates = Location2.getStateCoordinates(appContext, "ddsgdffdgdf");
        assertEquals(0, coordinates.latitude, 0);
        assertEquals(0, coordinates.longitude, 0);
    }
}
