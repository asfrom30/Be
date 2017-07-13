package com.doyoon.android.bravenewworld.presenter.activity.interfaces;

import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;

/**
 * Created by DOYOON on 7/13/2017.
 */

public interface InviteDialog {
    void showInvitedDialog(PickMeRequest pickMeRequest);
    void showInvitingDialog(UserProfile invitingTargetUserProfile, int userType);
}
