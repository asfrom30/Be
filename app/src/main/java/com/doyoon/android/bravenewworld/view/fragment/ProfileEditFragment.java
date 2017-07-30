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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.presenter.UserProfilePresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.UserProfileView;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.device.VirtualKeyboard;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;
import static com.doyoon.android.bravenewworld.R.id.edit_profile_btn_complete;
import static com.doyoon.android.bravenewworld.presenter.UserStatusPresenter.myUserProfile;

/**
 * Created by DOYOON on 7/22/2017.
 */

public class ProfileEditFragment extends Fragment implements UserProfileView {

    private EditText textViewName, textViewWork, textViewAge;
    private Spinner spinnerGender;
    private ImageView editProfileImage;
    private TextView editProfileEditTextComment;
    private EditText editProfileEditTextName;
    private EditText editProfileEditTextAge;
    private EditText editProfileEditTextWork;
    private ImageButton editProfileBtnComplete;
    private ImageButton editProfileBtnCamera;
    private ImageButton editProfileBtnBack;
    private ViewGroup viewGroup;
    private RadioButton editProfileRadioMale, editProfileRadioFemale;

    public static ProfileEditFragment newInstance() {

        Bundle args = new Bundle();

        ProfileEditFragment fragment = new ProfileEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_edit, container, false);
        initView(view);
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


    private void onCameraBtn() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), Const.ACTIVITY_REQ_CODE.SELECT_PROFILE_IMAGE); // 선택한 이미지를 돌려받기위해서 startActivityForResult를 사용한다.
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Const.ACTIVITY_REQ_CODE.SELECT_PROFILE_IMAGE:
                    Uri imageUri = data.getData();
                    UserProfilePresenter.getInstance().saveProfileImageToRemote(imageUri.toString());
                    break;
            }
        }
    }

    private void initView(View view) {
        editProfileImage = (ImageView) view.findViewById(R.id.edit_profile_image);
        editProfileEditTextComment = (TextView) view.findViewById(R.id.edit_profile_editText_comment);
        editProfileEditTextName = (EditText) view.findViewById(R.id.edit_profile_editText_name);
        editProfileEditTextAge = (EditText) view.findViewById(R.id.edit_profile_editText_age);
        editProfileEditTextWork = (EditText) view.findViewById(R.id.edit_profile_editText_work);
        editProfileBtnComplete = (ImageButton) view.findViewById(edit_profile_btn_complete);
        editProfileBtnCamera = (ImageButton) view.findViewById(R.id.edit_profile_btn_camera);
        editProfileBtnBack = (ImageButton) view.findViewById(R.id.edit_profile_btn_back);

        editProfileRadioMale = (RadioButton) view.findViewById(R.id.edit_profile_radio_male);
        editProfileRadioFemale = (RadioButton) view.findViewById(R.id.edit_profile_radio_female);

        viewGroup = (ViewGroup) view.findViewById(R.id.edit_profile_view_group);
    }

    private void addWidgetsListener() {
        editProfileBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraBtn();
            }
        });

        editProfileBtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editProfileEditTextName.getText().toString();
                String work = editProfileEditTextWork.getText().toString();
                int age = Integer.parseInt(editProfileEditTextAge.getText().toString());
                String comment = editProfileEditTextComment.getText().toString();

                int gender = Const.Gender.NOT_YET_CHOOSED;
                if(editProfileRadioMale.isChecked()) gender = Const.Gender.MALE;
                else if(editProfileRadioFemale.isChecked()) gender = Const.Gender.FEMALE;

                UserProfilePresenter.getInstance().saveProfileToRemote(name, work, age, comment, gender);

                VirtualKeyboard.hide(getActivity(), editProfileBtnComplete);
            }
        });

        editProfileBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

    }

    @Override
    public void updateProfile() {
        if(myUserProfile == null) return;
        if(myUserProfile.getImageUri() != null) setProfileImage(myUserProfile.getImageUri());

        if(myUserProfile.getName() != null) editProfileEditTextName.setText(myUserProfile.getName());
        if(myUserProfile.getWork() != null) editProfileEditTextWork.setText(myUserProfile.getWork());
        if(myUserProfile.getAge() != 0) editProfileEditTextAge.setText(myUserProfile.getAge()+"");

        if(myUserProfile.getGender() == Const.Gender.MALE) editProfileRadioMale.setChecked(true);
        else if(myUserProfile.getGender() == Const.Gender.FEMALE) editProfileRadioFemale.setChecked(true);
    }

    @Override
    public void updateProfileImage() {

    }

    @Override
    public void updateGiverAndTakerCount() {

    }

    /* private method */
    private void setProfileImage(String strImageUri){
        if (strImageUri == null) return;
        if(editProfileImage == null) return;

        Glide.with(this).load(strImageUri)
                .bitmapTransform(new CenterCrop(getActivity()), new RoundedCornersTransformation(getActivity(), 25, 0, RoundedCornersTransformation.CornerType.TOP))
                .into(editProfileImage);
    }


}
