package com.doyoon.android.bravenewworld.presenter;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.doyoon.android.bravenewworld.domain.RemoteDao;
import com.doyoon.android.bravenewworld.util.Const;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by DOYOON on 7/19/2017.
 */

public class LocationPresenter {

    private static final String TAG = LocationPresenter.class.getSimpleName();
    private static LocationPresenter instance;

    public static LocationPresenter getInstance(){

        if (instance == null) {
            instance = new LocationPresenter();
        }

        return instance;
    }

    private LocationCallback locationCallback;
    private OnCompleteListener onStopLocationCompleteListener;

    private LocationPresenter() {

    }

    public void run(Activity activity){

        // update values using data stored in the bundle
        // updateValuesFromBundle(savedInstanceState);


        /* Boiler Plate */
        locationServiceInit(activity);

        if (locationCallback == null) {
            locationCallback = createLocationCallback();
        }
        startLocationUpdates(locationCallback);
    }

    public void stop(){
        if (onStopLocationCompleteListener == null) {
            onStopLocationCompleteListener = createStopLocationCompleteListener();
        }

        /* Firebase handling */

        stopLocationUpdates(locationCallback, onStopLocationCompleteListener);
    }

    /* Create a callback for receiving location events */
    private LocationCallback createLocationCallback() {
        return new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location currentLocation = locationResult.getLastLocation();
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                String lastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                RemoteDao.LocationFinder.updateMyLocation(currentLatLng, UserStatusPresenter.locationFinderAccessKey, UserStatusPresenter.myUserAccessKey);

                Log.e(TAG, currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + ", " + lastUpdateTime);
            }
        };
    }

    private OnCompleteListener createStopLocationCompleteListener() {
        return new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        };
    }

    private Activity activity;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    public void locationServiceInit(Activity activity){

        this.activity = activity;

        if(mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        }

        if (mSettingsClient == null) {
            mSettingsClient = LocationServices.getSettingsClient(activity);
        }

        if (mLocationRequest == null) {
            mLocationRequest = getNewLocationRequest();
        }

        if (mLocationSettingsRequest == null) {
            mLocationSettingsRequest = buildLocationSettingsRequest(mLocationRequest);
        }
    }


    /* Uses a {Builder} to build a {LocationSettingsRequest} that is used for checking
    * if a device has the needed location settings */
    private LocationSettingsRequest buildLocationSettingsRequest(LocationRequest mLocationRequest){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        return builder.build();
    }


    /*
     sets up the location request. android has two location request setting:
    */
    private LocationRequest getNewLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(Const.GoogleMap.UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(Const.GoogleMap.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(Const.GoogleMap.PRIORITY);

        return locationRequest;
    }

    private void startLocationUpdates(final LocationCallback locationCallback) {
        // Begin by checking if the device has the necessary location settings
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this.activity, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied");

                        //noinspection MissingPermission
                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this.activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location Settings are not satisfied. Attempting to upgrade location settings");

                                try {
                                    /* show the dialog by calling startResolutionForresult(), and check the result in onActivityResult() */
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(activity, Const.GoogleMap.REQUEST_CHECK_SETTING);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Setting";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                                // mRequestingLocationUpdates = false;
                                break;
                        }
                    }
                });
    }

    private void stopLocationUpdates(LocationCallback locationCallback, OnCompleteListener onCompleteListener){

//        if (!mRequestingLocationUpdates) {
//            Log.d(TAG, "stopLocationUpdates : updates never requested, no-op");
//            return;
//        }

        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(activity, onCompleteListener);
    }


}
