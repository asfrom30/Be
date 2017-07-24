package com.doyoon.android.bravenewworld;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.doyoon.android.bravenewworld.domain.RemoteDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.presenter.UserProfilePresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.ViewPagerMover;
import com.doyoon.android.bravenewworld.view.fragment.ChatFragment;
import com.doyoon.android.bravenewworld.view.fragment.LocationFragment;
import com.doyoon.android.bravenewworld.view.fragment.ProfileFragment;
import com.doyoon.android.bravenewworld.view.fragment.SelectFragment;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.doyoon.android.bravenewworld.z.util.LogUtil;
import com.doyoon.android.bravenewworld.z.util.view.ViewPagerBuilder;
import com.tsengvn.typekit.TypekitContextWrapper;

import static com.doyoon.android.bravenewworld.domain.DummyDao.getMyDummyUserAccessKey;

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

        /* Get User Access Key */
        String myUserAcessKey = getMyDummyUserAccessKey();
        // String myUserAcessKey = getIntent().getStringExtra(Const.ExtraKey.USER_ACCESS_KEY);
        if (myUserAcessKey == null) {

        }

        /* Build AppPresenter */
        buildPresenter(myUserAcessKey);

        /* Load Default Data from Remote*/
        UserProfilePresenter.getInstance().loadMyUserProfileFromRemote();

        /* make view pager*/
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        TabLayout tabLayout = (TabLayout)  findViewById(R.id.main_tab_layout);

        ViewPagerBuilder.getInstance(viewPager)
                .addFragment(SelectFragment.newInstance())
                .addFragment(LocationFragment.newInstance())
                .addFragment(ChatFragment.newInstance())
                .addFragment(ProfileFragment.newInstance())
                .linkTabLayout(tabLayout)
                .build(getSupportFragmentManager());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.logLifeCycle(TAG, "On Pause");
        RemoteDao.ActiveUser.remove(Const.ActiveUserType.Giver);
        RemoteDao.ActiveUser.remove(Const.ActiveUserType.Taker);
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
