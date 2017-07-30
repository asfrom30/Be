package com.doyoon.android.bravenewworld.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.local.ActiveUserProfile;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.ConvString;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class SendPickmeRequestDialog extends DialogFragment {

    private static final String TAG = SendPickmeRequestDialog.class.getSimpleName();

    // Use this instance of the interface to deliver action events
    private int userType;
    private Callback callback;
    private ActiveUserProfile targetActiveUserProfile;


    public SendPickmeRequestDialog(int userType, ActiveUserProfile targetActiveUserProfile, Callback callback) {
        this.userType = userType;
        this.callback = callback;
        this.targetActiveUserProfile = targetActiveUserProfile;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_invite, null);

        /* Custom UserProfileView */
        TextView textView = (TextView) view.findViewById(R.id.request_sending_textview);
        textView.setText(targetActiveUserProfile.getName() + ", "
                + targetActiveUserProfile.getAge() + ", "
                + ConvString.getGender(targetActiveUserProfile.getGender()));

        if (targetActiveUserProfile.getImageUri() != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.request_sending_imageView);
            Glide.with(this).load(targetActiveUserProfile.getImageUri()).bitmapTransform(new CropCircleTransformation(getContext())).into(imageView);
        }


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(view)
                // Add action buttons
                .setNegativeButton("Show Detail Profile ", new DialogInterface.OnClickListener() {
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



        if(userType == Const.ActiveUserType.Taker){
            builder.setPositiveButton("PickMeRequest", new DialogInterface.OnClickListener() {
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
            });
        }

        return builder.create();
    }

    private void onPositiveClicked(){
        AppPresenter.getInstance().sendPickMeRequest(targetActiveUserProfile);
    }

    private void onNegativeClicked() {
        /* Show Detail Friend Profile... */
    }


    public abstract static class Callback {
        public void onPreExecuteInPositiveClicked(){};
        public void onPostExecuteInPositiveClicked(){};
        public void onPreExecuteInNegativeClicked(){};
        public void onPostExecuteInNegativeClicked(){};
    }


}
