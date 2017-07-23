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
import com.doyoon.android.bravenewworld.presenter.LocationPresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.LocationUIController;
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

public class LocationFragment extends Fragment implements OnMapReadyCallback, LocationUIController {

    private static final String TAG = LocationFragment.class.getSimpleName();
    private Button btnStart;
    private Button btnStop;

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
        addWidgetsListener();


        return view;
    }

    private void dependencyInjection(View view, Bundle savedInstanceState){
        mMapView = (MapView) view.findViewById(R.id.mainMapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        btnStart = (Button) view.findViewById(R.id.btnStart);
        btnStop = (Button) view.findViewById(R.id.btnStop);
    }

    private void addWidgetsListener(){

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationPresenter.getInstance().run(getActivity());
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationPresenter.getInstance().stop();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "On Map Ready");

        mGoogleMap = googleMap;
        setDefaultMapSetting();

        if (UserStatusPresenter.getInstance().userStatus == Const.UserStatus.ON_FINDING) {
            AppPresenter.getInstance().setLocationUIController(this);
        } else {
            updateOnlyMyLocation();
        }
    }

    private void updateOnlyMyLocation(){
        /* Code start */
        if (mGoogleMap != null) {
            Log.i(TAG, "mGoogleMap not null");
            if (UserStatusPresenter.userStatus == Const.UserStatus.ON_FINDING) {
                // add marker to map
                // run update and move marker
            } else {
                Log.i(TAG, "Try to get Location in Map Fragment");
                AppPresenter.getInstance().getLastLocation(new AppPresenter.LocationCallback() {
                    @Override
                    public void execute(LatLng latLng) {
                        // Get Lastlocation and Update
                        mGoogleMap.clear();
                        addMarker(latLng, Const.LOCATION_FRAG.DEFAULT_MAP_PIN_RES_ID);
                        moveCamera(mGoogleMap, latLng, currentCameraZoom);
                    }
                });
            }
        } else {
            Log.i(TAG, "mGoogleMap null");
        }
    }

    private void updateMarker(){

        if(mGoogleMap == null) return;
        mGoogleMap.clear();

        if(myLatLng != null) {
            addMarker(myLatLng, Const.LOCATION_FRAG.MY_MAP_PIN_RES_ID);
        }

        if (otherLatLng != null) {
            addMarker(otherLatLng, Const.LOCATION_FRAG.OTHER_MAP_PIN_RES_ID);
        }
    }

    private void addMarker(LatLng latLng, int ResId) {
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .alpha(Const.LOCATION_FRAG.MARKER_ALPHA)
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
//            resetMarker(AppPresenter.getInstance().getActiveUserMap());
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
    public void updateMyMarker(double lat, double lng) {
        myLatLng = new LatLng(lat, lng);
        updateMarker();
    }

    @Override
    public void updateOtherMarker(double lat, double lng) {
        otherLatLng = new LatLng(lat, lng);
        updateMarker();
    }
}
