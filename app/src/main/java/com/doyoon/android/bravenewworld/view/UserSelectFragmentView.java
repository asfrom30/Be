package com.doyoon.android.bravenewworld.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.fragment.UserSelectMapFragment;
import com.doyoon.android.bravenewworld.presenter.fragment.abst.RecyclerFragment;
import com.doyoon.android.bravenewworld.util.Const;

import java.util.List;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class UserSelectFragmentView {

    private static String TAG = UserSelectFragmentView.class.getSimpleName();

    private Context context;
    private UserSelectMapFragment presenter;
    private View baseView;

    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private Button tempButton;

    private ProgressDialog progressDialog;

    private RecyclerFragment displayUserListFragment;

    public UserSelectFragmentView(UserSelectMapFragment userSelectMapFragment, Context context, View baseView) {

        int layoutID = R.layout.fragment_user_select_map;

        this.presenter = userSelectMapFragment;
        this.context = context;
        this.baseView = baseView;

        this.setWidgetsPropFromResources();
        this.dependencyInjection();
        this.addWidgetsListener();

        /* Publish User List Fragment */
        // FrameLayout frameLayout = (FrameLayout) this.baseView.findViewById(R.id.user_map_frame_layout);
        FragmentTransaction transaction = this.presenter.getFragmentManager().beginTransaction();
        transaction.add(R.id.user_map_frame_layout, displayUserListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void notifyDataListChanged(){
        displayUserListFragment.notifySetChanged();
    }

    private void setWidgetsPropFromResources() {
        Resources resources = context.getResources();

        mLatitudeLabel = resources.getString(R.string.latitude_label);
        mLongitudeLabel = resources.getString(R.string.longitude_label);
    }

    private void dependencyInjection() {
        /* Relate to Location */
        mLatitudeText = (TextView) baseView.findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) baseView.findViewById((R.id.longitude_text));

        tempButton = (Button) baseView.findViewById(R.id.getPosition_btn);

        displayUserListFragment = new DisplayUserListFragment(this, this.presenter.getDataList());

        progressDialog = new ProgressDialog(this.context);
    }

    private void addWidgetsListener() {
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTriggeredUpdateUIThread();
            }
        });
    }

    public void onItemClicked(UserProfile userProfile){
        this.presenter.onItemClicked(userProfile);
    }

    public static class DisplayUserListFragment extends RecyclerFragment<UserProfile> {

        private UserSelectFragmentView parent;
        private List<UserProfile> displayUserList;

        public DisplayUserListFragment() {

        }

        public DisplayUserListFragment(UserSelectFragmentView parent, List<UserProfile> displayUserList) {
            this.parent = parent;
            this.displayUserList = displayUserList;
        }

        @Override
        public CustomViewHolder throwCustomViewHolder(View view) {
            return new CustomViewHolder(view) {

                ImageView imageView;
                TextView textViewIndex, textViewDistance, textViewUserType, textViewKey, textViewName, textViewAge, textViewGender;

                @Override
                public void updateRecyclerItemView(View view, UserProfile userProfile) {
                    textViewKey.setText(userProfile.getKey());
                    textViewName.setText(userProfile.getName());
                    textViewAge.setText(userProfile.getAge() + "");
                    if (userProfile.getGender() == Const.Gender.FEMALE) {
                        textViewGender.setText("남자");
                    } else {
                        textViewGender.setText("여자");
                    }

                }

                @Override
                public void dependencyInjection(View itemView, UserProfile userProfile) {
                    textViewKey = (TextView) itemView.findViewById(R.id.userprofile_key);
                    textViewName = (TextView) itemView.findViewById(R.id.userprofile_name);
                    textViewAge = (TextView) itemView.findViewById(R.id.userprofile_age);
                    textViewGender = (TextView) itemView.findViewById(R.id.userprofile_gender);

                    textViewIndex = (TextView) itemView.findViewById(R.id.userprofile_index);
                    textViewDistance = (TextView) itemView.findViewById(R.id.userprofile_distance);
                    textViewUserType = (TextView) itemView.findViewById(R.id.userprofile_type);
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
    //        View container = findViewById(R.id.main_activity_container);
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
    //                              View.OnClickListener listener) {
    //        Snackbar.make(findViewById(android.R.id.content),
    //                getString(mainTextStringId),
    //                Snackbar.LENGTH_INDEFINITE)
    //                .setAction(getString(actionStringId), listener).show();
    //    }
    //

}
