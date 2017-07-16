package com.doyoon.android.bravenewworld.view.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.RemoteDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseGeoDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.geovalue.ActiveUser;
import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.base.fragment.PermissionFragment;
import com.doyoon.android.bravenewworld.presenter.dialog.PickmeRequestNoticeDialog;
import com.doyoon.android.bravenewworld.presenter.dialog.PickmeRequestSendingDialog;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class UserSelectMapFragment extends PermissionFragment implements OnMapReadyCallback {

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
    public static int USER_TYPE = Const.UserType.NOT_YET_CHOOSED;

    /* Shared Preference */
    private boolean onMatchingFlag = true;
    private int startIndexForFetchUser = 0;
    private LatLng lastLatLng;
    private double SEARCH_DISTANCE_KM = 100;
    private float CURRENT_CAMERA_ZOOM = Const.DEFAULT_CAMERA_ZOOM;

    public static UserSelectMapFragment instance;

    public static UserSelectMapFragment getInstance(){
        if (instance == null) {
            instance = new UserSelectMapFragment();
        }
        Log.e(TAG, "return instance");
        return instance;
    }

    private UserSelectMapFragment() {

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

        /* Layout Inflating */
        if (baseView == null) {
            baseView = inflater.inflate(R.layout.fragment_user_select_map, container, false);
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
        }
        return baseView;
    }

    public void reset(int userType){

        this.removeMatchingCompleteListener();

        onMatchingFlag = false;
        UserSelectMapFragment.USER_TYPE = userType;

    }

    public void runService(){
        if (matchingCompleteListener == null) {
            initializeMatchingCompleteListener();
            this.addMatchingCompleteListener();
        }

        checkRuntimePermission(new Callback() {
            @Override
            public void runWithPermission() {
                run();
            }

            @Override
            public void runWithoutPermission() {

            }
        });
    }

    private void run(){
        Log.e(TAG, "On SElect Map Fragment Run Service");

        if(USER_TYPE == Const.UserType.Giver) {
            this.addPickMeRequestListener();
        }

        if (!onMatchingFlag) {
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

    private void runFragmentWithoutPermission() {

    }

    private void stop(){

    }

    /* Matching Complete Listener on Firebase */
    private ValueEventListener matchingCompleteListener;

    private void initializeMatchingCompleteListener(){
        matchingCompleteListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e(TAG, "Value Changed");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void addMatchingCompleteListener(){
        final String modelPath = FirebaseHelper.getModelPath(Const.RefKey.MATCHING_COMPLETE, Const.MY_USER_KEY);
        FirebaseDatabase.getInstance().getReference(modelPath).addValueEventListener(matchingCompleteListener);
    }

    private void removeMatchingCompleteListener(){
        final String modelPath = FirebaseHelper.getModelPath(Const.RefKey.MATCHING_COMPLETE, Const.MY_USER_KEY);
        FirebaseDatabase.getInstance().getReference(modelPath).removeEventListener(matchingCompleteListener);
    }

    /* Core Business Logic */
    private void receivePickMeRequest(PickMeRequest pickMeRequest){
        DialogFragment dialogFragment = new PickmeRequestNoticeDialog(pickMeRequest, new PickmeRequestNoticeDialog.Callback(){
            @Override
            public void onPostExecuteInPositiveClicked() {
                viewPagerMover.moveViewPage(Const.ViewPagerIndex.CHAT);
            }
        });
        dialogFragment.show(getFragmentManager(), null);
        // Create Pick Me Response

        // and send
        // inviteDialog.showInvitedDialog(pickMeRequest);

        // todo readed pickMeRequest must be deleted...
        // dataSnapshot.getRef().removeValue();
    }

    public void onActiveUserItemClicked(UserProfile clickedUserProfile) {
        DialogFragment dialogFragment = new PickmeRequestSendingDialog(USER_TYPE, clickedUserProfile,new PickmeRequestSendingDialog.Callback() {
            /* Show Detail Profile */

            /* Sending */
        });
        dialogFragment.show(getFragmentManager(), null);
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
                // todo  남은 메세지수 몇개... 방향키로 메세지 이동할 수 있게 할 것...
                // todo Message를 add만 하고 update ui하게끔 onChilAdded와 같은 Thread를 쓰면 안된다.
                // todo 이것은 그냥 데이터만 추가하고.... 다른 쓰레드에서 update를 갱신할 수 있도록
                // todo 여기에 다 추가하면... firebase firebase 업데이트 속도가 느려질 수 있겠지만 큰 상관은 없을듯...
                PickMeRequest pickMeRequest = dataSnapshot.getValue(PickMeRequest.class);
                receivePickMeRequest(pickMeRequest);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled");
            }
        });
    }

    private void changeOnMatchingStatus(boolean bool) {
        if (bool) {
            onMatchingFlag = true;
        } else {

            onMatchingFlag = false;
        }
    }

    private void runChatService(String otherUserAccessKey, String chatAccessKey){
        // todo Delete All Request History...
        UserChatFragment.getInstance().runChatService();
        // getViewPagerMover(getActivity()).moveViewPage(Const.ViewPagerIndex.CHAT);
        viewPagerMover.moveViewPage(Const.ViewPagerIndex.CHAT);
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }




    /* Getter and Setter */
    public List<UserProfile> getDataList() {
        return this.displayUserList;
    }

    private interface PostUpdateLatLng {
        void callback();
    }

}





