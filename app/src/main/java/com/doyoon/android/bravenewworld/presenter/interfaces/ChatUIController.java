package com.doyoon.android.bravenewworld.presenter.interfaces;

import com.doyoon.android.bravenewworld.domain.firebase.value.Chat;

/**
 * Created by DOYOON on 7/17/2017.
 */

public interface ChatUIController {
    void addChat(Chat chat);
    void notifySetChanged();
    void setFocusLastItem();
    void updateProfileView();
    void updateTitle();
}
