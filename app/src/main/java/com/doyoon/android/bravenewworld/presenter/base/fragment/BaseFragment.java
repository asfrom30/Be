package com.doyoon.android.bravenewworld.presenter.base.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class BaseFragment extends Fragment{

    public <T> T getParentActivity(Class<T> tClass) {

        if(getActivity().getClass() == tClass){
            return (T) getActivity();
        } else {
            return null;
        }
    }
}
