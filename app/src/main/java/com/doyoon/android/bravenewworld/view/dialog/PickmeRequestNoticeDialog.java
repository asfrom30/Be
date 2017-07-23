package com.doyoon.android.bravenewworld.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.domain.reactivenetwork.ReactiveInviteResponse;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.util.ConvString;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class PickmeRequestNoticeDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    private Callback callback;
    private PickMeRequest pickMeRequest;

    public PickmeRequestNoticeDialog(PickMeRequest pickMeRequest, Callback callback) {
        this.pickMeRequest = pickMeRequest;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_invited, null);

        /* Custom UserProfileView */
        TextView textView = (TextView) view.findViewById(R.id.response_sending_textview);
        textView.setText(pickMeRequest.getName() + ", "
                + pickMeRequest.getAge() + ", "
                + ConvString.getGender(pickMeRequest.getGender()));

        if (pickMeRequest.getImageUrl() != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.request_sending_imageView);
            Glide.with(this).load(pickMeRequest.getImageUrl()).bitmapTransform(new CropCircleTransformation(getContext())).into(imageView);
        }

        Log.e("TAG", pickMeRequest.toString());

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("수락", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if (callback != null) {
                            callback.onPreExecuteInPositiveClicked();
                        }

                        onPositiveClicked();

                        if (callback != null) {
                            callback.onPostExecuteInPositiveClicked();
                        }
                    }
                })
                .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null) {
                            callback.onPreExecuteInNegativeClicked();
                        }

                        onNegativeClicked();

                        if (callback != null) {
                            callback.onPostExecuteInNegativeClicked();
                        }
                    }
                });

        return builder.create();
    }

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

    private void onNegativeClicked(){

    }


    public abstract static class Callback {
        public void onPreExecuteInPositiveClicked(){};
        public void onPostExecuteInPositiveClicked(){};
        public void onPreExecuteInNegativeClicked(){};
        public void onPostExecuteInNegativeClicked(){};
    }
}
