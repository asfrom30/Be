package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.z.util.LogUtil;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class SelectFragment extends Fragment {

    private static String TAG = SelectFragment.class.getSimpleName();

    public static SelectFragment newInstance() {

        Bundle args = new Bundle();

        SelectFragment fragment = new SelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "on Create");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_user_select, container, false);

        if (UserStatusPresenter.getInstance().isOnFinding()) {
            getFragmentManager().beginTransaction().add(R.id.user_select_frame_layout, ActiveUserFragment.newInstance(), null).commit();
        } else {
            startPreFragment();
        }

        return view;
    }

    private void startPreFragment(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.user_select_frame_layout, SelectPreFragment.newInstance());
        transaction.commit();
    }
}
