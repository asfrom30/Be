package com.doyoon.android.bravenewworld.presenter.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.ConvString;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class PickmeRequestSendingDialog extends DialogFragment {

    private static final String TAG = PickmeRequestSendingDialog.class.getSimpleName();

    // Use this instance of the interface to deliver action events
    private int userType;
    private Callback callback;
    private UserProfile targetUserProfile;


    public PickmeRequestSendingDialog(int userType, UserProfile targetUserProfile, Callback callback) {
        this.userType = userType;
        this.callback = callback;
        this.targetUserProfile = targetUserProfile;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_invite, null))
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

        if(userType == Const.UserType.Taker){
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
        String invitingTargetUserAccessKey = ConvString.commaSignToString(targetUserProfile.getEmail());
        PickMeRequest pickMeRequest = new PickMeRequest();
        if (Const.MY_USER_PROFILE == null) {
            Log.e(TAG, "User Profile is null, can not send pick me request");
            return;
        }
        pickMeRequest.fetchDataFromUserProfile(Const.MY_USER_PROFILE);

        /* Add Pick Me Request to Target */
        FirebaseDao.insert(pickMeRequest, invitingTargetUserAccessKey);
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
