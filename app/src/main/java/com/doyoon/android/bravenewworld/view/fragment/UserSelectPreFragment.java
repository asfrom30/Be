package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.util.LogUtil;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class UserSelectPreFragment extends Fragment {

    public static final String TAG = UserSelectPreFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logLifeCycle(TAG, "on Create");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LogUtil.logLifeCycle(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_user_select_pre, container, false);

        final ImageButton imGiverBtn = (ImageButton) view.findViewById(R.id.imGiverBtn);
        final ImageButton imTakerBtn = (ImageButton) view.findViewById(R.id.imTakerBtn);

        /* Listener */
        imGiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UserSelectMapFragment.getInstance().reset(Const.UserType.Giver);
                goUserSelectMapFragment();
                // rerun service

                // go User
            }
        });

        imTakerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UserSelectMapFragment.getInstance().reset(Const.UserType.Taker);
                goUserSelectMapFragment();
            }
        });

        return view;
    }

    public void goUserSelectMapFragment() {
        String fragTag = "UserMap";

        if(getFragmentManager().findFragmentByTag(fragTag) == UserSelectMapFragment.getInstance()){
            getFragmentManager().beginTransaction().remove(UserSelectMapFragment.getInstance()).commit();
        }

        getFragmentManager().beginTransaction().add(R.id.user_select_frame_layout, UserSelectMapFragment.getInstance(), fragTag).commit();

    }

    /* Didn't work */
    /*
    public void goUserSelectMapFragment() {
        String fragTag = "UserMap";
        if(getFragmentManager().findFragmentByTag(fragTag) == UserSelectMapFragment.getInstance()){
            getFragmentManager().beginTransaction().replace(R.id.user_select_frame_layout, UserSelectMapFragment.getInstance()).commit();
        } else {
            getFragmentManager().beginTransaction().add(R.id.user_select_frame_layout, UserSelectMapFragment.getInstance(), fragTag).commit();
        }
    }
    */



    //todo For Motion Listener, Later....
        /*
        imGiverBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imGiverBtn.setImageResource(R.drawable.select_fragment_umb_active);
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        imGiverBtn.setImageResource(R.drawable.select_fragment_umb_deactive);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
        imTakerBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);

                switch(action) {
                    case (MotionEvent.ACTION_DOWN) :
                        Log.i(TAG,"Action was DOWN");
                        return true;
                    case (MotionEvent.ACTION_MOVE) :
                        //Log.d(TAG,"Action was MOVE");
                        return true;
                    case (MotionEvent.ACTION_UP) :
                        Log.i(TAG,"Action was UP");
                        return true;
                    case (MotionEvent.ACTION_CANCEL) :
                        Log.i(TAG,"Action was CANCEL");
                        return true;
                    case (MotionEvent.ACTION_OUTSIDE) :
                        Log.i(TAG,"Movement occurred outside bounds " +
                                "of current screen element");
                        return true;
                    default :
                        return getActivity().onTouchEvent(event);
                }
            }
        });
        */
}
