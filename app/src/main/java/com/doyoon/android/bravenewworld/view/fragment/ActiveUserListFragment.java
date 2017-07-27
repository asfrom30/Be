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
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.view.fragment.base.RecyclerFragment;
import com.doyoon.android.bravenewworld.z.util.Const;

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
    private View baseView;

    private ProgressDialog progressDialog;

    private DisplayUserListFragment displayUserListFragment;

    public ActiveUserListFragment(ActiveUserMapFragment activeUserMapFragment, Context context, View baseView) {

        this.presenter = activeUserMapFragment;
        this.context = context;
        this.baseView = baseView;

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

        return displayUserListFragment.getAdpater();
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

    public void onItemClicked(UserProfile userProfile){
        this.presenter.onActiveUserItemClicked(userProfile);
    }

    public void onScrollEnded(){
        AppPresenter.getInstance().fetchNextPageUserProfiles();
    }


    public static class DisplayUserListFragment extends RecyclerFragment<UserProfile> {

        private ActiveUserListFragment parent;
        private List<UserProfile> displayUserList;

        public DisplayUserListFragment() {

        }

        public DisplayUserListFragment(ActiveUserListFragment parent, List<UserProfile> displayUserList) {
            this.parent = parent;
            this.displayUserList = displayUserList;
        }

        public RecyclerView.Adapter getAdpater(){
            return super.adapter;
        }

        @Override
        public CustomViewHolder throwCustomViewHolder(View view) {
            return new CustomViewHolder(view) {

                ImageView imageView;
                TextView textViewIndex, textViewDistance, textViewUserWork, textViewKey, textViewName, textViewAge, textViewGender;

                @Override
                public void updateRecyclerItemView(View view, UserProfile userProfile) {
                    //todo make thumbnail
                    if (userProfile.getImageUri() != null) {
                        Glide.with(getContext()).load(userProfile.getImageUri()).bitmapTransform(new CropCircleTransformation(getContext())).into(imageView);
                    }
                    if(userProfile.getKey() != null) textViewKey.setText(userProfile.getKey());
                    if(userProfile.getName() != null) textViewName.setText(userProfile.getName());
                    if(userProfile.getAge() != 0) textViewAge.setText(userProfile.getAge() + "");

                    if (userProfile.getGender() == Const.Gender.FEMALE) {
                        textViewGender.setText("여자");
                    } else {
                        textViewGender.setText("남자");
                    }

                }

                @Override
                public void dependencyInjection(View itemView, UserProfile userProfile) {
                    imageView = (ImageView) itemView.findViewById(R.id.userprofile_image);
                    textViewKey = (TextView) itemView.findViewById(R.id.userprofile_key);
                    textViewName = (TextView) itemView.findViewById(R.id.userprofile_name);
                    textViewAge = (TextView) itemView.findViewById(R.id.userprofile_age);
                    textViewGender = (TextView) itemView.findViewById(R.id.userprofile_gender);

                    textViewIndex = (TextView) itemView.findViewById(R.id.userprofile_index);
                    textViewDistance = (TextView) itemView.findViewById(R.id.userprofile_distance);
                    textViewUserWork = (TextView) itemView.findViewById(R.id.userprofile_work);
                }

                @Override
                public void onClick(View v) {
                    UserProfile userProfile = getT();
                    parent.onItemClicked(userProfile);
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
        public List<UserProfile> throwDataList() {
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

    /* Data Loading Progress Dialog*/
    public void startInProgress() {
        progressDialog.setMessage("Downloading Music");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);
        progressDialog.show();
        final int totalProgressTime = 100;
        final Thread t = new Thread() {
            @Override
            public void run() {
                int jumpTime = 0;

                while (jumpTime < totalProgressTime) {
                    try {
                        sleep(200);
                        jumpTime += 5;
                        progressDialog.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    public void endInProgress() {
        progressDialog.dismiss();
    }

    /*
    mLatitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
            mLatitudeLabel,
            mLastLocation.getLatitude()));
    mLongitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
            mLongitudeLabel,
            mLastLocation.getLongitude()));
    */

    //
    //    /**
    //     * Shows a {@link Snackbar} using {@code text}.
    //     *
    //     * @param text The Snackbar text.
    //     */
    //    private void showSnackbar(final String text) {
    //        UserProfileView container = findViewById(R.id.main_activity_container);
    //        if (container != null) {
    //            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
    //        }
    //    }
    //
    //    /**
    //     * Shows a {@link Snackbar}.
    //     *
    //     * @param mainTextStringId The id for the string resource for the Snackbar text.
    //     * @param actionStringId   The text of the action item.
    //     * @param listener         The listener associated with the Snackbar action.
    //     */
    //    private void showSnackbar(final int mainTextStringId, final int actionStringId,
    //                              UserProfileView.OnClickListener listener) {
    //        Snackbar.make(findViewById(android.R.id.content),
    //                getString(mainTextStringId),
    //                Snackbar.LENGTH_INDEFINITE)
    //                .setAction(getString(actionStringId), listener).show();
    //    }
    //

}
