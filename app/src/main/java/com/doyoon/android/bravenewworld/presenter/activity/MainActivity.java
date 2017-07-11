package com.doyoon.android.bravenewworld.presenter.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.presenter.fragment.UserMapFragment;

public class MainActivity extends FragmentActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Construct DB Structure */
        FirebaseHelper.buildDbStructure(getBaseContext());

        /* Create Dummy */
        /*
        for(int i =0; i < 10; i++) {
            Giver giver = DummyDao.createDummyGiver();
            FirebaseDao.insert(giver);
            Log.i(TAG, giver.toString());
        }
        */

        /* Log in activity 에서 log in 정보를 받아 온다.... */
        startFragment(UserMapFragment.newInstance());

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
