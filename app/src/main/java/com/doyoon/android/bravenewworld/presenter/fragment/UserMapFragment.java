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
import com.doyoon.android.bravenewworld.domain.firebase.value.Giver;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.DistanceUtil;
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
import java.util.List;

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
    private List<Giver> giverList = new ArrayList();


    public static UserMapFragment newInstance() {
        return new UserMapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /* Layout Inflating */
        View view = inflater.inflate(R.layout.fragment_user_map, container, false);
        thisView = new ThisView(getContext(), view);

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
    }

    public void onBtn (){
        Log.i(TAG, "Service Start");
        this.runService();
    }

    public void runService(){

        //thisView.startInProgress();

        updateLastLatLng(new PostUpdateLatLng() {
            @Override
            public void callback() {
                loadDataAtFirst();
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

        if(giverList.size() != 0){
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


    private void loadDataAtFirst(){
        String modelDir = FirebaseHelper.getModelDir("giver");
        FirebaseDatabase.getInstance().getReference(modelDir).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                giverList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Giver giver = item.getValue(Giver.class);

                    // Calculate Distance...
                    LatLng myLatLng = lastLatLng;
                    LatLng yourLatLng = giver.getLatLng();
                    double distance = 100;
                    if (DistanceUtil.isDistanceNear(myLatLng, yourLatLng, distance)) {
                        giverList.add(giver);
                    }
                }
                updateListUI();
                updateMapUI();

                Log.i(TAG, "Load Data At First Complete, loaded data size is " + giverList.size());
                thisView.endInProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addDataChangedListener(){
        String modelDir = FirebaseHelper.getModelDir("taker");
        FirebaseDatabase.getInstance().getReference(modelDir).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
        // updateListUI();  // Update One   // Apply paging
        // updateMapUI();
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
        for (Giver giver : giverList) {

        }
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
    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    //private LatLng getLatLng

    @SuppressWarnings("MissingPermission")
    private void updateLastLatLng(final PostUpdateLatLng postUpdateLatLng) {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Log.e(TAG, "여기는?");
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location  = task.getResult();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            lastLatLng = latLng;
                            postUpdateLatLng.callback();
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
    private void toMyLocationEnabled(PermissionChecker permissionChecker){
        if (isPermissionsGranted()) {
            //noinspection MissingPermission
            mGoogleMap.setMyLocationEnabled(true);
            Log.i(TAG, "setMyLocationEnabled = true");
            Toast.makeText(getActivity(), "Location이 승인되었습니다. 서비스를 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
        } else {
            permissionChecker.notGranted();
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

    public interface PermissionChecker {
        void notGranted();
    }

    private void firstTryToSetEnableMyLocation(){
        this.toMyLocationEnabled(new PermissionChecker() {
            @Override
            public void notGranted() {
                /* If permission is not granted, Request permission at once  */
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, Const.LOCATION_REQ_CODE);
                }
            }
        });
    }

    private void lastTryToSetEnableMyLocation(){
        toMyLocationEnabled(new PermissionChecker() {
            @Override
            public void notGranted() {
                Log.e(TAG, "권한이 없으면 서비스를 정상적으로 이용할 수 없습니다.");
                Toast.makeText(getActivity(), "권한이 없으면 서비스를 정상적으로 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class ThisView {

        private Context context;
        private View view;

        private String mLatitudeLabel;
        private String mLongitudeLabel;
        private TextView mLatitudeText;
        private TextView mLongitudeText;
        private Button button;

        private ProgressDialog progressDialog;

        private ThisView(Context context, View view) {
            this.context = context;
            this.view = view;

            this.setWidgetsPropFromResources();
            this.dependencyInjection();
            this.addWidgetsListener();

        }

        private void addWidgetsListener() {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserMapFragment.this.onBtn();
                }
            });
        }

        private void setWidgetsPropFromResources() {
            Resources resources = context.getResources();

            mLatitudeLabel = resources.getString(R.string.latitude_label);
            mLongitudeLabel = resources.getString(R.string.longitude_label);
        }

        private void dependencyInjection(){
            /* Relate to Location */
            mLatitudeText = (TextView) view.findViewById((R.id.latitude_text));
            mLongitudeText = (TextView) view.findViewById((R.id.longitude_text));

            button = (Button) view.findViewById(R.id.getPosition_btn);

            progressDialog = new ProgressDialog(this.context);
        }

        /* Data Loading Progress Dialog*/
        public void startInProgress(){
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

                    while(jumpTime < totalProgressTime) {
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

        public void endInProgress(){
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


/* show snack bar */
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
