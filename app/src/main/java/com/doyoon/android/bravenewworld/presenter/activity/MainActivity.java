package com.doyoon.android.bravenewworld.presenter.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.view.fragment.UserChatFragment;
import com.doyoon.android.bravenewworld.view.fragment.UserMapFragment;
import com.doyoon.android.bravenewworld.view.fragment.UserProfileFragment;
import com.doyoon.android.bravenewworld.view.fragment.UserSelectFragment;
import com.doyoon.android.bravenewworld.presenter.interfaces.ViewPagerMover;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.ConvString;
import com.doyoon.android.bravenewworld.util.LogUtil;
import com.doyoon.android.bravenewworld.util.view.ViewPagerBuilder;

public class MainActivity extends AppCompatActivity implements ViewPagerMover{

    private static String TAG = MainActivity.class.getSimpleName();

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "on Create");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Construct DB Structure */
        FirebaseHelper.buildDbStructure(getBaseContext());

        /* Create Dummy */
        // createDummyData();

        /* Log in activity 에서 log in 정보를 받아 온다.... */
        // Get User ID from bundle get Bundle, get Extra...
        this.setMyUserKeyAndProfile();

        /* make view pager*/
        if (viewPager == null) {
            viewPager = (ViewPager) findViewById(R.id.main_view_pager);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);

            ViewPagerBuilder.getInstance(viewPager)
                    .addFragment(new UserSelectFragment())
                    .addFragment(new UserMapFragment())
                    .addFragment(UserChatFragment.getInstance())
                    .addFragment(UserProfileFragment.getInstance())
                    .linkTabLayout(tabLayout)
                    .build(getSupportFragmentManager());
        }



    }

    private void setMyUserKeyAndProfile() {
        // String email = getIntent().getStringExtra()
        String email = getDummyUserEmail();
        String userKey = ConvString.commaSignToString(email);
        Const.MY_USER_KEY = userKey;

        FirebaseDao.read(UserProfile.class, new FirebaseDao.ReadCallback<UserProfile>() {
            @Override
            public void execute(UserProfile userProfile) {
                Const.MY_USER_PROFILE = userProfile;
                Log.i(TAG, "My User Key and User Profile is set Complete");
            }
        }, Const.MY_USER_KEY);
    }

    private String getDummyUserEmail(){
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        String email;
        if (currentApiVersion == 25) {
            email = "miraee05@naver.com";
        } else if (currentApiVersion == 23) {
            email = "YDJQ74F025@google.com";
        } else {
            email = "miraee05@naver.com";
        }
        return email;
    }

    @Override
    public void moveViewPage(int targetViewPage) {
        if (viewPager == null) {
            return;
        }
        viewPager.setCurrentItem(targetViewPage);
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}
