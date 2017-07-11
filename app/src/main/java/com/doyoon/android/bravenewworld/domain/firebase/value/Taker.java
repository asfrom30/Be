package com.doyoon.android.bravenewworld.domain.firebase.value;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseModel;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DOYOON on 7/11/2017.
 */

public class Taker extends FirebaseModel {

    private String key;
    private double latitude;
    private double longitude;

    public Taker() {
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void setKey(String key) {

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

    public LatLng getLatLng(){
        return new LatLng(this.latitude, this.longitude);
    }

    @Override
    public String toString() {
        return "Taker{" +
                "key='" + key + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
