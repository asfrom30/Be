package com.doyoon.android.bravenewworld.domain;

import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.util.Const;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by DOYOON on 7/14/2017.
 */

public class RemoteDao {

    public static class FetchUserProfile {
        private static String TAG = FetchUserProfile.class.getSimpleName();

        public static void execute(String userAccessKey, final FetchUserProfile.Callback callback){
            String modelDir = FirebaseHelper.getModelDir("userprofile", userAccessKey);
            FirebaseDatabase.getInstance().getReference(modelDir + "userprofile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    callback.postExecute(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(FetchUserProfile.TAG, databaseError.toString());
                }
            });
        }

        public interface Callback {
            void postExecute(DataSnapshot dataSnapshot);
        }
    }


    public static class FetchMyProfile {
        public static void execute(){
            String modelDir = FirebaseHelper.getModelDir(Const.RefKey.USER_PROFILE, UserStatusPresenter.myUserAccessKey) + Const.RefKey.USER_PROFILE;
            FirebaseDatabase.getInstance().getReference(modelDir).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    UserStatusPresenter.myUserProfile = userProfile;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
