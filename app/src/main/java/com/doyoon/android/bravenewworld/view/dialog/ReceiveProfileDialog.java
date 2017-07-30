package com.doyoon.android.bravenewworld.view.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.domain.reactivenetwork.ReactiveInviteResponse;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.util.ConvString;
import com.doyoon.android.bravenewworld.util.view.ProgressHelper;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by DOYOON on 7/27/2017.
 */

public class ReceiveProfileDialog extends DialogFragment {

    private PickMeRequest pickMeRequest;

    // Use this instance of the interface to deliver action events

    public ReceiveProfileDialog(PickMeRequest pickMeRequest) {
        this.pickMeRequest = pickMeRequest;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_receive_profile);


        ImageView dialogProfileImage = (ImageView) dialog.findViewById(R.id.dialog_profile_image);
        TextView dialogProfileName = (TextView) dialog.findViewById(R.id.dialog_profile_name);
        TextView dialogProfileDetail = (TextView) dialog.findViewById(R.id.dialog_profile_detail);
        TextView dialogProfileDistance = (TextView) dialog.findViewById(R.id.dialog_profile_distance);
        ViewGroup sendViewGroup = (ViewGroup) dialog.findViewById(R.id.dialog_profile_send);

        /* Custom UserProfileView */
        // todo null check
        dialogProfileName.setText(pickMeRequest.getName());
        dialogProfileDetail.setText(pickMeRequest.getAge() + ", "
                + ConvString.getGender(pickMeRequest.getGender()));

        dialogProfileDistance.setText(pickMeRequest.getDistance());

        if (pickMeRequest.getImageUrl() != null) {
            Glide.with(this).load(pickMeRequest
                    .getImageUrl())
                    .bitmapTransform(new CenterCrop(getActivity()), new RoundedCornersTransformation(getActivity(), 25, 0, RoundedCornersTransformation.CornerType.TOP))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            preImageLoadFlag = true;
                            if(progressDialog != null) progressDialog.dismiss();
                            return false;
                        }
                    })
                    .into(dialogProfileImage);
        }

        sendViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPositiveClicked();
                dismiss();
            }
        });



        return dialog;
    }

    private ProgressDialog progressDialog;
    private boolean preImageLoadFlag = false;

    private void onPositiveClicked(){
        ReactiveInviteResponse.hasActiveUser(pickMeRequest.getFromUserAccessKey(), new ReactiveInviteResponse.Callback() {
            @Override
            public void userExist() {   // 초대에 응했는데 아직 있다면...
                AppPresenter.getInstance().acceptPickmeRequestNotice(pickMeRequest);
            }

            @Override
            public void userNotExist() {
                // todo Another Dialog... 사용자가 다른 사용자와 매칭 되었습니다.
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        /* Progress dialog */
        if(!preImageLoadFlag){
            progressDialog = ProgressHelper.toShowDefaultDialog(getContext(), getString(R.string.progress_dialog_pull_profile));
        }
    }
}
