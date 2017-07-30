package com.doyoon.android.bravenewworld.presenter.permission;

/**
 * Created by DOYOON on 7/28/2017.
 */

public class PermissionPresenter {

    // private static String[] permissions = Manifest.permission.ACCESS_FINE_LOCATION;


    private String[] permissions;
    private RunCallback runCallback;
    private CancleCallback cancleCallback;

    public static PermissionPresenter newInstance(String[] permissions, RunCallback runCallback, CancleCallback cancleCallback){
        return new PermissionPresenter(permissions, runCallback, cancleCallback);
    }

    public PermissionPresenter(String[] permissions, RunCallback runCallback, CancleCallback cancleCallback) {
        this.permissions = permissions;
        this.runCallback = runCallback;
        this.cancleCallback = cancleCallback;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        boolean granted = true;
//
//        for (int isGrant : grantResults) {
//            if(isGrant != PackageManager.PERMISSION_GRANTED){
//                granted = false;
//                break;
//            }
//        }
//        if(granted) callback.run();
//        else callback.cancel();
//
//
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        RuntimePermissionUtil.postPermissionResult(requestCode, permissions, grantResults, new RuntimePermissionUtil.Callback() {
//
//            @Override
//            public void run() {
//                setButtonsEnabled(true);
//            }
//
//            @Override
//            public void cancel() {
//                SnackBarHelper.showWithPreImageLoad(getActivity(), "서비스를 정상적으로 이용하시려면 위치서비스가 필요합니다", null, null);
//            }
//
//        });
//    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public RunCallback getRunCallback() {
        return runCallback;
    }

    public void setRunCallback(RunCallback runCallback) {
        this.runCallback = runCallback;
    }

    public CancleCallback getCancleCallback() {
        return cancleCallback;
    }

    public void setCancleCallback(CancleCallback cancleCallback) {
        this.cancleCallback = cancleCallback;
    }

    public interface RunCallback {
        void execute();
    }

    public interface CancleCallback {
        void execute();
    }
}
