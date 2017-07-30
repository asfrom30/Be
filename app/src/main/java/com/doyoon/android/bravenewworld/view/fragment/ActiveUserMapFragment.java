package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.geovalue.ActiveUser;
import com.doyoon.android.bravenewworld.domain.local.ActiveUserProfile;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.fetch.MyLastLocationFetcher;
import com.doyoon.android.bravenewworld.presenter.interfaces.ActiveUserListView;
import com.doyoon.android.bravenewworld.presenter.interfaces.ActiveUserMapView;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LogUtil;
import com.doyoon.android.bravenewworld.view.dialog.SendProfileDialog;
import com.doyoon.android.bravenewworld.view.fragment.base.UserBaseFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;

/**
 * 설명
 */

public class ActiveUserMapFragment extends UserBaseFragment implements OnMapReadyCallback, ActiveUserMapView, ActiveUserListView {

    private static String TAG = ActiveUserMapFragment.class.getSimpleName();
    private static int linkRes = R.layout.fragment_user_select_map;

    /* UserProfileView */
    private ActiveUserListFragment mActiveUserListView;
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    // private List<UserProfile> displayUserList = new ArrayList();

    /* Shared Preference */
    private double searchDistanceKm = 100;
    private float CURRENT_CAMERA_ZOOM = Const.DEFAULT_CAMERA_ZOOM;

    public static ActiveUserMapFragment newInstance() {

        Bundle args = new Bundle();

        ActiveUserMapFragment fragment = new ActiveUserMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View baseView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logLifeCycle(TAG, "on Create");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "onCreateView()");

        /* Link to AppPresenter */
        AppPresenter.getInstance().setActiveUserListView(this);
        AppPresenter.getInstance().setActiveUserMapView(this);

        /* Layout Inflating */
        baseView = inflater.inflate(R.layout.fragment_user_select_map, container, false);
        if (this.mActiveUserListView == null) {
            this.mActiveUserListView = new ActiveUserListFragment(this, getContext(), baseView);
        }

        /* Get Default Setting  */
        searchDistanceKm = Const.DEFAULT_SEARCH_DISTANCE_KM;

        /* Map UserProfileView Dependency */
        // Gets the MapView from the XML layout and creates it
        mMapView = (MapView) baseView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMapView.getMapAsync(this);


        //todo... not working
//        if (AppPresenter.getInstance().getActiveUserProfileList().size() != 0) {
//            notifyListDataSetChanged();
//        }

        return baseView;
    }

//    public void onActiveUserItemClicked(UserProfile clickedUserProfile) {
//        int userType = AppPresenter.getInstance().getUserType();
//        DialogFragment dialogFragment = new SendPickmeRequestDialog( userType
//                , clickedUserProfile
//                , new SendPickmeRequestDialog.Callback() {
//                    /* Show Detail Profile */
//
//                    /* Sending */
//        });
//
//        dialogFragment.showWithPreImageLoad(getFragmentManager(), null);
//    }

    public void onActiveUserItemClicked(ActiveUserProfile clickedAcitveUserProfile) {
        SendProfileDialog sendProfileDialog = new SendProfileDialog(clickedAcitveUserProfile, UserStatusPresenter.activeUserType);
        sendProfileDialog.show(getFragmentManager(), null);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        setDefaultMapSetting(mGoogleMap);

        //noinspection MissingPermission
        //mGoogleMap.setMyLocationEnabled(true);
        LatLng myLastLatLng = UserStatusPresenter.myLatLng;

        if(myLastLatLng != null){
            clearAllMarkers();
            addMyLocationMarker(myLastLatLng);
        }

        if (AppPresenter.getInstance().getActiveUserMap() != null) {
            addOtherActiveUserMarkers(AppPresenter.getInstance().getActiveUserMap());
        }
    }


    @Deprecated // Moved to Presenter
    private void addMarkerAndSetFocusMyLastLatlng(){
        if (mGoogleMap == null) {
            Log.i(TAG, "mGoogleMap is null, Can't focus Last Lat Lng");
            return;
        }

        MyLastLocationFetcher.getInstance().fetch(getActivity(), new MyLastLocationFetcher.Callback() {
            @Override
            public void execute(LatLng latLng) {
                mGoogleMap.clear();
                addMarker(latLng);
                moveCamera(latLng);
                Log.i(TAG, "Set Focus at my last location" + latLng.toString());
            }
        });
    }

    private void setDefaultMapSetting(GoogleMap googleMap){
        // googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

    }

    private void addMarker(LatLng latLng) {
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .alpha(Const.LocationFrag.MARKER_ALPHA)
                .icon(BitmapDescriptorFactory.fromResource(Const.ActiveUserMapFrag.DEFAULT_MAP_PIN_RES_ID)));
    }

    private void moveCamera(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, CURRENT_CAMERA_ZOOM);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    /* Activity Life Cycler */
    @Override
    public void onResume() {
        LogUtil.logLifeCycle(TAG, "on Resume");
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        LogUtil.logLifeCycle(TAG, "onPause");
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        LogUtil.logLifeCycle(TAG, "onStop");
        super.onStop();
    }



    @Override
    public void onDestroy() {
        LogUtil.logLifeCycle(TAG, "onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
        LogUtil.logLifeCycle(TAG, "onSaveInstanceState");
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /* Getter and Setter */
    public List<ActiveUserProfile> getDataList() {
        return AppPresenter.getInstance().getActiveUserProfileList();
        //return this.displayUserList;
    }

    /* Interface for presenter */
    @Override
    public void notifyListDataSetChanged() {
        if(mActiveUserListView.getAdapter() == null){
            Log.e(TAG, "mActiveUserListView adapter is null, can not notify data set changed");
        }

        mActiveUserListView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void notifyListDataRemoved(int position) {
        mActiveUserListView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyListDataAdded(int position) {
        mActiveUserListView.getAdapter().notifyItemInserted(position);
    }


    @Override
    public void clearAllMarkers() {
        if (this.mGoogleMap == null) return;
        mGoogleMap.clear();
    }

    @Override
    public void addMyLocationMarker(LatLng latLng) {
        if (this.mGoogleMap == null) return;

        addMarker(latLng);
        moveCamera(latLng);
    }

    @Override
    public void addOtherActiveUserMarkers(Map<String, ActiveUser> activeUserMap){
        if (this.mGoogleMap == null) return;

        for (Map.Entry<String, ActiveUser> entry : activeUserMap.entrySet()) {
            ActiveUser activeUser = entry.getValue();

            int markerResId = Const.ActiveUserMapFrag.getMapPinResId(UserStatusPresenter.activeUserType);

            this.mGoogleMap.addMarker(new MarkerOptions()
                    .position(activeUser.getLatLng())
                    .alpha(Const.ActiveUserMapFrag.MARKER_ALPHA)
                    .icon(BitmapDescriptorFactory.fromResource(markerResId)));

            Log.e(TAG, "ADD Marker" + activeUser.getKey());
        }
    }
}





