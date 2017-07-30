package com.doyoon.android.bravenewworld.domain.local;

import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;

/**
 * Created by DOYOON on 7/29/2017.
 */

public class ActiveUserProfile extends UserProfile {

    private float distanceFromUser;

    public float getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(float distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    @Override
    public String toString() {
        return "ActiveUserProfile{" +
                "distanceFromUser=" + distanceFromUser +
                '}';
    }
}
