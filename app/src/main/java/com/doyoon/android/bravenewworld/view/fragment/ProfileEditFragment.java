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
import android.widget.Spinner;

import com.doyoon.android.bravenewworld.presenter.UserProfilePresenter;
import com.doyoon.android.bravenewworld.util.Const;

import static android.app.Activity.RESULT_OK;

/**
 * Created by DOYOON on 7/22/2017.
 */

public class ProfileEditFragment extends Fragment {


    private EditText textViewName, textViewWork, textViewAge;
    private Spinner spinnerGender;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


//        textViewWork = (EditText) view.findViewById(R.id.profile_work_editText);
//        textViewAge = (EditText) view.findViewById(R.id.profile_age_editText);
//        spinnerGender = (Spinner) view.findViewById(R.id.profile_spinner);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private void onImageClicked(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), Const.ACTIVITY_REQ_CODE.SELECT_PROFILE_IMAGE); // 선택한 이미지를 돌려받기위해서 startActivityForResult를 사용한다.
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Const.ACTIVITY_REQ_CODE.SELECT_PROFILE_IMAGE:
                    Uri imageUri = data.getData();
//                    imageView.setImageURI(imageUri);
                    UserProfilePresenter.getInstance().uploadProfileImage(imageUri.toString());
                    break;

            }
        }
    }

}
