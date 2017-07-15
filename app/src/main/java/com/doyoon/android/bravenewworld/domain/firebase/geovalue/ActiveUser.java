package com.doyoon.android.bravenewworld.domain.firebase.geovalue;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class ActiveUser {

    private boolean isActive;
    private LatLng latLng;
    private String key;

    public ActiveUser(String key, LatLng latLng) {
        this.isActive = true;
        this.latLng = latLng;
        this.key = key;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
                "isActive=" + isActive +
                ", latLng=" + latLng +
                ", key='" + key + '\'' +
                '}';
    }
}
