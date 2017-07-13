package com.doyoon.android.bravenewworld.presenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.util.Const;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class UserSelectPreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_select_pre, container, false);

        ImageButton imGiverBtn = (ImageButton) view.findViewById(R.id.imGiverBtn);
        ImageButton imTakerBtn = (ImageButton) view.findViewById(R.id.imTakerBtn);

        /* Listener */
        imGiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSelectMapFragment.USER_TYPE = Const.UserType.Giver;
                goUserSelectMapFragment();
            }
        });

        imTakerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSelectMapFragment.USER_TYPE = Const.UserType.Taker;
                goUserSelectMapFragment();
            }
        });
        return view;
    }

    public void goUserSelectMapFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.user_select_frame_layout, new UserSelectMapFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
