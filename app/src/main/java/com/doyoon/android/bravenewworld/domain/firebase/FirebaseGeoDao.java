package com.doyoon.android.bravenewworld.domain.firebase;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class FirebaseGeoDao {

    private static String TAG = FirebaseGeoDao.class.getSimpleName();

    // todo make singlton.... each path.... gender/giver/taker
    private static GeoFire instance = null;

    public static void insert(String dir, String key, GeoLocation geoLocation) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(dir);
        GeoFire geoFire = new GeoFire(ref);

        geoFire.setLocation(key, geoLocation, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.i(TAG, "There was an error saving the location to GeoFire: " + error);
                } else {
                    Log.i(TAG, "Location saved on server successfully in GeoFire");
                }
            }
        });
    }

    public static void delete(String dir, String key){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(dir);
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(key, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.i(TAG, "There was an error removing the location to GeoFire: " + error);
                } else {
                    Log.i(TAG, "Location removed on server successfully!");
                }
            }
        });
    }

    public static void geoQuery(String dir, GeoLocation coordiGeoLocation, double distance){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(dir);
        GeoFire geoFire = new GeoFire(ref);

        // todo singltone instance...
        GeoQuery geoQuery = geoFire.queryAtLocation(coordiGeoLocation, distance);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.e(TAG, "On Key Entered");
            }

            @Override
            public void onKeyExited(String key) {
                Log.e(TAG, "On Key Exited");
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

}
