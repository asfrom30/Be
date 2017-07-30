package com.doyoon.android.bravenewworld.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.local.ActiveUserProfile;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.util.ConvString;
import com.doyoon.android.bravenewworld.view.fragment.base.RecyclerFragment;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class ActiveUserListFragment {

    private static String TAG = ActiveUserListFragment.class.getSimpleName();
    int linkRes = R.layout.fragment_user_select_map;

    private Context context;
    private ActiveUserMapFragment presenter;

    private ProgressDialog progressDialog;

    private DisplayUserListFragment displayUserListFragment;

    public ActiveUserListFragment(ActiveUserMapFragment activeUserMapFragment, Context context, View baseView) {

        this.presenter = activeUserMapFragment;
        this.context = context;

        this.setWidgetsPropFromResources();
        this.dependencyInjection();
        this.addWidgetsListener();

        /* Publish User List Fragment */
        // FrameLayout frameLayout = (FrameLayout) this.baseView.findViewById(R.id.user_map_frame_layout);
        FragmentTransaction transaction = this.presenter.getFragmentManager().beginTransaction();
        transaction.add(R.id.user_map_frame_layout, displayUserListFragment);
        transaction.commit();

    }

    public RecyclerView.Adapter getAdapter(){
        if(displayUserListFragment == null) {
            Log.i(TAG, "displayUserListFragment is null, can not return Recycler Adpater");
        }

        return displayUserListFragment.getAdapter();
    }


    private void setWidgetsPropFromResources() {
        Resources resources = context.getResources();
        //mLatitudeLabel = resources.getString(R.string.latitude_label);
        //mLongitudeLabel = resources.getString(R.string.longitude_label);
    }

    private void dependencyInjection() {
        /* Relate to Location */
        displayUserListFragment = new DisplayUserListFragment(this, this.presenter.getDataList());
        progressDialog = new ProgressDialog(this.context);
    }

    private void addWidgetsListener() {
    }

    public void onItemClicked(ActiveUserProfile activeUserProfile){
        this.presenter.onActiveUserItemClicked(activeUserProfile);
    }

    public void onScrollEnded(){
        AppPresenter.getInstance().fetchNextPageUserProfiles();
    }


    public static class DisplayUserListFragment extends RecyclerFragment<ActiveUserProfile> {

        private ActiveUserListFragment parent;
        private List<ActiveUserProfile> displayUserList;

        public DisplayUserListFragment() {

        }

        public DisplayUserListFragment(ActiveUserListFragment parent, List<ActiveUserProfile> displayUserList) {
            this.parent = parent;
            this.displayUserList = displayUserList;
        }

        public RecyclerView.Adapter getAdapter(){
            return super.adapter;
        }

        @Override
        public CustomViewHolder throwCustomViewHolder(View view) {
            return new CustomViewHolder(view) {

                ImageView imageView;
                TextView textViewIndex, textViewDistance, textViewUserWork, textViewName, textViewAge, textViewGender;

                @Override
                public void updateRecyclerItemView(View view, ActiveUserProfile activeUserProfile) {
                    //todo make thumbnail
                    if (activeUserProfile.getImageUri() != null) {
                        Glide.with(getContext()).load(activeUserProfile.getImageUri()).bitmapTransform(new CropCircleTransformation(getContext())).into(imageView);
                    }
                    if(activeUserProfile.getName() != null) textViewName.setText(activeUserProfile.getName());
                    if(activeUserProfile.getAge() != 0) textViewAge.setText(activeUserProfile.getAge() + "");
                    if(activeUserProfile.getGender() != 0) textViewGender.setText(ConvString.getGender(activeUserProfile.getGender()));

                    String distance = ConvString.getDistance(activeUserProfile.getDistanceFromUser(), getString(R.string.fragment_active_distance_unit));
                    if(activeUserProfile.getDistanceFromUser() != 0) textViewDistance.setText(distance);


                }

                @Override
                public void dependencyInjection(View itemView, ActiveUserProfile activeUserProfile) {
                    imageView = (ImageView) itemView.findViewById(R.id.userprofile_image);
                    textViewName = (TextView) itemView.findViewById(R.id.userprofile_name);
                    textViewAge = (TextView) itemView.findViewById(R.id.userprofile_age);
                    textViewGender = (TextView) itemView.findViewById(R.id.userprofile_gender);

                    textViewIndex = (TextView) itemView.findViewById(R.id.userprofile_index);
                    textViewDistance = (TextView) itemView.findViewById(R.id.userprofile_distance);
                    textViewUserWork = (TextView) itemView.findViewById(R.id.userprofile_work);
                }

                @Override
                public void onClick(View v) {
                    ActiveUserProfile activeUserProfile = getT();
                    parent.onItemClicked(activeUserProfile);
                }
            };
        }

        @Override
        public int throwFragmentLayoutResId() {
            return R.layout.inner_fragment_active_user_list;
        }

        @Override
        public int throwRecyclerViewResId() {
            return R.id.inner_recycler_view;
        }

        @Override
        public List<ActiveUserProfile> throwDataList() {
            return this.displayUserList;
        }

        @Override
        public int throwItemLayoutId() {
            return R.layout.item_active_user_list;
        }

        @Override
        public void scrollEndCallback() {
            this.parent.onScrollEnded();
        }
    }
}
