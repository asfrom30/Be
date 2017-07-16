package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.doyoon.android.bravenewworld.util.LogUtil;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class UserMapFragment extends Fragment {

    private static final String TAG = UserMapFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logLifeCycle(TAG, "on Create");
    }


}
