package com.doyoon.android.bravenewworld.presenter.listener;

import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.MatchingComplete;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Created by DOYOON on 7/23/2017.
 */

public class MatchingCompleteListener {

    private static MatchingCompleteListener instance;

    public static MatchingCompleteListener getInstance() {
        if (instance == null) {
            instance = new MatchingCompleteListener();
        }
        return instance;
    }

    /* Firebase : Matching Complete Listener on Firebase */
    private ValueEventListener matchingCompleteListener;


    private MatchingCompleteListener() {

    }

    public void addMatchingCompleteListener(final Callback callback){
        if (matchingCompleteListener != null) {
            removeMatchingCompleteListener();
        }

        final String modelPath = FirebaseHelper.getModelPath(Const.RefKey.MATCHING_COMPLETE, UserStatusPresenter.myUserAccessKey);
        Log.e(TAG, "matching complete model path is " + modelPath);
        this.matchingCompleteListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MatchingComplete matchingComplete = dataSnapshot.getValue(MatchingComplete.class);
                // todo if value is empty but traceAndExecute first at once???
                if (matchingComplete == null) {
                    Log.e(TAG, "matching complete is null");
                    return;
                }

                callback.execute(matchingComplete);

                Log.i(TAG, matchingComplete.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference(modelPath).addValueEventListener(this.matchingCompleteListener);
    }

    public void removeMatchingCompleteListener(){
        if (this.matchingCompleteListener == null) {
            Log.e(TAG, "matchingCompleteListener already null, can't remove complete listener");
            return;
        }
        String modelPath = FirebaseHelper.getModelPath(Const.RefKey.MATCHING_COMPLETE, UserStatusPresenter.myUserAccessKey);
        FirebaseDatabase.getInstance().getReference(modelPath).removeEventListener(this.matchingCompleteListener);
        Log.i(TAG, "removeMatchingCompleteListener Succesfully");
    }

    public interface Callback {
        void execute(MatchingComplete matchingComplete);
    }
}
