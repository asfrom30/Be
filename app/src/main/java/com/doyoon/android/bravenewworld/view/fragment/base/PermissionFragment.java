package com.doyoon.android.bravenewworld.view.fragment.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.doyoon.android.bravenewworld.z.util.Const;

/**
 * Created by DOYOON on 7/16/2017.
 */

public abstract class PermissionFragment extends UserBaseFragment {

    private static final String TAG = PermissionFragment.class.getSimpleName();

    private Callback callback;

    /* Runtime Permission Check for getting My Location */
    public void checkRuntimePermission(Callback callback) {

        this.callback = callback;

        if (isPermissionsGranted()) {
            callback.runWithPermission();
        } else {
            /* If permission is not granted, Request permission at once  */
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, Const.LOCATION_REQ_CODE);
            } else {
                // Permission이 없으면 서비스를 정상적으로 이용할수 없습니다.
            }
        }
    }

    private boolean isPermissionsGranted(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Const.LOCATION_REQ_CODE) {
            if (isPermissionsGranted()) {
                callback.runWithPermission();
            } else {
                Log.e(TAG, "권한이 없으면 서비스를 정상적으로 이용할 수 없습니다.");
                Toast.makeText(getActivity(), "권한이 없으면 서비스를 정상적으로 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            // lastTryToSetEnableMyLocation();
        }
    }

    // protected abstract void runFragment();
    // protected abstract void runFragmentWithoutPermission();

    public interface Callback {
        void runWithPermission();
        void runWithoutPermission();
    }

}
