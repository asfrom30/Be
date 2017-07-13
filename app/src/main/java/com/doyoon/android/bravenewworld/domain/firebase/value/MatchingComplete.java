package com.doyoon.android.bravenewworld.domain.firebase.value;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class MatchingComplete {
    private String from;
    private String chatAccessKey;

    public MatchingComplete() {
    }

    public MatchingComplete(String from, String chatAccessKey) {
        this.from = from;
        this.chatAccessKey = chatAccessKey;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getChatAccessKey() {
        return chatAccessKey;
    }

    public void setChatAccessKey(String chatAccessKey) {
        this.chatAccessKey = chatAccessKey;
    }
}
