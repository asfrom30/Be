package com.doyoon.android.bravenewworld.presenter.interfaces;

import com.doyoon.android.bravenewworld.domain.firebase.geovalue.ActiveUser;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

/**
 * Created by DOYOON on 7/16/2017.
 */

public interface ActiveUserMapView {
    void clearAllMarkers();
    void addMyLocationMarker(LatLng latLng);
    void addOtherActiveUserMarkers(Map<String, ActiveUser> activeUserMap);
}
