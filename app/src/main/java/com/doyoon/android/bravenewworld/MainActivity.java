package com.doyoon.android.bravenewworld;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.Presenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.ViewPagerMover;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.ConvString;
import com.doyoon.android.bravenewworld.util.LogUtil;
import com.doyoon.android.bravenewworld.util.view.ViewPagerBuilder;
import com.doyoon.android.bravenewworld.view.fragment.ChatFragment;
import com.doyoon.android.bravenewworld.view.fragment.MapFragment;
import com.doyoon.android.bravenewworld.view.fragment.ProfileFragment;
import com.doyoon.android.bravenewworld.view.fragment.TestChatFragment;

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

        /* Construct DB Structure */
        FirebaseHelper.buildDbStructure(getBaseContext());

        /* Create Dummy */
        // createDummyData();

        /* Log in activity 에서 log in 정보를 받아 온다.... */
        // Get User ID from bundle get Bundle, get Extra...
        this.setMyUserKeyAndProfile();

        /* Build Presenter */
        // todo what difference between baseContext and applicationContext
        Presenter.getInstance().initialize(this);
        Presenter.getInstance().setViewPagerMover(this);

        /* make view pager*/
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);

        ViewPagerBuilder.getInstance(viewPager)
                .addFragment(TestChatFragment.newInstance())
                // .addFragment(SelectFragment.newInstance())
                .addFragment(MapFragment.newInstance())
                .addFragment(ChatFragment.newInstance())
                .addFragment(ProfileFragment.newInstance())
                .linkTabLayout(tabLayout)
                .build(getSupportFragmentManager());
    }

    /**
     * 나는 뭐하는 놈입니다~
     */
    private void setMyUserKeyAndProfile() {
        // String email = getIntent().getStringExtra()
        String email = getDummyUserEmail();
        String userKey = ConvString.commaSignToString(email);
        Const.MY_USER_KEY = userKey;

        FirebaseDao.read(UserProfile.class, new FirebaseDao.ReadCallback<UserProfile>() {
            @Override
            public void execute(UserProfile userProfile) {
                Const.MY_USER_PROFILE = userProfile;
                Log.i(TAG, "My User Key and User Profile is set Complete" + Const.MY_USER_PROFILE.toString());
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

    /*
    @Override
    protected void attachBaseContext(Context newBase) {
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "Wedding Chardonnay.ttf"))
                .addBold(Typekit.createFromAsset(this, "Double_Bubble_shadow.otf"))
                .addItalic(Typekit.createFromAsset(this, "Double_Bubble_shadow_italic.otf"))
                .addBoldItalic(Typekit.createFromAsset(this, "Double_Bubble_shadow_italic.otf"))
                .addCustom1(Typekit.createFromAsset(this, "soopafre.ttf"))
                .addCustom2(Typekit.createFromAsset(this, "Break It Down.ttf"));

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
    */


    @Override
    public void moveViewPage(int targetViewPage) {
        if (viewPager == null) {
            return;
        }
        viewPager.setCurrentItem(targetViewPage);
    }
}