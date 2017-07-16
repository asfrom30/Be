package com.doyoon.android.bravenewworld.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseUploader;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.DateUtil;
import com.doyoon.android.bravenewworld.util.LogUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;
import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class UserProfileFragment extends Fragment {

    public static final String TAG = UserProfileFragment.class.getSimpleName();

    public static UserProfileFragment instance;

    public static UserProfileFragment getInstance() {
        if (instance == null) {
            instance = new UserProfileFragment();
        }
        return instance;
    }

    private UserProfileFragment() {

    }

    private EditText textViewName, textViewWork, textViewAge;
    private Spinner spinnerGender;
    private ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logLifeCycle(TAG, "on Create");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        /* Dependency Injection */
        imageView = (ImageView) view.findViewById(R.id.profile_title_imageView);
        textViewName = (EditText) view.findViewById(R.id.profile_name_editText);
        textViewWork = (EditText) view.findViewById(R.id.profile_work_editText);
        textViewAge = (EditText) view.findViewById(R.id.profile_age_editText);
        spinnerGender = (Spinner) view.findViewById(R.id.profile_spinner);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), Const.ACTIVITY_REQ_CODE.GALLERY); // 선택한 이미지를 돌려받기위해서 startActivityForResult를 사용한다.
            }
        });

        updateProfileFromRemote();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Const.ACTIVITY_REQ_CODE.GALLERY:
                    Uri imageUri = data.getData();
                    imageView.setImageURI(imageUri);
                    uploadProfileImage(imageUri.toString());
                    setProfileImage(imageUri.toString());
                    break;

            }
        }
    }

    private void updateProfileFromRemote(){
        /* Get User Profile */
        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.USER_PROFILE, Const.MY_USER_KEY) + Const.RefKey.USER_PROFILE;
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

    private void updateUI(UserProfile userProfile){
        textViewName.setText(userProfile.getName());
        textViewWork.setText(userProfile.getWork());
        textViewAge.setText(userProfile.getAge() + "");
        setProfileImage(userProfile.getImageUri());
    }

    private void setProfileImage(String imageUriStr){
        Glide.with(this).load(imageUriStr).bitmapTransform(new CropCircleTransformation(getContext())).into(imageView);
    }

    private void uploadProfileImage(String strFileUri){
        // todo random key...
        String fileName = DateUtil.getCurrentDate() + "_" + Const.MY_USER_KEY;// 시간값 + UUID 추가해서 만듦...
        FirebaseUploader.execute(strFileUri, Const.StorageRefKey.USER_PROFILE, fileName, new FirebaseUploader.Callback() {
            @Override
            public void postExecute(Uri uploadedFileUri) {
                String modelDir = getModelDir(Const.RefKey.USER_PROFILE, Const.MY_USER_KEY)
                        + Const.RefKey.USER_PROFILE + "/" + Const.RefKey.USER_PROFILE_IMAGE_URI;
                FirebaseDatabase.getInstance().getReference(modelDir).setValue(uploadedFileUri.toString());
                Log.i(TAG, "정상적으로 추가되었습니다." + uploadedFileUri);
            }
        });
    }
}
