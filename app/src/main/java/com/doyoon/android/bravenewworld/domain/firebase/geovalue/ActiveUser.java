package com.doyoon.android.bravenewworld.domain.firebase.geovalue;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class ActiveUser {

    private LatLng latLng;
    private String key;

    public ActiveUser(String key, LatLng latLng) {
        this.key = key;
        this.latLng = latLng;

    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "ActiveUser{" +
                ", latLng=" + latLng +
                ", key='" + key + '\'' +
                '}';
    }

}
