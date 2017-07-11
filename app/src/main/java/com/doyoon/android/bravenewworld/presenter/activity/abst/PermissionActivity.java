package com.doyoon.android.bravenewworld.presenter.activity.abst;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by DOYOON on 6/27/2017.
 */

public abstract class PermissionActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String permissions[] = throwNeedPermissions();
        this.requestAndRunOrNot(this, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.postPermissionResult(requestCode, permissions, grantResults);
    }

    private static final String TAG = PermissionActivity.class.getName();
    private static final int REQ_CODE = 291623;
    public abstract String[] throwNeedPermissions();
    public abstract void run();
    public abstract void cancel();

    private void requestAndRunOrNot(Activity activity, String[] permissions){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            this.request(activity, permissions);
        } else {
            this.run();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)   /* 해당함수는 마쉬멜로우 버전(23) 이상에서만 실행이 됩니다. */
    private void request(Activity activity, String[] permissions){

        boolean hasPermissions = true;

        Context context = activity.getBaseContext();

        /* 필요한 권한중에 하나라도 없으면 hasPersmission == false */
        for (String permission : permissions) {
            if(context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED){
                hasPermissions = false;
                break;
            }
        }

        if(hasPermissions){
            this.run();
        } else {
            activity.requestPermissions(permissions, REQ_CODE); // Request Permission POP-UP
        }
    }

    private void postPermissionResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

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
        if(granted) this.run();
        else this.cancel();
    }

}
