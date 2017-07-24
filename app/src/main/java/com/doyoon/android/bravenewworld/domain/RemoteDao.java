package com.doyoon.android.bravenewworld.domain;

import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseGeoDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelDir;
import static com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper.getModelPath;
import static com.doyoon.android.bravenewworld.z.util.Const.RefKey.LOCATION_SERCIVE;

/**
 * Created by DOYOON on 7/14/2017.
 */

public class RemoteDao {

    private static final String TAG = RemoteDao.class.getSimpleName();

    public static class MatchingComplete {
        public static void remove(){
            String modelPath = FirebaseHelper.getModelPath(Const.RefKey.MATCHING_COMPLETE, UserStatusPresenter.myUserAccessKey);
            FirebaseDatabase.getInstance().getReference(modelPath).removeValue();
        }
    }

    public static class LocationFinder {
        public static String insertWithGetRandomKey(LatLng latLng) {
            String modelPath = FirebaseHelper.getModelPath(LOCATION_SERCIVE);
            String randomKey = FirebaseDatabase.getInstance().getReference(modelPath).push().getKey();

            if(latLng == null) latLng = new LatLng(Const.DefaultLatlng.latitude, Const.DefaultLatlng.longitude);

            com.doyoon.android.bravenewworld.domain.firebase.value.LocationFinder locationFinder
                    = new com.doyoon.android.bravenewworld.domain.firebase.value.LocationFinder(UserStatusPresenter.getInstance().myUserAccessKey, latLng.latitude, latLng.longitude);
            FirebaseDao.insert(locationFinder, randomKey);
            return randomKey;
        }

        public static void updateMyLocation(LatLng latLng, String... accessKeys){
            String modelPath = FirebaseHelper.getModelDir(Const.RefKey.LOCATION_SERCIVE);
            modelPath += accessKeys[0] + "/" + accessKeys[1];
            FirebaseDatabase.getInstance().getReference(modelPath + "/latitude").setValue(latLng.latitude);
            FirebaseDatabase.getInstance().getReference(modelPath + "/longitude").setValue(latLng.longitude);
        }

        public static ValueEventListener addListener(String locationAccessKey, String userAccessKey, final Callback callback){
            String modelPath = FirebaseHelper.getModelDir(Const.RefKey.LOCATION_SERCIVE);
            modelPath = modelPath + locationAccessKey + "/" + userAccessKey;

            Log.e(TAG, "Add Listener model path " + modelPath);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    com.doyoon.android.bravenewworld.domain.firebase.value.LocationFinder locationFinder
                            = dataSnapshot.getValue(com.doyoon.android.bravenewworld.domain.firebase.value.LocationFinder.class);
                    callback.execute(locationFinder);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            FirebaseDatabase.getInstance().getReference(modelPath).addValueEventListener(valueEventListener);

            return valueEventListener;
        }

        public static void removeListener(String locationAccessKey, String userAccessKey, ValueEventListener valueEventListener){
            String modelPath = FirebaseHelper.getModelPath(Const.RefKey.LOCATION_SERCIVE, locationAccessKey);
            modelPath = modelPath + userAccessKey;

            FirebaseDatabase.getInstance().getReference(modelPath).removeEventListener(valueEventListener);
        }

        public interface Callback {
            void execute(com.doyoon.android.bravenewworld.domain.firebase.value.LocationFinder locationFinder);
        }
    }

    public static class ActiveUser {
        public static void insert(LatLng latLng) {

            if (UserStatusPresenter.activeUserType == Const.ActiveUserType.NOT_YET_CHOOSED) {
                Log.e(TAG, "Try to insert Active User at geo fire, but user type is not yet choosed.");
                return;
            }

            if (UserStatusPresenter.myUserAccessKey == null) {
                Log.e(TAG, "Try to insert Active User at geo fire, but myUserAccessKey is null");
                return;
            }

            String modelDir = "";

            switch(UserStatusPresenter.activeUserType){
                case Const.ActiveUserType.Giver:
                    modelDir = getModelDir(Const.RefKey.ACTIVE_USER_TYPE_GIVER);
                    break;
                case Const.ActiveUserType.Taker:
                    modelDir = getModelDir(Const.RefKey.ACTIVE_USER_TYPE_TAKER);
                    break;
            }

            FirebaseGeoDao.insert(modelDir, UserStatusPresenter.myUserAccessKey, new GeoLocation(latLng.latitude, latLng.longitude));
        }

