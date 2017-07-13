package com.doyoon.android.bravenewworld.domain.reactivenetwork;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class ReactiveInviteResponse {

    public static String REF_ACTIVE_USER = "activeuser";

    public static void hasActiveUser(String userAccessKey, final Callback callback) {
        String modelDir = FirebaseHelper.getModelDir(REF_ACTIVE_USER);
        FirebaseDatabase.getInstance().getReference(modelDir + userAccessKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.userExist();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.userNotExist();
            }
        });
    }

    public interface Callback {
        void userExist();
        void userNotExist();
    }
}
