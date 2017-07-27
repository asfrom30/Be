package com.doyoon.android.bravenewworld.presenter.listener;

import android.util.Log;

import com.doyoon.android.bravenewworld.z.util.Const;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;

/**
 * Created by DOYOON on 7/24/2017.
 */

public class GeoQueryListener {

    /* Signleton */
    public static final String TAG = GeoQueryListener.class.getSimpleName();
    public static GeoQueryListener instance;
    public static GeoQueryListener getInstance(){
        if (instance == null) {
            instance = new GeoQueryListener();
        }
        return instance;
    }

    private GeoQuery currentGeoQuery;
    private GeoQueryEventListener currentGeoQueryEventListener;

    private GeoQueryListener() {
    }

    public void listen(int userType, LatLng originLatLng, Callback callback){
        this.addGeoQuery(userType, originLatLng, callback);

    }

    public void stop(){
        this.removeGeoQuery();
    }

    private void addGeoQuery(int userType, LatLng originLatLng, Callback callback){
        double distance_km = Const.DEFAULT_SEARCH_DISTANCE_KM;

        /* Valid Query Check */
        if (originLatLng == null) { Log.e(TAG, "Latlng is null, can't be set last geo location qeury");
            return;
        }
        if (distance_km <= 0) {Log.e(TAG, "Distance Query can't be set under zero");
            return;
        }

        /* Release Geo Query Remove All Listener Before getQuery */
        if (this.currentGeoQuery != null) {
            this.removeGeoQuery();
        }


        /* Prepare Query */
        GeoLocation lastGeoLocationQuery = new GeoLocation(originLatLng.latitude, originLatLng.longitude);
        String userTypeQuery = getQueryDependOnUserType(userType);

        /* reset get Query */
        this.currentGeoQuery = this.toBuildGeoQuery(lastGeoLocationQuery, userTypeQuery, distance_km);
        addGeoQueryListener(currentGeoQuery, callback);

        Log.i(TAG, "Add Geo Query Successfully");
    }

    private void removeGeoQuery() {
        if(this.currentGeoQuery == null) return;
        if(this.currentGeoQueryEventListener == null) return;

        this.currentGeoQuery.removeGeoQueryEventListener(this.currentGeoQueryEventListener);
        this.currentGeoQuery.removeAllListeners();
        this.currentGeoQuery = null;
        this.currentGeoQueryEventListener = null;

        Log.i(TAG, "Before Geo Query Is Removed Successful");
    }

    private GeoQuery toBuildGeoQuery(GeoLocation coordiGeoLocationQuery, String userTypeQuery, double distanceQuery){
        String modelDir = getModelDir(userTypeQuery);
        GeoQuery geoQuery = new GeoFire(FirebaseDatabase.getInstance().getReference(modelDir)).queryAtLocation(coordiGeoLocationQuery, distanceQuery);
        return geoQuery;
    }

    private void addGeoQueryListener(GeoQuery geoQuery, final Callback callback) {
        if (geoQuery == null) {
            Log.e(TAG, "Geo Query is null, Can't not add Listener");
            return;
        }

        this.currentGeoQueryEventListener = new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                callback.onKeyEntered(key, location);
            }

            @Override
            public void onKeyExited(String key) {
                callback.onKeyExited(key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                callback.onGeoQueryReady();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e(TAG, "On Geo Query Error " + error.toString());
            }
        };
        geoQuery.addGeoQueryEventListener(this.currentGeoQueryEventListener);
    }

    private String getQueryDependOnUserType(int userType){
        if (userType == Const.ActiveUserType.Giver) {
            return Const.QueryKey.TAKER;
        } else {
            return Const.QueryKey.GIVER;
        }
    }

    public interface Callback {
        void onKeyEntered(String key, GeoLocation location);
        void onKeyExited(String key);
        void onGeoQueryReady();
    }
}
