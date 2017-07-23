package com.doyoon.android.bravenewworld.presenter;

import android.net.Uri;
import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseUploader;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.interfaces.UserProfileView;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.doyoon.android.bravenewworld.z.util.DateUtil;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;
import static com.doyoon.android.bravenewworld.presenter.UserStatusPresenter.myUserAccessKey;
import static com.doyoon.android.bravenewworld.presenter.UserStatusPresenter.myUserProfile;

/**
 * Created by DOYOON on 7/22/2017.
 */

public class UserProfilePresenter {

    private static final String TAG = UserProfilePresenter.class.getSimpleName();

    public static UserProfilePresenter instance;

    public static UserProfilePresenter getInstance(){
        if (instance == null) {
            instance = new UserProfilePresenter();
        }
        return instance;
    }
    private UserProfileView userProfileView;

    private UserProfilePresenter() {
        userProfileViewList = new ArrayList<>();
    }

    public void loadMyUserProfileFromRemote(){
        if(myUserAccessKey == null) return;

        FirebaseDao.read(UserProfile.class, new FirebaseDao.ReadCallback<UserProfile>() {
            @Override
            public void execute(UserProfile userProfile) {
                myUserProfile = userProfile;
                if(userProfileView != null) userProfileView.update();

                Log.i(TAG, "load my userProfile successful from remote" + myUserProfile.toString());
            }
        }, myUserAccessKey);
    }

    public void setUserProfileView(UserProfileView userProfileView){
        this.userProfileView = userProfileView;
    }

    public void uploadProfileImage(String strFileUri){
        // todo random key...
        String fileName = DateUtil.getCurrentDate() + "_" + myUserAccessKey;// 시간값 + UUID 추가해서 만듦...
        FirebaseUploader.execute(strFileUri, Const.StorageRefKey.USER_PROFILE, fileName, new FirebaseUploader.Callback() {
            @Override
            public void postExecute(Uri uploadedFileUri) {
                String modelDir = getModelDir(Const.RefKey.USER_PROFILE, myUserAccessKey)
                        + Const.RefKey.USER_PROFILE + "/" + Const.RefKey.USER_PROFILE_IMAGE_URI;
                FirebaseDatabase.getInstance().getReference(modelDir).setValue(uploadedFileUri.toString());

                UserStatusPresenter.getInstance().myUserProfile.setImageUri(uploadedFileUri.toString());

                if(userProfileView != null) userProfileView.updateProfileImage();

                Log.i(TAG, "Add profile image to remote successful" + uploadedFileUri);
            }
        });
    }


    /* Todo update All view... */
    // Impelement Observer Pattern Lately for study
    // how to remove UserProfile UserProfileView.... on
    private List<UserProfileView> userProfileViewList;

    public void addCustomView(UserProfileView view) {

    }

    public void removeCustomView(UserProfileView view) {

    }

    private void updateViewForMulti(){
        /* Update All UI which relates to User Profile */
//        for (UserProfileView view : userProfileViewList) {
//            view.update(myUserProfile);
//        }
    }
}
