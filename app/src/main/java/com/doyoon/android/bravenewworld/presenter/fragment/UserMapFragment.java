package com.doyoon.android.bravenewworld.presenter.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.geovalue.ActiveUser;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.fragment.abst.RecyclerFragment;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LatLngUtil;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class UserMapFragment extends Fragment implements OnMapReadyCallback {

    private static String TAG = UserMapFragment.class.getSimpleName();

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private FusedLocationProviderClient mFusedLocationClient;

    private ThisView thisView;

    private LatLng lastLatLng;

    private Map<String, ActiveUser> activeUserMap = new HashMap<>();
    private List<UserProfile> displayUserList = new ArrayList();

    /* Preference */
    private double SEARCH_DISTANCE_KM = 100;

    public static UserMapFragment newInstance() {
        return new UserMapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /* Layout Inflating */
        View view = inflater.inflate(R.layout.fragment_user_map, container, false);
        thisView = new ThisView(this, getContext(), view);

        /* Get Default Setting  */
        SEARCH_DISTANCE_KM = Const.DEFAULT_SEARCH_DISTANCE_KM;

        /* Bundle */
        updateValuesFromBundle(savedInstanceState);

        /* Map View Dependency */
        // Gets the MapView from the XML layout and creates it
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMapView.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if(isPermissionsGranted()){
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
        return view;
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

    boolean needListUiUpdate = false;

    /* This is On Btn */
    public void onTriggeredUpdateUIThread(){
        // Log.i(TAG, "Service Start");
        // this.runService();
        /*
        if(giverList의 사이즈가 변했으면... ){
            google map을 업데이트하고
        }
        */
        if (needListUiUpdate) {

        }
        addGeoQueryListener();
    }

    public void runService(){

        //thisView.startInProgress();
        updateLastLatLng(new PostUpdateLatLng() {
            @Override
            public void callback() {
                /* Initialize end... real service start */
                activeUserMap.clear();
                changeDataListenerFromLastLatLng();
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        /* Google Map Default setting */
        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        //mGoogleMap.setMinZoomPreference(10.0f);
        //mGoogleMap.setMaxZoomPreference(10.0f);

        //noinspection MissingPermission
        mGoogleMap.setMyLocationEnabled(true);

        if (lastLatLng != null) {
            setFocusLastLatLng();
        }

        if(activeUserMap.size() != 0){
            updateMapUI();
        }
    }

    private void setFocusLastLatLng(){
        if (mGoogleMap == null) {
            return;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lastLatLng, 10.0f);
        mGoogleMap.moveCamera(cameraUpdate);
    }


    /* Data Listener */
    private GeoFire geoFire = null;
    private GeoQuery geoQuery = null;

    private void changeDataListenerFromLastLatLng(){
        toSetEnabledDataListener();
    }
    private void changeDataListenerFromDistance(double distance_km) {
        SEARCH_DISTANCE_KM = distance_km;
        toSetEnabledDataListener();
    }

    private void toSetEnabledDataListener(){
        Log.e(TAG, "Request Geo DAO");

        this.toUpdateGeoQuery();

        if(geoQuery == null) {
            Log.e(TAG, "Geo Query is null... please check this line");
        }

        this.geoQuery.removeAllListeners();
        this.addGeoQueryListener();
    }

    private void toUpdateGeoQuery(){
        if (geoFire == null) {
            String modelDir = FirebaseHelper.getModelDir("giver");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(modelDir);
            geoFire = new GeoFire(ref);
        }
        GeoLocation lastGeoLocation = new GeoLocation(lastLatLng.latitude, lastLatLng.longitude);
        geoQuery = geoFire.queryAtLocation(lastGeoLocation, SEARCH_DISTANCE_KM);
    }

    private void addGeoQueryListener() {
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                LatLng latLng = new LatLng(location.latitude, location.longitude);
                ActiveUser activeUser = new ActiveUser(key, latLng);
                activeUserMap.put(key, activeUser);

                /* No need this method... validate distance */
                float distance = LatLngUtil.distanceBetweenTwoLatLngUnitMeter(lastLatLng, activeUser.getLatLng());
                Log.i(TAG, "ADD User Complete Active User Key is " + activeUser.getKey() + ", distance is " + distance);
            }

            @Override
            public void onKeyExited(String key) {
                // todo need array list set... for remove activeuser using key... and search by order
                activeUserMap.remove(key);
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

    /* Runtime Permission Check for getting My Location */
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

    private static class ThisView {

        private Context context;
        private UserMapFragment presenter;
        private View view;

        private String mLatitudeLabel;
        private String mLongitudeLabel;
        private TextView mLatitudeText;
        private TextView mLongitudeText;
        private Button tempButton;

        private ProgressDialog progressDialog;

        private Fragment displayUserListFragment;

        private ThisView(UserMapFragment userMapFragment, Context context, View view) {
            this.presenter = userMapFragment;
            this.context = context;
            this.view = view;

            this.setWidgetsPropFromResources();
            this.dependencyInjection();
            this.addWidgetsListener();
        }

        private void addWidgetsListener() {
            tempButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onTriggeredUpdateUIThread();
                }
            });
        }

        private void setWidgetsPropFromResources() {
            Resources resources = context.getResources();

            mLatitudeLabel = resources.getString(R.string.latitude_label);
            mLongitudeLabel = resources.getString(R.string.longitude_label);
        }

        private void dependencyInjection() {
            /* Relate to Location */
            mLatitudeText = (TextView) view.findViewById((R.id.latitude_text));
            mLongitudeText = (TextView) view.findViewById((R.id.longitude_text));

            tempButton = (Button) view.findViewById(R.id.getPosition_btn);

            displayUserListFragment = new DisplayUserListFragment();

            progressDialog = new ProgressDialog(this.context);
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

    }

    public static class DisplayUserListFragment extends RecyclerFragment<UserProfile> {

        private List<UserProfile> displayUserList;

        public DisplayUserListFragment() {
            this.displayUserList = displayUserList;
        }

        @Override
        public CustomViewHolder throwCustomViewHolder(View view) {
            return null;
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
            return displayUserList;
        }

        @Override
        public int throwItemLayoutId() {
            return R.layout.item_active_user_list;
        }
    }
}





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



