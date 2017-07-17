package com.doyoon.android.bravenewworld.domain.firebase.value;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseModel;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class MatchingComplete extends FirebaseModel {
    private String giverAccessKey;
    private String takerAccessKey;
    private String chatAccessKey;

    public MatchingComplete() {
    }

    public MatchingComplete(String giverAccessKey, String takerAccessKey, String chatAccessKey) {
        this.takerAccessKey = takerAccessKey;
        this.giverAccessKey = giverAccessKey;
        this.chatAccessKey = chatAccessKey;
    }

    public String getTakerAccessKey() {
        return takerAccessKey;
    }

    public void setTakerAccessKey(String takerAccessKey) {
        this.takerAccessKey = takerAccessKey;
    }

    public String getChatAccessKey() {
        return chatAccessKey;
    }

    public void setChatAccessKey(String chatAccessKey) {
        this.chatAccessKey = chatAccessKey;
    }


    public String getGiverAccessKey() {
        return giverAccessKey;
    }

    public void setGiverAccessKey(String giverAccessKey) {
        this.giverAccessKey = giverAccessKey;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void setKey(String key) {

    }

    @Override
    public String toString() {
        return "MatchingComplete{" +
                "giverAccessKey='" + giverAccessKey + '\'' +
                ", takerAccessKey='" + takerAccessKey + '\'' +
                ", chatAccessKey='" + chatAccessKey + '\'' +
                '}';
    }
}
