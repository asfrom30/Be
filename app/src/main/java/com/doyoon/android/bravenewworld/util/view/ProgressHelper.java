package com.doyoon.android.bravenewworld.util.view;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by DOYOON on 7/22/2017.
 */

public class ProgressHelper {

    public static ProgressDialog toShowDefaultDialog(Context context, String title){

        ProgressDialog dialog = new ProgressDialog(context);

        dialog.setMessage(title);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(true);
        dialog.setProgressNumberFormat(null);
        dialog.setProgressPercentFormat(null);
//        dialog.setProgress(0);
        dialog.show();

        return dialog;
    }

//    /* Data Loading Progress Dialog*/
//    public void startInProgress() {
//
//        progressDialog.setMessage("Downloading Music");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setProgress(0);
//        progressDialog.showWithPreImageLoad();
//        final int totalProgressTime = 100;
//        final Thread t = new Thread() {
//            @Override
//            public void run() {
//                int jumpTime = 0;
//
//                while (jumpTime < totalProgressTime) {
//                    try {
//                        sleep(200);
//                        jumpTime += 5;
//                        progressDialog.setProgress(jumpTime);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        t.start();
//    }
//
//    public void endInProgress() {
//        progressDialog.dismiss();
//    }

}
