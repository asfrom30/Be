package com.doyoon.android.bravenewworld;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.ViewPagerMover;
import com.doyoon.android.bravenewworld.util.ConvString;
import com.doyoon.android.bravenewworld.util.LogUtil;
import com.doyoon.android.bravenewworld.util.view.ViewPagerBuilder;
import com.doyoon.android.bravenewworld.view.fragment.ChatFragment;
import com.doyoon.android.bravenewworld.view.fragment.MapFragment;
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

        /* Construct DB Structure */
        FirebaseHelper.buildDbStructure(getBaseContext());

        /* Create Dummy */
        // createDummyData();

        /* Log in activity 에서 log in 정보를 받아 온다.... */
        // Get User ID from bundle get Bundle, get Extra...
        String dummyUserAccessKey = getMyDummyUserAccessKey();

        /* Build AppPresenter */
        buildPresenter(dummyUserAccessKey);

        /* Load Default Data from Remote*/
        UserStatusPresenter.getInstance().loadMyUserProfileFromRemote();

        /* make view pager*/
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);

        ViewPagerBuilder.getInstance(viewPager)
                .addFragment(SelectFragment.newInstance())
                .addFragment(MapFragment.newInstance())
                .addFragment(ChatFragment.newInstance())
                .addFragment(ProfileFragment.newInstance())
                .linkTabLayout(tabLayout)
                .build(getSupportFragmentManager());
    }

    /**
     * 나는 뭐하는 놈입니다~
     */
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

    private String getMyDummyUserAccessKey(){
        String email = getDummyUserEmail();
        String userKey = ConvString.commaSignToString(email);
        return userKey;
    }

    private void buildPresenter(String myUserAccessKey){
        /* UserStatusPresenter */
        UserStatusPresenter.myUserAccessKey = myUserAccessKey;

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
