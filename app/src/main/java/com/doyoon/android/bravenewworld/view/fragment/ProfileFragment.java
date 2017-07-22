package com.doyoon.android.bravenewworld.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.UserProfilePresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.UserProfileView;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LogUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;
import static com.doyoon.android.bravenewworld.presenter.UserStatusPresenter.myUserProfile;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class ProfileFragment extends Fragment implements UserProfileView {

    public static final String TAG = ProfileFragment.class.getSimpleName();

    private EditText textViewName, textViewWork, textViewAge;
    private Spinner spinnerGender;
    private ImageView imageView;

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

        // UserProfilePresenter.getInstance().setUserProfileView(this);
        // update();

        return view;
    }

    private void dependencyInjection(View view){
        imageView = (ImageView) view.findViewById(R.id.profile_title_imageView);
        textViewName = (EditText) view.findViewById(R.id.profile_name_editText);
//        textViewWork = (EditText) view.findViewById(R.id.profile_work_editText);
//        textViewAge = (EditText) view.findViewById(R.id.profile_age_editText);
//        spinnerGender = (Spinner) view.findViewById(R.id.profile_spinner);
    }

    private void addWidgetsListener(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), Const.ACTIVITY_REQ_CODE.SELECT_PROFILE_IMAGE); // 선택한 이미지를 돌려받기위해서 startActivityForResult를 사용한다.
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Const.ACTIVITY_REQ_CODE.SELECT_PROFILE_IMAGE:
                    Uri imageUri = data.getData();
                    imageView.setImageURI(imageUri);
                    UserProfilePresenter.getInstance().uploadProfileImage(imageUri.toString());
                    break;

            }
        }
    }

    @Override
    public void update() {
        if(myUserProfile == null) return;
        if(myUserProfile.getImageUri() != null) setProfileImage(myUserProfile.getImageUri());
        if(myUserProfile.getName() != null) textViewName.setText(myUserProfile.getName());
        if(myUserProfile.getWork() != null) textViewWork.setText(myUserProfile.getWork());
        if(myUserProfile.getAge() != 0) textViewAge.setText(myUserProfile.getAge());
    }

    @Override
    public void updateProfileImage() {
        if(myUserProfile.getImageUri() != null) setProfileImage(myUserProfile.getImageUri());
    }

    @Override
    public void updateGiverAndTakerCount() {

    }

    private void setProfileImage(String strImageUri){
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
