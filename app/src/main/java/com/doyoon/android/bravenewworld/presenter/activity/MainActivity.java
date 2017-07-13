package com.doyoon.android.bravenewworld.presenter.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.ChatProfile;
import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.domain.reactivenetwork.ReactiveInviteResponse;
import com.doyoon.android.bravenewworld.presenter.activity.interfaces.InviteDialog;
import com.doyoon.android.bravenewworld.presenter.activity.interfaces.ViewPagerMover;
import com.doyoon.android.bravenewworld.presenter.dialog.DialogListener;
import com.doyoon.android.bravenewworld.presenter.dialog.InvitedDialogFragment;
import com.doyoon.android.bravenewworld.presenter.dialog.InvitingDialogFragment;
import com.doyoon.android.bravenewworld.presenter.fragment.UserChatFragment;
import com.doyoon.android.bravenewworld.presenter.fragment.UserMapFragment;
import com.doyoon.android.bravenewworld.presenter.fragment.UserProfileFragment;
import com.doyoon.android.bravenewworld.presenter.fragment.UserSelectFragment;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.ConvString;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements InviteDialog, ViewPagerMover{

    private static String TAG = MainActivity.class.getSimpleName();

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Construct DB Structure */
        FirebaseHelper.buildDbStructure(getBaseContext());

        /* Create Dummy */
        // DummyDao.createDummy();
        // DummyDao.insertDummyPickMeRequest();   // from "ZMWAFD3OLF@google_comma_com" to "miraee05@naver.com"

        /* Log in activity 에서 log in 정보를 받아 온다.... */
        // get Bundle, get Extra...
        String email = "miraee05@naver.com";
        String userKey = ConvString.commaSignToString(email);
        Const.MY_USER_KEY = userKey;


        /* Dependency Injection */
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);

        /* Add All Fragment */
        final List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new UserSelectFragment());
        fragmentList.add(new UserMapFragment());
        fragmentList.add(UserChatFragment.getInstance());
        fragmentList.add(new UserProfileFragment());

        CustomPageAdapter customPageAdapter = new CustomPageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(customPageAdapter);

        /* Tab Layout Listener */
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

    }

    /* I'm giver, 초대를 받았습니다.  */
    @Override
    public void showInvitedDialog(final PickMeRequest pickMeRequest){
        DialogFragment dialog = new InvitedDialogFragment(new DialogListener() {
            @Override
            public void onDialogPositiveClick() {
                ReactiveInviteResponse.hasActiveUser(pickMeRequest.getKey(), new ReactiveInviteResponse.Callback() {
                    @Override
                    public void userExist() {
                        // 아직 있으면 채팅방을 개설하고
                        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.CHAT_ROOM);
                        String chatAccessKey = FirebaseDatabase.getInstance().getReference(modelDir).push().getKey();

                        /* ChatProfile Manual Input for Remember Auto Generate Key */
                        ChatProfile chatProfile = new ChatProfile(chatAccessKey);
                        FirebaseDao.insert(chatProfile, chatAccessKey);
                        // FirebaseDatabase.getInstance().getReference(modelDir + chatAccessKey \).setValue(chatProfile);

                        // 상대방에게 response를 전달합니다.

                        //
                        /* 채팅방을 개설하고 상대방에게 채팅방 키를 전달해줍니다 */
                        /* 초대가 성사되었습니다. */
                        // onMatching을 true 변경하고 Chat으로 이동합니다.
                        UserChatFragment.chatAccessKey = chatAccessKey;
                        UserChatFragment.getInstance().runChatService();
                        moveViewPage(Const.ViewPagerIndex.CHAT);
                    }

                    @Override
                    public void userNotExist() {
                        // todo Another Dialog... 사용자가 다른 사용자와 매칭 되었습니다.
                    }
                });
            }

            @Override
            public void onDialogNegativeClick() {

            }
        });
        dialog.show(getSupportFragmentManager(), "Invited");
    }

    /* 초대를 합니다. */
    @Override
    public void showInvitingDialog(final UserProfile invitingTargetUserProfile, int userType){
        if (invitingTargetUserProfile == null) {
            Log.i(TAG, "called showInvitingDialog But UserProfile parameter is null");
            return;
        }

        // todo dialog fragment는 저절로 사라지나요?
        DialogFragment dialog = new InvitingDialogFragment(userType, new DialogListener() {
            @Override
            public void onDialogPositiveClick() {
                String invitingTargetUserAccessKey = invitingTargetUserProfile.getEmail();
                PickMeRequest pickMeRequest = new PickMeRequest();
                pickMeRequest.setFrom(Const.MY_USER_KEY);

                FirebaseDao.insert(pickMeRequest, invitingTargetUserAccessKey);

                /*
                ChatProfile chatProfile = new ChatProfile();
                chatProfile.setTakerKey(Const.MY_USER_KEY);
                FirebaseDao.insert(chatProfile);
                */
            }

            @Override
            public void onDialogNegativeClick() {
                /* Show Detail Freind Profile... */
            }
        });
        dialog.show(getSupportFragmentManager(), "Inviting");
    }

    @Override
    public void moveViewPage(int targetViewPage) {
        if (viewPager == null) {
            return;
        }
        viewPager.setCurrentItem(targetViewPage);
    }


    class CustomPageAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList;

        public CustomPageAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }




}
