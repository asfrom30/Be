package com.doyoon.android.bravenewworld.domain.firebase.value;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseModel;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class Chat extends FirebaseModel {

    private String key;
    private String message;
    private String ownerKey;

    public Chat() {

    }

    public Chat(String message, String ownerKey) {
        this.message = message;
        this.ownerKey = ownerKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOwnerKey() {
        return ownerKey;
    }

    public void setOwnerKey(String ownerKey) {
        this.ownerKey = ownerKey;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void setKey(String key) {

    }
}
