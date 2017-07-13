package com.doyoon.android.bravenewworld.presenter.activity.interfaces;

import com.doyoon.android.bravenewworld.domain.firebase.value.Invite;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;

/**
 * Created by DOYOON on 7/13/2017.
 */

public interface InviteDialog {
    void showInvitedDialog(Invite invite);
    void showInvitingDialog(UserProfile invitingTargetUserProfile, int userType);
}
