package com.doyoon.android.bravenewworld.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by DOYOON on 6/16/2017.
 */
/*
    Version : 0.1.0
    Version : 0.1.1 2017/07/19
 */

public class RuntimePermissionUtil {
    private static final String TAG = RuntimePermissionUtil.class.getName();
    private static final int REQ_CODE = 291623;

    public static boolean hasPermissions(Context context, String[] permissions){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : permissions) {
            if(context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    public static void requestAndRunOrNot(Activity activity, String[] permissions, Callback callback){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            RuntimePermissionUtil.request(activity, callback, permissions);
        } else {
            callback.run();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)   /* 해당함수는 마쉬멜로우 버전(23) 이상에서만 실행이 됩니다. */
    private static void request(Activity activity, Callback callback, String[] permissions){

        boolean hasPermissions = RuntimePermissionUtil.hasPermissions(activity, permissions);

        if(hasPermissions){
            callback.run();
        } else {
            activity.requestPermissions(permissions, REQ_CODE); // Request Permission POP-UP
        }
    }

    public static void postPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Callback callback){

        if (requestCode != REQ_CODE) {
            Log.e(TAG, "REQUEST CODE를 확인해주세요");
        }

        boolean granted = true;

        for (int isGrant : grantResults) {
            if(isGrant != PackageManager.PERMISSION_GRANTED){
                granted = false;
                break;
            }
        }
        if(granted) callback.run();
        else callback.cancel();
    }

    public interface Callback {
        void run();
        void cancel();
    }
}
