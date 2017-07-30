package com.doyoon.android.bravenewworld.util.googlemap;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by DOYOON on 7/26/2017.
 */

public class UiSetting {

    public static void defaultSet(GoogleMap googleMap){
        // googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
    }

}
