package com.doyoon.android.bravenewworld;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.presenter.UserProfilePresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.ViewPagerMover;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LogUtil;
import com.doyoon.android.bravenewworld.util.view.ViewPagerBuilder;
import com.doyoon.android.bravenewworld.view.fragment.ChatFragment;
import com.doyoon.android.bravenewworld.view.fragment.LocationFragment;
import com.doyoon.android.bravenewworld.view.fragment.ProfileFragment;
import com.doyoon.android.bravenewworld.view.fragment.SelectFragment;
import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * 나는 뭐하는 놈입니다~
 */
public class MainActivity extends AppCompatActivity implements ViewPagerMover{

    private static String TAG = MainActivity.class.getSimpleName();

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "on Create");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Move to Splash */
        // Construct DB Structure
        FirebaseHelper.buildDbStructure(getBaseContext());

        /* Create Dummy */
        // DummyDao.insertDummyMyProfile();
        // DummyDao.createDummies(Const.RefKey.ACTIVE_USER_TYPE_GIVER);
        // DummyDao.createDummies(Const.RefKey.ACTIVE_USER_TYPE_TAKER);

        /* Get User Access Key */
        // String myUserAcessKey = getMyDummyUserAccessKey();
        String myUserAcessKey = getIntent().getStringExtra(Const.ExtraKey.USER_ACCESS_KEY);
        Log.e(TAG, "login Successfully" + myUserAcessKey);

        /* Build AppPresenter */
        buildPresenter(myUserAcessKey);

        /* Load Default Data from Remote*/
        UserProfilePresenter.getInstance().loadMyUserProfileFromRemote();

        /* make view pager */
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);

        ViewPagerBuilder.getInstance(viewPager, getBaseContext())
                .addFragment(SelectFragment.newInstance())
                .addFragment(LocationFragment.newInstance())
                .addFragment(ChatFragment.newInstance())
                .addFragment(ProfileFragment.newInstance())
                .linkTabLayout(tabLayout)
                .setFirstSelectedTabColor()
                .build(getSupportFragmentManager());
    }

    private void buildPresenter(String myUserAccessKey){
        /* UserStatusPresenter */
        UserStatusPresenter.myUserAccessKey = myUserAccessKey;
        UserProfilePresenter.getInstance();

        // todo Search what difference between baseContext and applicationContext
        /* App Presenter */
        AppPresenter.getInstance().initialize(this);
        AppPresenter.getInstance().setViewPagerMover(this);
    }

    /* Activity Cycle*/
    @Override
    protected void onPause() {
        AppPresenter.getInstance().stop();
        super.onPause();
    }



    @Override
    protected void onResume() {
        AppPresenter.getInstance().restart();
        super.onResume();
    }


    /* Apply font all app */
    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void moveViewPage(int targetViewPage) {
        if (viewPager == null) {
            return;
        }
        viewPager.setCurrentItem(targetViewPage);
    }
}
