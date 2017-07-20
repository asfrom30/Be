package com.doyoon.android.bravenewworld.presenter;

import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOYOON on 7/20/2017.
 *
 * THIS CLASS HAS ALL OF THE INFO FOR USER STATUS
 *
 */

public class UserStatusPresenter {

    public static final String TAG = UserStatusPresenter.class.getSimpleName();

    public static final int USER_INITIAL_STATE = 0;
    public static final int USER_ON_MATCHING = 1;
    public static final int USER_ON_MATCHED = 2;
    public static final int USER_ON_TOGHETHER = 3;

    public static UserStatusPresenter instance;

    public static UserStatusPresenter getInstance(){
        if (instance == null) {
            instance = new UserStatusPresenter();
        }
        return instance;
    }

    /* Static Variable */
    public static int userStatus;
    public static String myUserAccessKey;
    public static String otherUserAccessKey;
    public static UserProfile myUserProfile;
    public static UserProfile otherUserProfile;

    private List<UserProfileUI> userProfileUIList;

    private UserStatusPresenter() {
        userStatus = USER_INITIAL_STATE;
        userProfileUIList = new ArrayList<>();
    }

    public void loadMyProfileFromLocal(){

    }

    public void loadMyUserProfileFromRemote(){
        FirebaseDao.read(UserProfile.class, new FirebaseDao.ReadCallback<UserProfile>() {
            @Override
            public void execute(UserProfile userProfile) {
                myUserProfile = userProfile;

                /* Update All UI which relates to User Profile */
                for (UserProfileUI userProfileUI : userProfileUIList) {
                    userProfileUI.update();
                }

                Log.i(TAG, "load my userProfile successful from remote" + myUserProfile.toString());
            }
        }, myUserAccessKey);
    }




    public interface UserProfileUI {
        void update();
    }


}
