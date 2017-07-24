package com.doyoon.android.bravenewworld.presenter.listener;

import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.Chat;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by DOYOON on 7/24/2017.
 */

public class ChatListener {

    public static final String TAG = ChatListener.class.getSimpleName();

    public static ChatListener instance;

    public static ChatListener getInstance(){
        if (instance == null) {
            instance = new ChatListener();
        }

        return instance;
    }

    private ChatListener() {

    }

    private ChildEventListener chatListener;
    private String currentChatAccessKey;        // buffer chat accesskey. prevent to add listener twice

    public void addChatListener(final Callback callback){

        String chatAccessKey = UserStatusPresenter.chatAccessKey;

        if(chatAccessKey == null) {Log.i(TAG, "Access key is null, addChatListener can't start");
            return;
        }

        if (chatListener != null) {Log.i(TAG, "Chat Listener is not null. Try to remove chat listener.");
            removeChatListener();
        }

        this.currentChatAccessKey = chatAccessKey;

        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.CHAT, chatAccessKey);
        FirebaseDatabase.getInstance().getReference(modelDir).addChildEventListener(this.chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (chat == null) { Log.e(TAG, "Chat is null");
                    return;
                }
                callback.onChatAdded(chat);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeChatListener(){

        if (this.currentChatAccessKey == null) {Log.e(TAG, "chatAccessKey is null, can't removeChatListener");
            return;
        }

        if (this.chatListener != null) {Log.e(TAG, "chatListener already null, can't removeChatListener");
            return;
        }

        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.CHAT, currentChatAccessKey);
        FirebaseDatabase.getInstance().getReference(modelDir).removeEventListener(this.chatListener);

        this.chatListener = null;
        this.currentChatAccessKey = null;
    }

    public interface Callback {
        void onChatAdded(Chat chat);
    }
}