        public static void remove(int userType, String... userAccessKey){

            if (userType== Const.ActiveUserType.NOT_YET_CHOOSED) {
                Log.e(TAG, "Try to remove Active User at geo fire, but user type is not yet choosed.");
                return;
            }

            if (UserStatusPresenter.myUserAccessKey == null) {
                Log.e(TAG, "Try to remove Active User at geo fire, but myUserAccessKey is null");
                return;
            }

            String modelDir = "";

            if(userType == Const.ActiveUserType.Giver) {
                modelDir = getModelDir(Const.RefKey.ACTIVE_USER_TYPE_GIVER);
            } else if(userType == Const.ActiveUserType.Taker){
                modelDir = getModelDir(Const.RefKey.ACTIVE_USER_TYPE_TAKER);
            }

            FirebaseGeoDao.delete(modelDir, UserStatusPresenter.myUserAccessKey);
        }
    }

    public static class FetchUserProfile {
        private static String TAG = FetchUserProfile.class.getSimpleName();

        public static void execute(String userAccessKey, final FetchUserProfile.Callback callback){
            String modelDir = getModelDir("userprofile", userAccessKey);
            FirebaseDatabase.getInstance().getReference(modelDir + "userprofile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    callback.postExecute(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(FetchUserProfile.TAG, databaseError.toString());
                }
            });
        }

        public interface Callback {
            void postExecute(DataSnapshot dataSnapshot);
        }
    }


    public static class FetchMyProfile {
        public static void execute(){
            String modelDir = getModelDir(Const.RefKey.USER_PROFILE, UserStatusPresenter.myUserAccessKey) + Const.RefKey.USER_PROFILE;
            FirebaseDatabase.getInstance().getReference(modelDir).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile userProfile = dataSnapshot.getValue(com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile.class);
                    UserStatusPresenter.myUserProfile = userProfile;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static class UserProfile {
        public static void insertName(String name){
            String modelPath = getModelPath(Const.RefKey.USER_PROFILE,  UserStatusPresenter.myUserAccessKey);
            FirebaseDatabase.getInstance().getReference(modelPath + Const.RefKey.PROFILE_NAME).setValue(name);
        }

        public static void insertWork(String work){
            String modelPath = getModelPath(Const.RefKey.USER_PROFILE,  UserStatusPresenter.myUserAccessKey);
            FirebaseDatabase.getInstance().getReference(modelPath + Const.RefKey.PROFILE_WORK).setValue(work);
        }

        public static void insertAge(int age){
            String modelPath = getModelPath(Const.RefKey.USER_PROFILE,  UserStatusPresenter.myUserAccessKey);
            FirebaseDatabase.getInstance().getReference(modelPath + Const.RefKey.PROFILE_AGE).setValue(age);
        }

        public static void insertComment(String comment){
            String modelPath = getModelPath(Const.RefKey.USER_PROFILE,  UserStatusPresenter.myUserAccessKey);
            FirebaseDatabase.getInstance().getReference(modelPath + Const.RefKey.PROFILE_COMMENT).setValue(comment);
        }

        public static void insertGender(int gender) {
            String modelPath = getModelPath(Const.RefKey.USER_PROFILE,  UserStatusPresenter.myUserAccessKey);
            FirebaseDatabase.getInstance().getReference(modelPath + Const.RefKey.PROFILE_GENDER).setValue(gender);
        }

        public static void increaseRainCount() {
            String modelPath = getModelPath(Const.RefKey.USER_PROFILE,  UserStatusPresenter.myUserAccessKey);

        }

        public static void increaseUmbCount() {
            String modelPath = getModelPath(Const.RefKey.USER_PROFILE,  UserStatusPresenter.myUserAccessKey);
        }


    }
}
