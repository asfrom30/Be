package com.doyoon.android.bravenewworld.presenter.interfaces;

import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;

/**
 * Created by DOYOON on 7/16/2017.
 */

public interface ActiveUserListUIController {

    void addActiveUser(UserProfile userProfile);
    void notifySetChanged();
}
