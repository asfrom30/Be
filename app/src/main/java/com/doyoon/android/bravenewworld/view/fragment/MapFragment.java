package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.presenter.MapPresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.MapUpdater;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LogUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, MapUpdater {

    private static final String TAG = MapFragment.class.getSimpleName();
    private Button btnStart;
    private Button btnStop;

    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static float currentCameraZoom = Const.DEFAULT_CAMERA_ZOOM;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

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


        mMapView = (MapView) view.findViewById(R.id.mainMapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        /* Test  */
        btnStart = (Button) view.findViewById(R.id.btnStart);
        btnStop = (Button) view.findViewById(R.id.btnStop);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPresenter.getInstance().run(getActivity());
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPresenter.getInstance().stop();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Log.i(TAG, "On Map Ready");
//        setDefaultMapSetting(mGoogleMap);
//
//        //noinspection MissingPermission
//        mGoogleMap.setMyLocationEnabled(true);
//        setFocusMyLatlng();
//
//        if (AppPresenter.getInstance().getActiveUserMap() != null) {
//            resetMarker(AppPresenter.getInstance().getActiveUserMap());
//        }

        /* Code start */
        if (mGoogleMap != null) {
            Log.i(TAG, "mGoogleMap not null");
            if (UserStatusPresenter.userStatus == UserStatusPresenter.USER_ON_MATCHED) {
                // add marker to map
                // run update and move marker
            } else {
                Log.i(TAG, "Try to get Location in Map Fragment");
                AppPresenter.getInstance().getLastLocation(new AppPresenter.LocationCallback() {
                    @Override
                    public void execute(LatLng latLng) {
                        // Get Lastlocation and Update
                        mGoogleMap.clear();
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .alpha(Const.MAP_SETTING.MARKER_ALPHA)
                                .icon(BitmapDescriptorFactory.fromResource(getMapPinResId())));
                        moveCamera(mGoogleMap, latLng, currentCameraZoom);
                    }
                });
            }
        } else {
            Log.i(TAG, "mGoogleMap null");
        }


    }

    private void run() {
        MapPresenter.getInstance().run(getActivity());
    }

    private void cancle() {

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

    private int getMapPinResId(){
        int userType = AppPresenter.getInstance().getUserType();

        if (userType == Const.UserType.Taker) {
            return Const.MAP_SETTING.GIVER_MAP_PIN_RES_ID;
        } else if (userType == Const.UserType.Giver) {
            return Const.MAP_SETTING.TAKER_MAP_PIN_RES_ID;
        } else {
            return R.drawable.map_pin_umbrella;
        }
    }

    private void moveCamera(GoogleMap googleMap, LatLng latLng, float cameraZoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, cameraZoom);
        mGoogleMap.moveCamera(cameraUpdate);
    }

}
