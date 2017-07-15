package com.doyoon.android.bravenewworld.presenter.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.util.Const;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class PickMeRequestDialog extends DialogFragment {

    // Use this instance of the interface to deliver action events
    DialogListener listener;
    int userType;


    public PickMeRequestDialog(int userType, DialogListener listener) {
        this.userType = userType;
        this.listener = listener;
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
                        listener.onDialogNegativeClick();
                    }
                });

        if(userType == Const.UserType.Taker){
            builder.setPositiveButton("PickMeRequest", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    listener.onDialogPositiveClick();
                }
            });
        }

        return builder.create();
    }


}
