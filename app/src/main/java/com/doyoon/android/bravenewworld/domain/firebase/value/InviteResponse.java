package com.doyoon.android.bravenewworld.domain.firebase.value;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class InviteResponse {

    private String from;
    private String chatAccessKey;

    public InviteResponse() {
    }

    public InviteResponse(String from, String chatAccessKey) {
        this.from = from;
        this.chatAccessKey = chatAccessKey;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
