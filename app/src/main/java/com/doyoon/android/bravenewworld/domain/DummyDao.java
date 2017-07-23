package com.doyoon.android.bravenewworld.domain;

import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseGeoDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.PickMeRequest;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.z.util.Const;
import com.doyoon.android.bravenewworld.z.util.ConvString;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import static com.doyoon.android.bravenewworld.z.util.ConvString.commaSignToString;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class DummyDao {

    private static final String TAG = DummyDao.class.getSimpleName();

    /* Secure Key */
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }

    public static String getMyDummyUserAccessKey() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        String email = "";
        if (currentApiVersion == 25) {
            email = "miraee05@naver.com";
        } else if (currentApiVersion == 23) {
            email = "jumum@google.com";
        }
        return ConvString.commaSignToString(email);
    }

    /* MY DUMMY PROFILE */
    public static UserProfile insertDummyMyProfile() {
        UserProfile userProfile = getMyProfile();
        String key = commaSignToString(userProfile.getEmail());
        userProfile.setKey(key);
        FirebaseDao.insert(userProfile, key);
        return userProfile;
    }

    private static UserProfile getMyProfile(){
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        UserProfile userProfile = null;

        if (currentApiVersion == 25) {
            userProfile =  new UserProfile("김도윤", 33, Const.Gender.MALE, "miraee05@naver.com");
            userProfile.setImageUri("https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Fkim.jpg?alt=media&token=c688f7c5-e4a2-4c09-afe4-c8b04d7657d0");
        } else if (currentApiVersion == 23) {
            userProfile =  new UserProfile("황정음", 29, Const.Gender.FEMALE, "jumum@google.com");
            userProfile.setImageUri("https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Fhwang.jpg?alt=media&token=06e493cc-be8b-4cf2-b3fc-14407b656911");
        }

        return userProfile;
    }

    private static UserProfile createDummyUserProfile() {
        int gender = getRandomInt(0, 1);
        if (gender == 0) {
            gender = -1;
        }
        UserProfile userProfile = new UserProfile(getSaltString(3), getRandomInt(20, 30), gender, commaSignToString(getRandomEmailAddress()));
        return userProfile;
    }

    private static String getRandomEmailAddress() {
        String random = getSaltString(10);
        return random + "@google.com";
    }

    public static String getRandomMsg(){
        return getSaltString(10);
    }

    private static String getSaltString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    /* MY DUMMY GIVER */
    private static LatLng createDummyLatLng() {
        LatLng latLng = getRandomLatLng();
        return latLng;
    }

    private static LatLng getRandomLatLng() {
        // ref 1 : https://www.google.co.kr/maps/place/37%C2%B029'29.7%22N+126%C2%B059'34.7%22E/@37.4917622,126.9779259,13.74z/data=!4m5!3m4!1s0x0:0x0!8m2!3d37.491576!4d126.992967?hl=en
        // ref 2 : https://www.google.co.kr/maps/place/37%C2%B031'26.9%22N+127%C2%B001'36.6%22E/@37.524129,127.0246373,17z/data=!3m1!4b1!4m5!3m4!1s0x0:0x0!8m2!3d37.524129!4d127.026826?hl=en

        double top = 127.026826;
        double bottom = 126.992967;

        double left = 37.491576;
        double right = 37.524129;

        double longitude = getRandomDouble(bottom, top);
        double latitude = getRandomDouble(left, right);
        return new LatLng(latitude, longitude);
    }

    private static Random r = new Random();

    private static double getRandomDouble(double min, double max) {
        return min + (max - min) * r.nextDouble();
    }

    private static int getRandomInt(int min, int max) {
        return r.nextInt((max - min) + 1) + min;
    }

    public static void createDummies(String userType){
        /* Create MY Dummy */
        UserProfile myUserProfile = DummyDao.insertDummyMyProfile();
        String myAccessKey = ConvString.commaSignToString(myUserProfile.getEmail());
        FirebaseDao.insert(myUserProfile, myAccessKey);

        for(int i =0; i < 10; i++) {
            /* Create Dummy Profile */
            UserProfile userProfile = DummyDao.createDummyUserProfile();
            userProfile.setImageUri(getDummyImageUrl(userProfile.getGender()));
            String userAccessKey = ConvString.commaSignToString(userProfile.getEmail());
            Log.i(TAG, "Create Dummy User : " + userProfile.toString());
            FirebaseDao.insert(userProfile, userAccessKey);

            /* Insert givers using Geo Fire for active user */
            LatLng latLng = DummyDao.createDummyLatLng();
            String modelDir = FirebaseHelper.getModelDir(userType);
            FirebaseGeoDao.insert(modelDir, userAccessKey, new GeoLocation(latLng.latitude, latLng.longitude));
        }
    }


    /* Dummy PickMeRequest */
    public static void insertDummyPickMeRequest(){
        /* Get User Profile */
        String targetDummyAccessKey = "ZMWAFD3OLF@google_comma_com";
        String modelDir = FirebaseHelper.getModelDir("userprofile", targetDummyAccessKey);

        /* Create Dummy Invites */
        final PickMeRequest pickMeRequest = new PickMeRequest();

        /* User Profile을 한번 가져온다. Invite를 생성하기 위해서... */
        FirebaseDatabase.getInstance().getReference(modelDir + "userprofile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                pickMeRequest.fetchDataFromUserProfile(userProfile);
                FirebaseDao.insert(pickMeRequest, UserStatusPresenter.myUserAccessKey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static String getDummyImageUrl(int gender){
        int index = getRandomInt(1, 5);
        String imageUrl;
        if (gender == Const.Gender.MALE) {
            switch (index) {
                case 1:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Fmale_1.jpg?alt=media&token=046139c2-57ca-4a06-98ab-f21a4c3f3023";
                    break;
                case 2:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Fmale_1.jpg?alt=media&token=046139c2-57ca-4a06-98ab-f21a4c3f3023";
                    break;
                case 3:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Fmale_1.jpg?alt=media&token=046139c2-57ca-4a06-98ab-f21a4c3f3023";
                    break;
                case 4:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Fmale_4.jpg?alt=media&token=bdf8fb32-1814-4223-852c-7618e1341976";
                    break;
                case 5:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Fmale_5.jpg?alt=media&token=218350e5-2838-4055-a876-bf8c432458a4";
                    break;
                default:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Fmale_5.jpg?alt=media&token=218350e5-2838-4055-a876-bf8c432458a4";
                    break;
            }
        } else {
            switch (index) {
                case 1:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Ffemale_1.jpg?alt=media&token=79f70ce1-18dd-41b8-bc27-0051533a3325";
                    break;
                case 2:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Ffemale_2.jpg?alt=media&token=4a14fb88-e24c-4d3e-98bc-4a13a71d670c";
                    break;
                case 3:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Ffemale_3.jpg?alt=media&token=ed2ecbb2-2538-471e-a60e-4d96abcb2a87";
                    break;
                case 4:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Ffemale_4.jpg?alt=media&token=c1fe04c5-5468-4116-bcb9-c34d7323271f";
                    break;
                case 5:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Ffemale_5.jpg?alt=media&token=64e305a1-3d9c-4c92-950d-2a51d8703654";
                    break;
                default:
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/bravenewworld-45c25.appspot.com/o/userprofile%2Ffemale_5.jpg?alt=media&token=64e305a1-3d9c-4c92-950d-2a51d8703654";
                    break;
            }
        }
        return imageUrl;
    }

}
