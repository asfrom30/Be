package com.doyoon.android.bravenewworld.z.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class LatLngUtil {

    public static float distanceBetweenTwoLatLngUnitMeter(LatLng latLng1, LatLng latLng2){
        float[] results = new float[1];
        Location.distanceBetween(latLng1.latitude, latLng1.longitude,
                latLng2.latitude, latLng2.longitude, results);
        return results[0];
    }
}
