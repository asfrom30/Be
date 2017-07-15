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
import com.doyoon.android.bravenewworld.domain.RemoteDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseGeoDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.geovalue.ActiveUser;
import com.doyoon.android.bravenewworld.domain.firebase.value.MatchingComplete;
import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.activity.interfaces.InviteDialog;
import com.doyoon.android.bravenewworld.presenter.interfaces.ViewPagerMover;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LatLngUtil;
import com.doyoon.android.bravenewworld.util.LogUtil;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class UserSelectMapFragment extends Fragment implements OnMapReadyCallback {

    private static String TAG = UserSelectMapFragment.class.getSimpleName();
    private static int linkRes = R.layout.fragment_user_select_map;

    /* View */
    private UserSelectFragmentView mMainView;
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private FusedLocationProviderClient mFusedLocationClient;

    private List<String> activeUserList = new ArrayList<>();
    private Map<String, ActiveUser> activeUserMap = new HashMap<>();
    private List<UserProfile> displayUserList = new ArrayList();

    /* Static Preference */
    public static int USER_TYPE = Const.UserType.Taker;

    /* Shared Preference */
    private boolean onMatching = false;
    private int startIndexForFetchUser = 0;
    private LatLng lastLatLng;
    private double SEARCH_DISTANCE_KM = 100;
    private float CURRENT_CAMERA_ZOOM = Const.DEFAULT_CAMERA_ZOOM;

    ViewPagerMover viewPagerMover;
    InviteDialog inviteDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LogUtil.logLifeCycle(TAG, "onCreateView()");

        /* Layout Inflating */
        View baseView = inflater.inflate(R.layout.fragment_user_select_map, container, false);
        this.mMainView = new UserSelectFragmentView(this, getContext(), baseView);

        /* interface dependency */
        this.viewPagerMover = getViewPagerMover(getActivity());
        this.inviteDialog = getInviteDialog(getActivity());

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
            this.addPickMeRequestListener();
        } else if(USER_TYPE == Const.UserType.Taker){
            this.addMatchingCompleteListener();
        }

        if (!onMatching) {
            // todo insertMyUser to giver or taker...
        }

        //thisView.startInProgress();
        updateLastLatLng(new PostUpdateLatLng() {
            @Override
            public void callback() {

                /* Register Active User */
                if(USER_TYPE == Const.UserType.Giver) {
                    String modelDir = FirebaseHelper.getModelDir(Const.RefKey.ACTIVE_USER_TYPE_GIVER);
                    FirebaseGeoDao.insert(modelDir, Const.MY_USER_KEY, new GeoLocation(lastLatLng.latitude, lastLatLng.longitude));
                } else if(USER_TYPE == Const.UserType.Taker){
                    String modelDir = FirebaseHelper.getModelDir(Const.RefKey.ACTIVE_USER_TYPE_TAKER);
                    FirebaseGeoDao.insert(modelDir, Const.MY_USER_KEY, new GeoLocation(lastLatLng.latitude, lastLatLng.longitude));
                }

                /* After Update LatLng focus to LatLng*/
                setFocusLastLatLng();

                /* Add Geo Query Data using Last LatLng */
                activeUserMap.clear();
                activeUserList.clear();
                toEnableActiveUserListListener(USER_TYPE, lastLatLng, SEARCH_DISTANCE_KM);
            }
        });
    }

    public void fetchNextPageUserProfiles(){

        if (startIndexForFetchUser + 1 == activeUserList.size()) {
            return;
        }

        int endIndex = startIndexForFetchUser + Const.PAGING_NUMBER_AT_ONCE;

        if (endIndex > activeUserList.size()) {
            endIndex = activeUserList.size();
        }

        Log.i(TAG, startIndexForFetchUser + "부터 " + endIndex + "까지 데이터를 가져오려고 시도 합니다. ");
        fetchUserProfiles(startIndexForFetchUser, endIndex);
        startIndexForFetchUser = endIndex;
    }

    private void fetchUserProfiles(int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            String userAccessKey = activeUserList.get(i);
            RemoteDao.FetchUserProfile.execute(userAccessKey, new RemoteDao.FetchUserProfile.Callback() {
                @Override
                public void postExecute(DataSnapshot dataSnapshot) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    displayUserList.add(userProfile);
                    mMainView.notifyDataListChanged();
                    Log.i(TAG, "Fetch UserProfile Succesful : " + userProfile.toString());
                }
            });
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

    private void resetMarker (){
        if (this.mGoogleMap == null) {
            return;
        }

        this.mGoogleMap .clear();
        for (Map.Entry<String, ActiveUser> entry : activeUserMap.entrySet()) {
            ActiveUser activeUser = entry.getValue();

            if(activeUser.isActive()){
                this.mGoogleMap.addMarker(new MarkerOptions()
                        .position(activeUser.getLatLng())
                        .alpha(Const.MAP_SETTING.MARKER_ALPHA)
                        .icon(BitmapDescriptorFactory.fromResource(getMapPinResId())));
            }
        }
    }

    private void setDefaultMapSetting(GoogleMap googleMap){
        // googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

    }

    private int getMapPinResId(){
        if (USER_TYPE == Const.UserType.Taker) {
            return Const.MAP_SETTING.GIVER_MAP_PIN_RES_ID;
        } else {
            return Const.MAP_SETTING.TAKER_MAP_PIN_RES_ID;
        }

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

        Log.i(TAG, "Add Geo Query Successfully");
    }

    private void detachGeoQuery(GeoQuery geoQuery) {
        if (this.geoQuery != null) {
            this.geoQuery.removeAllListeners();
            this.geoQuery = null;

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

                // 한번 등록되었다가 등록되지 않은 유저가 다시 active로 올경우 key가 중복될수 있으니 이경우 다시 active를 해줘야 한다.
                activeUserMap.put(key, activeUser);
                activeUserList.add(key);

                /* No need this method... validate distance */
                float distance_m = LatLngUtil.distanceBetweenTwoLatLngUnitMeter(lastLatLng, activeUser.getLatLng());
                Log.i(TAG, "ADD Active User Complete " + activeUser.getKey() + ", distance(m) is " + distance_m);
            }

            @Override
            public void onKeyExited(String key) {
                /* Don't remove key and object in activeUserMap and activeUserList */
                activeUserMap.get(key).setActive(false);
                resetMarker();
                Log.i(TAG, "Remove Geo Query : Active user [" + key + "] is now deactive");
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                fetchNextPageUserProfiles();
                resetMarker();
                Log.i(TAG, "On Geo Query Ready");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private String getQueryDependOnUserType(int userType){
        if (userType == Const.UserType.Giver) {
            return Const.QueryKey.TAKER;
        } else {
            return Const.QueryKey.GIVER;
        }
    }

    /* Add Firebase Listener, refer key is "invite" */
    private void addPickMeRequestListener(){

        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.PICK_ME_REQUEST, Const.MY_USER_KEY);

        FirebaseDatabase.getInstance().getReference(modelDir).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                PickMeRequest pickMeRequest = dataSnapshot.getValue(PickMeRequest.class);
                // getInviteDialog(getActivity()).showInvitedDialog(pickMeRequest);
                inviteDialog.showInvitedDialog(pickMeRequest);

                // todo readed pickMeRequest must be deleted...
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

    /* Add Firebase Listener, refer key is "invite" */
    //todo if matching complete detach Matching Complete Listener
    private void addMatchingCompleteListener(){

        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.MATCHING_COMPLETE, Const.MY_USER_KEY);

        FirebaseDatabase.getInstance().getReference(modelDir).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // matching 중이 아닐때만...
                if (!onMatching) {
                    MatchingComplete matchingComplete = dataSnapshot.getValue(MatchingComplete.class);

                    // todo Delete All Request History...

                    // Go Chat...
                    UserChatFragment.chatAccessKey = matchingComplete.getChatAccessKey();

                    Log.i(TAG, "matching chat key is" + matchingComplete.getChatAccessKey());
                    Log.i(TAG, "set chat access key is " + UserChatFragment.chatAccessKey);

                    UserChatFragment.getInstance().runChatService();
                    // getViewPagerMover(getActivity()).moveViewPage(Const.ViewPagerIndex.CHAT);
                    viewPagerMover.moveViewPage(Const.ViewPagerIndex.CHAT);


                    // On Matching false
                    // onMatching = true;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
        //getInviteDialog(getActivity()).showSendingPickMeRequestDialog(userProfile, USER_TYPE);
        this.inviteDialog.showSendingPickMeRequestDialog(userProfile, USER_TYPE);
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

    // todo make to inclue baseFragment...
    private InviteDialog getInviteDialog(Object object){
        if (object instanceof InviteDialog) {
            return (InviteDialog) object;
        } else {
            throw new RuntimeException("You have to implement PickMeRequest Dialog Interface");
        }
    }

    // todo make to inclue baseFragment...
    private ViewPagerMover getViewPagerMover(Object object) {
        if (object instanceof InviteDialog) {
            return (ViewPagerMover) object;
        } else {
            throw new RuntimeException("You have to implement PickMeRequest Dialog Interface");
        }
    }
}





