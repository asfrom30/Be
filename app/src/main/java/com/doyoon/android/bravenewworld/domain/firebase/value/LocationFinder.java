package com.doyoon.android.bravenewworld.domain.firebase.value;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseModel;

/**
 * Created by DOYOON on 7/23/2017.
 */

public class LocationFinder extends FirebaseModel {

    private String accessKey;
    private double latitude;
    private double longitude;

    public LocationFinder() {
    }

    public LocationFinder(String accessKey, double latitude, double longitude) {
        this.accessKey = accessKey;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String getKey() {
        return this.accessKey;
    }

    @Override
    public void setKey(String key) {

    }


}
