package com.doyoon.android.bravenewworld.domain.firebase.value;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseModel;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class MatchingComplete extends FirebaseModel {

    private String fromUserAccessKey;
    private String chatAccessKey;

    public MatchingComplete() {
    }

    public MatchingComplete(String fromUserAccessKey, String chatAccessKey) {
        this.fromUserAccessKey = fromUserAccessKey;
        this.chatAccessKey = chatAccessKey;
    }

    public String getFromUserAccessKey() {
        return fromUserAccessKey;
    }

    public void setFromUserAccessKey(String fromUserAccessKey) {
        this.fromUserAccessKey = fromUserAccessKey;
    }

    public String getChatAccessKey() {
        return chatAccessKey;
    }

    public void setChatAccessKey(String chatAccessKey) {
        this.chatAccessKey = chatAccessKey;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void setKey(String key) {

    }
}
