package com.doyoon.android.bravenewworld.presenter.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.DummyDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseGeoDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.fragment.UserMapFragment;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Construct DB Structure */
        FirebaseHelper.buildDbStructure(getBaseContext());

        /* Create Dummy */
        // this.createDummy();

        /* Log in activity 에서 log in 정보를 받아 온다.... */
        startFragment(UserMapFragment.newInstance());

    }

    private void createDummy(){
        /* Create MY Dummy */
        UserProfile myUserProfile = DummyDao.createDummyMyProfile();
        FirebaseDao.insert(myUserProfile);

        for(int i =0; i < 10; i++) {
            /* Create Dummy Profile */
            UserProfile userProfile = DummyDao.createDummyUserProfile();
            Log.i(TAG, "Create Dummy User : " + userProfile.toString());
            FirebaseDao.insert(userProfile);

            /* Insert givers using Geo Fire */
            LatLng latLng = DummyDao.createDummyLatLng();
            String modelDir = FirebaseHelper.getModelDir("giver");
            FirebaseGeoDao.insert(modelDir, userProfile.getKey(), new GeoLocation(latLng.latitude, latLng.longitude));
        }
    }

    public void startFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.main_frame_layout, fragment);
        transaction.commit();
    }

    public void goFragment(Fragment fragment) {
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.main_frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goPrevFragment(){
        FragmentManager manager = this.getSupportFragmentManager();
        manager.popBackStack();
    }
}
