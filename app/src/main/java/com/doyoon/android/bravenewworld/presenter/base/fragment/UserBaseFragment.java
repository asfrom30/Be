package com.doyoon.android.bravenewworld.presenter.base.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.doyoon.android.bravenewworld.presenter.interfaces.ViewPagerMover;

/**
 * Created by DOYOON on 7/15/2017.
 */

public abstract class UserBaseFragment extends Fragment {

    protected ViewPagerMover viewPagerMover;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewPagerMover = (ViewPagerMover) context;
    }
}
