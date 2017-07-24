package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.UserProfilePresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.UserProfileView;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.doyoon.android.bravenewworld.z.util.LogUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.doyoon.android.bravenewworld.presenter.UserStatusPresenter.myUserProfile;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class ProfileFragment extends Fragment implements UserProfileView {

    public static final String TAG = ProfileFragment.class.getSimpleName();

    private TextView textViewTitle;
    private ImageView imageView;
    private ImageButton btnEditProfile;

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logLifeCycle(TAG, "on Create");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        dependencyInjection(view);
        addWidgetsListener();

        UserProfilePresenter.getInstance().addUserProfileView(this);
        updateProfile();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        UserProfilePresenter.getInstance().removeUserProfileView(this);
    }

    private void dependencyInjection(View view){
        imageView = (ImageView) view.findViewById(R.id.profile_title_imageView);
        textViewTitle = (TextView) view.findViewById(R.id.profile_title_textView);
        btnEditProfile = (ImageButton) view.findViewById(R.id.btnEditProfile);
    }

    private void addWidgetsListener(){
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileFragment();
            }
        });
    }

    private void showEditProfileFragment() {
        Log.i(TAG, "Show Edit Profile Fragment");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom);
        transaction.add(R.id.profile_frame_layout, ProfileEditFragment.newInstance(), null);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void updateProfile() {
        if(myUserProfile == null) return;
        if(myUserProfile.getImageUri() != null) setProfileImage(myUserProfile.getImageUri());

        String title = "";

        if(myUserProfile.getName() != null) title += myUserProfile.getName();
        if(myUserProfile.getWork() != null) title += ", " + myUserProfile.getWork();
        if(myUserProfile.getAge() != 0) title += ", " + myUserProfile.getAge();

        textViewTitle.setText(title);
    }

    @Override
    public void updateProfileImage() {
        if(myUserProfile.getImageUri() != null) setProfileImage(myUserProfile.getImageUri());
    }

    @Override
    public void updateGiverAndTakerCount() {

    }

    private void setProfileImage(String strImageUri){
        if (strImageUri == null) return;
        if(imageView == null) return;
        Glide.with(this).load(strImageUri).bitmapTransform(new CropCircleTransformation(getContext())).into(imageView);
    }


    @Deprecated
    private void updateProfileFromRemote(){
        /* Get User Profile */
        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.USER_PROFILE, UserStatusPresenter.myUserAccessKey) + Const.RefKey.USER_PROFILE;
        FirebaseDatabase.getInstance().getReference(modelDir).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                updateUI(userProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Deprecated
    private void updateUI(UserProfile userProfile){

    }


}
