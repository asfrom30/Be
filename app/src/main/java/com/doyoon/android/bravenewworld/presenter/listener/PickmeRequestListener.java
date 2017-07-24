package com.doyoon.android.bravenewworld.presenter.listener;

import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;

/**
 * Created by DOYOON on 7/23/2017.
 */

public class PickmeRequestListener {

    private static final String TAG = PickmeRequestListener.class.getSimpleName();

    private static PickmeRequestListener instance;

    public static PickmeRequestListener getInstance() {
        if (instance == null) {
            instance = new PickmeRequestListener();
        }
        return instance;
    }


    private ChildEventListener pickmeRequestListener;

    private PickmeRequestListener() {

    }

    public void addPickMeRequestListener(final Callback callback){

        if (this.pickmeRequestListener != null) {
            removePickMeRequestListener();
        }

        String modelDir = getModelDir(Const.RefKey.PICK_ME_REQUEST, UserStatusPresenter.myUserAccessKey);
        FirebaseDatabase.getInstance().getReference(modelDir).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // todo  남은 메세지수 몇개... 방향키로 메세지 이동할 수 있게 할 것...
                // todo Message를 add만 하고 updateProfile ui하게끔 onChilAdded와 같은 Thread를 쓰면 안된다.
                // todo 이것은 그냥 데이터만 추가하고.... 다른 쓰레드에서 update를 갱신할 수 있도록
                // todo 여기에 다 추가하면... firebase firebase 업데이트 속도가 느려질 수 있겠지만 큰 상관은 없을듯...
                PickMeRequest pickMeRequest = dataSnapshot.getValue(PickMeRequest.class);

                if(pickMeRequest == null) return;
                callback.execute(pickMeRequest);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled");
            }
        });
    }

    public void removePickMeRequestListener(){
        if (this.pickmeRequestListener == null) {
            Log.e(TAG, "pickmeRequestListener already null, can't remove complete listener");
            return;
        }

        String modelDir = getModelDir(Const.RefKey.PICK_ME_REQUEST, UserStatusPresenter.myUserAccessKey);
        FirebaseDatabase.getInstance().getReference(modelDir).removeEventListener(pickmeRequestListener);
        Log.i(TAG, "removePickMeRequestListener Succesfully");
    }

    public interface Callback {
        void execute(PickMeRequest pickMeRequest);
    }

}
