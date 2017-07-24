package com.doyoon.android.bravenewworld.presenter;

import android.net.Uri;
import android.util.Log;

import com.doyoon.android.bravenewworld.domain.RemoteDao;
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

    private List<UserProfileView> userProfileViewList;

    private UserProfilePresenter() {
        userProfileViewList = new ArrayList<>();
    }

    public void loadMyUserProfileFromRemote(){
        if(myUserAccessKey == null) return;

        FirebaseDao.read(UserProfile.class, new FirebaseDao.ReadCallback<UserProfile>() {
            @Override
            public void execute(UserProfile userProfile) {
                myUserProfile = userProfile;
                notifyMyProfileUpdate();
                Log.i(TAG, "load my userProfile successful from remote" + myUserProfile.toString());
            }
        }, myUserAccessKey);
    }


    public void saveProfileToRemote(String name, String work, int age, String comment) {
        UserProfile currentProfile = UserStatusPresenter.getInstance().myUserProfile;
        if(currentProfile == null) return;

        if(!name.equals(currentProfile.getName())) RemoteDao.UserProfile.insertName(name);
        if(!work.equals(currentProfile.getWork())) RemoteDao.UserProfile.insertWork(work);
        if(age != currentProfile.getAge()) RemoteDao.UserProfile.insertAge(age);
        if(!comment.equals(currentProfile.getComment())) RemoteDao.UserProfile.insertComment(comment);

        loadMyUserProfileFromRemote();
    }

    public void saveProfileImageToRemote(String imageUrl) {
        // todo random key...
        String fileName = DateUtil.getCurrentDate() + "_" + myUserAccessKey;// 시간값 + UUID 추가해서 만듦...
        FirebaseUploader.execute(imageUrl, Const.StorageRefKey.USER_PROFILE, fileName, new FirebaseUploader.Callback() {
            @Override
            public void postExecute(Uri uploadedFileUri) {
                String modelDir = getModelDir(Const.RefKey.USER_PROFILE, myUserAccessKey)
                        + Const.RefKey.USER_PROFILE + "/" + Const.RefKey.USER_PROFILE_IMAGE_URI;
                FirebaseDatabase.getInstance().getReference(modelDir).setValue(uploadedFileUri.toString());

                loadMyUserProfileFromRemote();
                Log.i(TAG, "Add profile image to remote successful" + uploadedFileUri);
            }
        });
    }



    @Deprecated
    public void saveProfileToRemote(final String name, final String work, final int age, final String comment, String imageUrl) {
        UserProfile currentProfile = UserStatusPresenter.getInstance().myUserProfile;
        if(currentProfile == null) return;

        String fileName = DateUtil.getCurrentDate() + "_" + myUserAccessKey;// 시간값 + UUID 추가해서 만듦...
        FirebaseUploader.execute(imageUrl, Const.StorageRefKey.USER_PROFILE, fileName, new FirebaseUploader.Callback() {
            @Override
            public void postExecute(Uri uploadedFileUri) {
                String modelDir = getModelDir(Const.RefKey.USER_PROFILE, myUserAccessKey)
                        + Const.RefKey.USER_PROFILE + "/" + Const.RefKey.USER_PROFILE_IMAGE_URI;
                FirebaseDatabase.getInstance().getReference(modelDir).setValue(uploadedFileUri.toString());
                saveProfileToRemote(name, work, age, comment);
                Log.i(TAG, "Add profile image to remote successful" + uploadedFileUri);
            }
        });
    }

    public void notifyMyProfileUpdate(){
        for (UserProfileView view : userProfileViewList) {
            view.updateProfile();
        }
    }

    public void addUserProfileView(UserProfileView view) {
        this.userProfileViewList.add(view);
    }

    public void removeUserProfileView(UserProfileView view) {
        this.userProfileViewList.remove(view);
    }

}
