package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.presenter.UserProfilePresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.fetch.MyLastLocationFetcher;
import com.doyoon.android.bravenewworld.presenter.interfaces.FindingMapView;
import com.doyoon.android.bravenewworld.presenter.interfaces.OtherUserProfileUpdater;
import com.doyoon.android.bravenewworld.presenter.interfaces.UserProfileView;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.doyoon.android.bravenewworld.z.util.LogUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class LocationFragment extends Fragment implements OnMapReadyCallback, FindingMapView, OtherUserProfileUpdater, UserProfileView {

    private static final String TAG = LocationFragment.class.getSimpleName();
    private ImageView locationFragmentMyImage, locationFragmentOtherImage;

    public static LocationFragment newInstance() {

        Bundle args = new Bundle();

        LocationFragment fragment = new LocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static float currentCameraZoom = Const.DEFAULT_CAMERA_ZOOM;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private LatLng myLatLng;
    private LatLng otherLatLng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LogUtil.logLifeCycle(TAG, "on Create");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "on Create UserProfileView");

        View view = inflater.inflate(R.layout.fragment_user_map, container, false);
        dependencyInjection(view, savedInstanceState);

        AppPresenter.getInstance().setFindingMapView(this);
        AppPresenter.getInstance().addOtherUserProfileUpdater(this);

        UserProfilePresenter.getInstance().addUserProfileView(this);
        updateProfile();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        UserProfilePresenter.getInstance().removeUserProfileView(this);
    }



    private void dependencyInjection(View view, Bundle savedInstanceState) {
        mMapView = (MapView) view.findViewById(R.id.mainMapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        locationFragmentMyImage = (ImageView) view.findViewById(R.id.location_fragment_my_image);
        locationFragmentOtherImage = (ImageView) view.findViewById(R.id.location_fragment_other_image);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "On Map Ready");

        mGoogleMap = googleMap;
        setDefaultMapSetting();

        if (UserStatusPresenter.getInstance().userStatus != Const.UserStatus.ON_FINDING) {
            updateOnlyMyLocation();
        }

    }

    private void updateOnlyMyLocation() {
        /* Code start */
        if (mGoogleMap != null) {
            Log.i(TAG, "mGoogleMap not null");
            if (UserStatusPresenter.userStatus == Const.UserStatus.ON_FINDING) {
                // add marker to map
                // traceAndExecute updateProfile and move marker
            } else {
                MyLastLocationFetcher.getInstance().fetch(getActivity(), new MyLastLocationFetcher.Callback() {
                    @Override
                    public void execute(LatLng latLng) {
                        // Get Lastlocation and Update
                        mGoogleMap.clear();
                        addMarker(latLng, Const.LocationFrag.DEFAULT_MAP_PIN_RES_ID);
                        moveCamera(mGoogleMap, latLng, currentCameraZoom);
                    }
                });
            }
        } else {
            Log.i(TAG, "mGoogleMap null");
        }
    }

    @Override
    public void updateMyMarker(double lat, double lng) {
        if (UserStatusPresenter.getInstance().userStatus != Const.UserStatus.ON_FINDING) return;

        myLatLng = new LatLng(lat, lng);
        updateMarker();
    }

    @Override
    public void updateOtherMarker(double lat, double lng) {
        if (UserStatusPresenter.getInstance().userStatus != Const.UserStatus.ON_FINDING) return;

        otherLatLng = new LatLng(lat, lng);
        updateMarker();
    }

    private void updateMarker() {
        if (mGoogleMap == null) return;
        mGoogleMap.clear();

        if (myLatLng != null) {
            addMarker(myLatLng, Const.LocationFrag.MY_MAP_PIN_RES_ID);
        }

        if (otherLatLng != null) {
            addMarker(otherLatLng, Const.LocationFrag.OTHER_MAP_PIN_RES_ID);
        }
    }

    private void addMarker(LatLng latLng, int ResId) {
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .alpha(Const.LocationFrag.MARKER_ALPHA)
                .icon(BitmapDescriptorFactory.fromResource(ResId)));
    }

    private void setDefaultMapSetting() {
//        setDefaultMapSetting(mGoogleMap);
//
//        //noinspection MissingPermission
//        mGoogleMap.setMyLocationEnabled(true);
//        setFocusMyLatlng();
//
//        if (AppPresenter.getInstance().getActiveUserMap() != null) {
//            addOtherActiveUserMarkers(AppPresenter.getInstance().getActiveUserMap());
//        }
    }


    /* Activity Life Cycler */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

//        if (mRequestingLocationUpdates && checkPermissions()) {
//            startLocationUpdates();
//        } else if (!checkPermissions()) {
//            requestPermissions();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        // stopLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void moveCamera(GoogleMap googleMap, LatLng latLng, float cameraZoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, cameraZoom);
        mGoogleMap.moveCamera(cameraUpdate);
    }


    @Override
    public void otherUserProfileUpdate(UserProfile userProfile) {
        if(userProfile.getImageUri() == null || "".equals(userProfile.getImageUri())) return;

        Glide.with(this).load(userProfile.getImageUri()).bitmapTransform(new CropCircleTransformation(getContext())).into(locationFragmentOtherImage);
    }

    @Override
    public void updateProfile() {
        if(UserStatusPresenter.myUserProfile == null) return;
        if(UserStatusPresenter.myUserProfile.getImageUri() == null || "".equals(UserStatusPresenter.myUserProfile)) return;
        Glide.with(this).load(UserStatusPresenter.myUserProfile.getImageUri()).bitmapTransform(new CropCircleTransformation(getContext())).into(locationFragmentMyImage);
    }

    @Override
    public void updateProfileImage() {

    }

    @Override
    public void updateGiverAndTakerCount() {

    }
}
