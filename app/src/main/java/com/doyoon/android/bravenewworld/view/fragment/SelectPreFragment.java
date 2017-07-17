package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.presenter.Presenter;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LogUtil;

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
        super.onCreate(savedInstanceState);
        LogUtil.logLifeCycle(TAG, "on Create");
    }

    private ImageButton imGiverBtn;
    private ImageButton imTakerBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LogUtil.logLifeCycle(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_user_select_pre, container, false);
        this.dependencyInjection(view);
        this.addButtonListener();

        return view;
    }

    private void dependencyInjection(View view){
        imGiverBtn = (ImageButton) view.findViewById(R.id.imGiverBtn);
        imTakerBtn = (ImageButton) view.findViewById(R.id.imTakerBtn);
    }

    private void addButtonListener(){
        imGiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Presenter.getInstance().runOnFinding(Const.UserType.Giver);
                goUserSelectMapFragment();
            }
        });

        imTakerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Presenter.getInstance().runOnFinding(Const.UserType.Taker);
                goUserSelectMapFragment();
            }
        });
    }

    private void goUserSelectMapFragment() {
        getFragmentManager().beginTransaction().add(R.id.user_select_frame_layout, ActiveUserFragment.newInstance(), null).commit();
    }


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
