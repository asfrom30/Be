package com.doyoon.android.bravenewworld.view.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LogUtil;
import com.doyoon.android.bravenewworld.util.view.SnackBarHelper;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class SelectPreFragment extends Fragment {

    public static final String TAG = SelectPreFragment.class.getSimpleName();


    public static SelectPreFragment newInstance() {

        Bundle args = new Bundle();

        SelectPreFragment fragment = new SelectPreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /* Activity Life Cycle */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "on Create");
        super.onCreate(savedInstanceState);
    }

    private static final int BTN_UMB_SELECTED = -1;
    private static final int NOT_YET_SELECTED = 0;
    private static final int BTN_RAIN_SELECTED = 1;

    private int selectedBtn = NOT_YET_SELECTED;
    private boolean guideSnackBarFlag;
    private ImageButton imGiverBtn;
    private ImageButton imTakerBtn;
    private TextView imGiverTextView;
    private TextView imTakerTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_user_select_pre, container, false);
        this.dependencyInjection(view);
        this.addButtonListener();

        guideSnackBarFlag = false;
        updateButtonView();

        // todo apply runtime permission
        // setButtonsEnabled(false);
        return view;
    }



    private void setButtonsEnabled(boolean flag) {
        imGiverBtn.setEnabled(flag);
        imTakerBtn.setEnabled(flag);
    }

    private void dependencyInjection(View view) {
        imGiverBtn = (ImageButton) view.findViewById(R.id.imGiverBtn);
        imTakerBtn = (ImageButton) view.findViewById(R.id.imTakerBtn);
        imGiverTextView = (TextView) view.findViewById(R.id.imGiverTextView);
        imTakerTextView = (TextView) view.findViewById(R.id.imTakerTextView);
    }

    private void addButtonListener() {
        // UMB
        imGiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrNotGuideSnackBar();
                if (selectedBtn == BTN_UMB_SELECTED) {
                    AppPresenter.getInstance().runOnMatching(Const.ActiveUserType.Giver);
                } else {
                    selectedBtn = BTN_UMB_SELECTED;
                    updateButtonView();
                }
            }
        });

        // Rain
        imTakerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrNotGuideSnackBar();
                if (selectedBtn == BTN_RAIN_SELECTED) {
                    AppPresenter.getInstance().runOnMatching(Const.ActiveUserType.Taker);
                } else {
                    selectedBtn = BTN_RAIN_SELECTED;
                    updateButtonView();
                }

            }
        });
    }

    private void showOrNotGuideSnackBar() {
        if (!guideSnackBarFlag) {
            guideSnackBarFlag = true;
            SnackBarHelper.show(getActivity(), "한번 더 누르시면 서비스를 시작합니다.", null, null);
        }
    }
//
//    private void goUserSelectMapFragment() {
//        getFragmentManager().beginTransaction().add(R.id.user_select_frame_layout, ActiveUserMapFragment.newInstance(), null).commit();
//    }

    private void updateButtonView() {

        imGiverTextView.setTextColor(getResources().getColor(R.color.white));
        imTakerTextView.setTextColor(getResources().getColor(R.color.white));

        if (selectedBtn == BTN_UMB_SELECTED) {
            Glide.with(getActivity()).load(R.drawable.select_btn_umb_on).into(imGiverBtn);
            Glide.with(getActivity()).load(R.drawable.select_btn_rain_off).into(imTakerBtn);
            /* Animation */
            scaleUp(imGiverBtn, 1.2f);
            scaleUp(imTakerBtn, 0.8f);
            /* color */
            imGiverTextView.setTextColor(getResources().getColor(R.color.custom_slate_grey));

        } else if (selectedBtn == BTN_RAIN_SELECTED) {
            Glide.with(getActivity()).load(R.drawable.select_btn_umb_off).into(imGiverBtn);
            Glide.with(getActivity()).load(R.drawable.select_btn_rain_on).into(imTakerBtn);

            /* Animation */
            scaleUp(imTakerBtn, 1.2f);
            scaleUp(imGiverBtn, 0.8f);

            /* color */
            imTakerTextView.setTextColor(getResources().getColor(R.color.custom_slate_grey));
        }
    }

    private void scaleUp(View view, float scale) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        scaleUpX.setDuration(700);
        scaleUpY.setDuration(700);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(scaleUpX, scaleUpY);

        scaleUp.start();
    }
}
