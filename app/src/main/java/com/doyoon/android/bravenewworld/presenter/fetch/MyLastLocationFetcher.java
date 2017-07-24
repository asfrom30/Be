package com.doyoon.android.bravenewworld.presenter.fetch;

import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by DOYOON on 7/24/2017.
 */

public class MyLastLocationFetcher {

    /* Signleton */
    public static final String TAG = MyLastLocationFetcher.class.getSimpleName();
    public static MyLastLocationFetcher instance;
    public static MyLastLocationFetcher getInstance(){
        if (instance == null) {
            instance = new MyLastLocationFetcher();
        }
        return instance;
    }

    private FusedLocationProviderClient mFusedLocationClient;

    private MyLastLocationFetcher() {
    }

    @SuppressWarnings("MissingPermission")
    public void fetch(Activity activity, final Callback callback){

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(activity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            callback.execute(latLng);
                            Log.i(TAG, "Last Location Update Complete, My Last Location is " + latLng.toString());

                        } else {
                            Log.e(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }



    public interface Callback {
        void execute(LatLng latLng);
    }
}
