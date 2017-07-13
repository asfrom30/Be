package com.doyoon.android.bravenewworld.presenter.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.geovalue.ActiveUser;
import com.doyoon.android.bravenewworld.domain.firebase.value.Invite;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.activity.interfaces.InviteDialog;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LatLngUtil;
import com.doyoon.android.bravenewworld.view.UserSelectFragmentView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;
import static com.doyoon.android.bravenewworld.util.Const.PAGING_NUMBER_AT_ONCE;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class UserSelectMapFragment extends Fragment implements OnMapReadyCallback {

    private static String TAG = UserSelectMapFragment.class.getSimpleName();

    /* View */
    private UserSelectFragmentView mMainView;
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private FusedLocationProviderClient mFusedLocationClient;

    private List<String> activeUserList = new ArrayList<>();
    private Map<String, ActiveUser> activeUserMap = new HashMap<>();
    private List<UserProfile> displayUserList = new ArrayList();

    /* Flag */
    boolean needListUiUpdate = false;

    /* Static Preference */
    public static int USER_TYPE = Const.UserType.Taker;

    /* Shared Preference */
    private boolean onMatching = false;
    private int page = 1;
    private LatLng lastLatLng;
    private double SEARCH_DISTANCE_KM = 100;
    private float CURRENT_CAMERA_ZOOM = Const.DEFAULT_CAMERA_ZOOM;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /* Layout Inflating */
        View baseView = inflater.inflate(R.layout.fragment_user_select_map, container, false);
        this.mMainView = new UserSelectFragmentView(this, getContext(), baseView);

        /* Get Default Setting  */
        SEARCH_DISTANCE_KM = Const.DEFAULT_SEARCH_DISTANCE_KM;

        /* Bundle */
        updateValuesFromBundle(savedInstanceState);

        /* Map View Dependency */
        // Gets the MapView from the XML layout and creates it
        mMapView = (MapView) baseView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMapView.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        this.toGetLocationPermission();

        return baseView;
    }

    public void runService(){

        if(USER_TYPE == Const.UserType.Giver) {
            this.addInviteListener();
        }

        if (!onMatching) {
            // todo insertMyUser to giver or taker...
        }

        //thisView.startInProgress();
        updateLastLatLng(new PostUpdateLatLng() {
            @Override
            public void callback() {
                /* After Update LatLng focus to LatLng*/
                setFocusLastLatLng();

                /* Add Geo Query Data using Last LatLng */
                activeUserMap.clear();
                activeUserList.clear();
                toEnableActiveUserListListener(USER_TYPE, lastLatLng, SEARCH_DISTANCE_KM);
            }
        });
    }

    /* This is On Btn */
    public void onTriggeredUpdateUIThread(){
        Log.i(TAG, "Try to Update UI");

        //todo move to domain...

        int endIndex = page * PAGING_NUMBER_AT_ONCE;
        int startIndex = endIndex - PAGING_NUMBER_AT_ONCE;

        if (endIndex > activeUserList.size()) {
            endIndex = activeUserList.size();
            // todo 페이지의 끝 입니다..... reactive 하게.. 어떻게??
        }
        Log.i(TAG, startIndex + "부터 " + endIndex + "까지 데이터를 가져오려고 시도 합니다. ");
        for (int i = startIndex; i < endIndex; i++) {
            Log.i(TAG, i + "번째 데이터를 가져오려고 시도 합니다. ");
            String userAccessKey = activeUserList.get(i);
            String modelDir = FirebaseHelper.getModelDir("userprofile", userAccessKey);

            FirebaseDatabase.getInstance().getReference(modelDir + "userprofile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    displayUserList.add(userProfile);

                    Log.i(TAG, userProfile.toString());

                    // 일단은 할때마다 notify set cahnged....
                    mMainView.notifyDataListChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, databaseError.toString());
                }
            });

            // Increase Page
        }
        page += 1;

        /*
        if(giverList의 사이즈가 변했으면... ){
            google map을 업데이트하고
        }
        */
        if (needListUiUpdate) {

        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
//            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
//                mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
//            }
//            updateUI();
        }
        // todo Update One, Apply to paging
        updateListUI();
        updateMapUI();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        setDefaultMapSetting(mGoogleMap);

        //noinspection MissingPermission
        mGoogleMap.setMyLocationEnabled(true);

        setFocusLastLatLng();

        if(activeUserMap.size() != 0){
            updateMapUI();
        }
    }

    private void setDefaultMapSetting(GoogleMap googleMap){
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.setMinZoomPreference(10.0f);
        googleMap.setMaxZoomPreference(10.0f);
    }

    private void setFocusLastLatLng(){
        if (mGoogleMap == null) {
            Log.i(TAG, "mGoogleMap is null, Can't focus Last Lat Lng");
            return;
        }
        if (lastLatLng == null) {
            Log.i(TAG, "Last LatLng is null, Can't focus Last Lat Lng");
            return;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lastLatLng, CURRENT_CAMERA_ZOOM);
        mGoogleMap.moveCamera(cameraUpdate);
    }


    /* Add Firebase Listener for Geo Data */
    private GeoQuery geoQuery = null;

    private void toEnableActiveUserListListener(int userType, LatLng latLng, double distance_km) {
        /* Valid Query Check */
        if (latLng == null) {
            Log.i(TAG, "Latlng is null, can't be set last geo location qeury");
            return;
        }
        if (distance_km <= 0) {
            Log.i(TAG, "Distance Query can't be set under zero");
            return;
        }

        /* Release Geo Query Remove All Listener Before getQuery */
        this.detachGeoQuery(this.geoQuery);

        /* Prepare Query */
        GeoLocation lastGeoLocationQuery = new GeoLocation(latLng.latitude, latLng.longitude);
        String userTypeQuery = getQueryDependOnUserType(userType);
        double distanceQuery = distance_km;

        /* reset get Query */
        geoQuery = this.toBuildGeoQuery(lastGeoLocationQuery, userTypeQuery, distanceQuery);
        addGeoQueryListener(geoQuery);

    }

    private void detachGeoQuery(GeoQuery geoQuery) {
        if (geoQuery != null) {
            geoQuery.removeAllListeners();
            geoQuery = null;

            if (this.geoQuery == null) {
                Log.i(TAG, "Before Geo Query Is Detached Successful");
            }
        }
    }

    private GeoQuery toBuildGeoQuery(GeoLocation coordiGeoLocationQuery, String userTypeQuery, double distanceQuery){
        String modelDir = getModelDir(userTypeQuery);
        GeoQuery geoQuery = new GeoFire(FirebaseDatabase.getInstance().getReference(modelDir)).queryAtLocation(coordiGeoLocationQuery, distanceQuery);
        return geoQuery;
    }

    private void addGeoQueryListener(GeoQuery geoQuery) {
        if (geoQuery == null) {
            Log.e(TAG, "Geo Query is null, Can't not add Listener");
            return;
        }
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                LatLng latLng = new LatLng(location.latitude, location.longitude);
                ActiveUser activeUser = new ActiveUser(key, latLng);
                activeUserMap.put(key, activeUser);
                activeUserList.add(key);

                /* No need this method... validate distance */
                float distance = LatLngUtil.distanceBetweenTwoLatLngUnitMeter(lastLatLng, activeUser.getLatLng());
                Log.i(TAG, "ADD User Complete Active User Key is " + activeUser.getKey() + ", distance is " + distance);
            }

            @Override
            public void onKeyExited(String key) {
                // todo need array list set... for remove activeuser using key... and search by order
                activeUserMap.remove(key);
                activeUserList.remove(key);
                Log.i(TAG, "ADD User Complete Active User Key is " + key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private String getQueryDependOnUserType(int userType){
        if (userType == Const.UserType.Giver) {
            return Const.QueryKey.GIVER;
        } else {
            return Const.QueryKey.TAKER;
        }
    }

    /* Add Firebase Listener, refer key is "invite" */
    private void addInviteListener(){

        String modelDir = FirebaseHelper.getModelDir("invite", Const.MY_USER_KEY);

        FirebaseDatabase.getInstance().getReference(modelDir).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Invite invite = dataSnapshot.getValue(Invite.class);
                getInviteDialog(getActivity()).showInvitedDialog(invite);

                // todo readed invite must be deleted...
                // dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "on Child Added called ");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "on Child Added called ");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "on Child Added called ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "on Child Added called ");
            }
        });
    }

    /* Location... */
    @SuppressWarnings("MissingPermission")
    private void updateLastLatLng(final PostUpdateLatLng postUpdateLatLng) {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location  = task.getResult();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            lastLatLng = latLng;
                            postUpdateLatLng.callback();
                            Log.i(TAG, "Last Location Update Complete, My Last Location is " + latLng.toString());
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            // showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    public interface PostUpdateLatLng {
        void callback();
    }

    /* View Listener */
    public void onItemClicked(UserProfile userProfile) {
        getInviteDialog(getActivity()).showInvitingDialog(userProfile, USER_TYPE);
    }

    /* View Update */
    private void updateMapUI(){
        if (mGoogleMap == null) {
            return;
        }

        // update google map

        //Marker marker = new Marker();
        //new MarkerOptions().position().title()

        // Add a marker in Sydney and move the camera
        // mGoogleMap.addMarker(new MarkerOptions().position(lastLatLng).title("I'm here"));
        //mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    private void updateListUI(){
        // update list View...

        Log.i(TAG, "Update UI List View");
    }


    /* Activity Life Cycler */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /* Runtime Permission Check for getting My Location */
    private void toGetLocationPermission() {
        if (isPermissionsGranted()) {
            this.runService();
        } else {
            /* If permission is not granted, Request permission at once  */
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, Const.LOCATION_REQ_CODE);
            } else {
                // Permission이 없으면 서비스를 정상적으로 이용할수 없습니다.
            }
        }
    }

    public boolean isPermissionsGranted(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Const.LOCATION_REQ_CODE) {
            if (isPermissionsGranted()) {
                runService();
            } else {
                Log.e(TAG, "권한이 없으면 서비스를 정상적으로 이용할 수 없습니다.");
                Toast.makeText(getActivity(), "권한이 없으면 서비스를 정상적으로 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            // lastTryToSetEnableMyLocation();
        }
    }


    /* Getter and Setter */
    public List<UserProfile> getDataList() {
        return this.displayUserList;
    }

    private InviteDialog getInviteDialog(Object object){
        if (object instanceof InviteDialog) {
            return (InviteDialog) object;
        } else {
            throw new RuntimeException("You have to implement Invite Dialog Interface");
        }
    }
}





