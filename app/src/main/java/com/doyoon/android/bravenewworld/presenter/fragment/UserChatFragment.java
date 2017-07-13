package com.doyoon.android.bravenewworld.presenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.doyoon.android.bravenewworld.presenter.fragment.abst.RecyclerFragment;

import java.util.List;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class UserChatFragment extends RecyclerFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public CustomViewHolder throwCustomViewHolder(View view) {
        return null;
    }

    @Override
    public int throwFragmentLayoutResId() {
        return 0;
    }

    @Override
    public int throwRecyclerViewResId() {
        return 0;
    }

    @Override
    public List throwDataList() {
        return null;
    }

    @Override
    public int throwItemLayoutId() {
        return 0;
    }
}
