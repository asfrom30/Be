package com.doyoon.android.bravenewworld.presenter.listener;

import android.util.Log;

import com.doyoon.android.bravenewworld.domain.RemoteDao;
import com.doyoon.android.bravenewworld.domain.firebase.value.LocationFinder;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by DOYOON on 7/24/2017.
 */

public class OtherUserLocationListener {

    /* Signleton */
    public static final String TAG = OtherUserLocationListener.class.getSimpleName();
    public static OtherUserLocationListener instance;
    public static OtherUserLocationListener getInstance(){
        if (instance == null) {
            instance = new OtherUserLocationListener();
        }
        return instance;
    }

    private OtherUserLocationListener() {
    }

    private String currentLocationFinderAccessKey;
    private String currentOtherUserAccessKey;
    private ValueEventListener locationListener;

    public void addOtherLocationListener(final Callback callback){
        String locationAccessKey = UserStatusPresenter.getInstance().locationFinderAccessKey;
        String otherUserAccessKey = UserStatusPresenter.getInstance().otherUserAccessKey;

        if(locationAccessKey == null) return;
        if(otherUserAccessKey == null) return;

        if(locationListener != null) removeOtherLocationListener();

        this.currentLocationFinderAccessKey = locationAccessKey;
        this.currentOtherUserAccessKey = otherUserAccessKey;

        this.locationListener = RemoteDao.LocationFinder.addListener(locationAccessKey, otherUserAccessKey
                , new RemoteDao.LocationFinder.Callback(){
                    @Override
                    public void execute(LocationFinder locationFinder) {
                        if(locationFinder == null) return;
                        // todo latitude and longitude validate check
                        if(locationFinder.getLatitude() == 0 || locationFinder.getLongitude() == 0) return;
                        callback.execute(locationFinder);

                        Log.i(TAG, "updateProfile other user  location in ui controller");
                    }
                });

        Log.i(TAG, "Add Location Listener Successfull");
    }


    public void removeOtherLocationListener() {

        if(this.currentLocationFinderAccessKey == null) return;
        if(this.currentOtherUserAccessKey == null) return;

        RemoteDao.LocationFinder.removeListener(this.currentLocationFinderAccessKey, this.currentOtherUserAccessKey, this.locationListener);

        this.locationListener = null;
        this.currentLocationFinderAccessKey = null;
        this.currentOtherUserAccessKey = null;


        Log.i(TAG, "remove Location Listener Successful");
    }

    public interface Callback {
        void execute(LocationFinder locationFinder);
    }
}
