package com.doyoon.android.bravenewworld.presenter;

import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.google.android.gms.maps.model.LatLng;

import static com.doyoon.android.bravenewworld.z.util.Const.UserStatus.USER_NOT_YET_MATCHED;
import static com.doyoon.android.bravenewworld.z.util.Const.ActiveUserType.NOT_YET_CHOOSED;

/**
 * Created by DOYOON on 7/20/2017.
 *
 * THIS CLASS HAS ALL OF THE INFO FOR USER STATUS
 *
 */

public class UserStatusPresenter {

    public static final String TAG = UserStatusPresenter.class.getSimpleName();

    public static UserStatusPresenter instance;

    public static UserStatusPresenter getInstance(){
        if (instance == null) {
            instance = new UserStatusPresenter();
        }
        return instance;
    }

    /* Static Variable */
    public static int userStatus = USER_NOT_YET_MATCHED;
    public static int activeUserType = NOT_YET_CHOOSED;

    public static String myUserAccessKey;
    public static String otherUserAccessKey;
    public static UserProfile myUserProfile;
    public static UserProfile otherUserProfile;

    public static LatLng lastLatLng;
    public static String chatAccessKey;
    public static String locationFinderAccessKey;


    private UserStatusPresenter() {

    }

    public boolean isNotYetChoosed() {
        if(userStatus == Const.UserStatus.USER_NOT_YET_MATCHED) return true;
        else return false;
    }

    public boolean isOnFinding() {
        if(userStatus == Const.UserStatus.ON_FINDING) return true;
        else return false;
    }

    public boolean isOnMatching() {
        if(userStatus == Const.UserStatus.ON_MATCHING) return true;
        else return false;
    }

    public void loadMyProfileFromLocal(){
        //todo using shared preference
    }
}
