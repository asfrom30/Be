package com.doyoon.android.bravenewworld.domain.firebase.value;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseModel;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class ChatProfile extends FirebaseModel{

    private boolean matchingFlag;

    private String giverKey;
    private String takerKey;
    private String chatAccessKey;

    public ChatProfile() {
    }

    public ChatProfile(String chatAccessKey) {
        this.chatAccessKey = chatAccessKey;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void setKey(String key) {

    }

    public String getGiverKey() {
        return giverKey;
    }

    public void setGiverKey(String giverKey) {
        this.giverKey = giverKey;
    }

    public String getTakerKey() {
        return takerKey;
    }

    public void setTakerKey(String takerKey) {
        this.takerKey = takerKey;
    }

    public boolean isMatchingFlag() {
        return matchingFlag;
    }

    public void setMatchingFlag(boolean matchingFlag) {
        this.matchingFlag = matchingFlag;
    }

    public String getChatAccessKey() {
        return chatAccessKey;
    }

    public void setChatAccessKey(String chatAccessKey) {
        this.chatAccessKey = chatAccessKey;
    }

}
