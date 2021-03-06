package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.ActiveUserFragmentPublisher;
import com.doyoon.android.bravenewworld.presenter.permission.PermissionPresenter;
import com.doyoon.android.bravenewworld.util.LogUtil;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class SelectFragment extends Fragment implements ActiveUserFragmentPublisher {

    private static String TAG = SelectFragment.class.getSimpleName();
    private PermissionPresenter permissionPresenter;

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

        AppPresenter.getInstance().setActiveUserFragmentPublisher(this);

        View view = inflater.inflate(R.layout.fragment_user_select, container, false);

        if (UserStatusPresenter.getInstance().isOnMatching()) {
            startFragment(ActiveUserMapFragment.newInstance());
        } else {
            startFragment(SelectPreFragment.newInstance());
        }

        return view;
    }

    private void startFragment(Fragment fragment){
        // getFragmentManager().beginTransaction().add(R.id.user_select_frame_layout, ActiveUserMapFragment.newInstance(), null).commit();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.user_select_frame_layout, fragment);
        transaction.commit();
    }

    @Override
    public void publish() {
        startFragment(ActiveUserMapFragment.newInstance());
    }



}
