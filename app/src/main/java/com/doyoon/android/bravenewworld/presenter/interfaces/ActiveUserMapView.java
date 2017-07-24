package com.doyoon.android.bravenewworld.presenter.interfaces;

import com.doyoon.android.bravenewworld.domain.firebase.geovalue.ActiveUser;

import java.util.Map;

/**
 * Created by DOYOON on 7/16/2017.
 */

public interface ActiveUserMapView {
    void resetMarker(Map<String, ActiveUser> activeUserMap);
}
