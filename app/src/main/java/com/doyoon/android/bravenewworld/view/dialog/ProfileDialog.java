package com.doyoon.android.bravenewworld.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.z.util.ConvString;

/**
 * Created by DOYOON on 7/27/2017.
 */

public class ProfileDialog extends DialogFragment {

    private UserProfile userProfile;
    // Use this instance of the interface to deliver action events
    private Callback callback;

    public ProfileDialog(UserProfile userProfile, Callback callback) {
        this.userProfile = userProfile;
        this.callback = callback;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_profile);


        ImageView dialogProfileImage = (ImageView) dialog.findViewById(R.id.dialog_profile_image);
        TextView dialogProfileName = (TextView) dialog.findViewById(R.id.dialog_profile_name);
        TextView dialogProfileDetail = (TextView) dialog.findViewById(R.id.dialog_profile_detail);
        TextView dialogProfileDistance = (TextView) dialog.findViewById(R.id.dialog_profile_distance);

        /* Custom UserProfileView */
        dialogProfileDetail.setText(userProfile.getName() + ", "
                + userProfile.getAge() + ", "
                + ConvString.getGender(userProfile.getGender()));

        if (userProfile.getImageUri() != null) {
            Glide.with(this).load(userProfile.getImageUri()).into(dialogProfileImage);
        }

        return dialog;
    }

    private void onPositiveClicked(){
//        ReactiveInviteResponse.hasActiveUser(pickMeRequest.getFromUserAccessKey(), new ReactiveInviteResponse.Callback() {
//            @Override
//            public void userExist() {   // 초대에 응했는데 아직 있다면...
//                AppPresenter.getInstance().acceptPickmeRequestNotice(pickMeRequest);
//            }
//
//            @Override
//            public void userNotExist() {
//                // todo Another Dialog... 사용자가 다른 사용자와 매칭 되었습니다.
//            }
//        });
    }

    private void onNegativeClicked(){

    }

    public abstract static class Callback {
        public void onPreExecuteInPositiveClicked(){};
        public void onPostExecuteInPositiveClicked(){};
        public void onPreExecuteInNegativeClicked(){};
        public void onPostExecuteInNegativeClicked(){};
    }

}
