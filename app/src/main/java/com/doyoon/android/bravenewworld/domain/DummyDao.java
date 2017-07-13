package com.doyoon.android.bravenewworld.domain;

import android.util.Log;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseGeoDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.Invite;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.ConvString;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import static com.doyoon.android.bravenewworld.util.ConvString.commaSignToString;

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

    /* MY DUMMY PROFILE */
    public static UserProfile createDummyMyProfile() {
        String email = "miraee05@naver.com";
        String key = commaSignToString(email);
        UserProfile userProfile = new UserProfile("김도윤", 33, Const.Gender.MALE, email);
        userProfile.setKey(key);
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

    public static void createDummy(){
        /* Create MY Dummy */
        UserProfile myUserProfile = DummyDao.createDummyMyProfile();
        String myAccessKey = ConvString.commaSignToString(myUserProfile.getEmail());
        FirebaseDao.insert(myUserProfile, myAccessKey);

        for(int i =0; i < 10; i++) {
            /* Create Dummy Profile */
            UserProfile userProfile = DummyDao.createDummyUserProfile();
            String userAccessKey = ConvString.commaSignToString(userProfile.getEmail());
            Log.i(TAG, "Create Dummy User : " + userProfile.toString());
            FirebaseDao.insert(userProfile, userAccessKey);

            /* Insert givers using Geo Fire */
            LatLng latLng = DummyDao.createDummyLatLng();
            String modelDir = FirebaseHelper.getModelDir("giver");
            FirebaseGeoDao.insert(modelDir, userAccessKey, new GeoLocation(latLng.latitude, latLng.longitude));
        }
    }


    /* Dummy Invite */
    public static void insertDummyInvite(){
        /* Get User Profile */
        String targetDummyAccessKey = "ZMWAFD3OLF@google_comma_com";
        String modelDir = FirebaseHelper.getModelDir("userprofile", targetDummyAccessKey);

        /* Create Dummy Invites */
        final Invite invite = new Invite();

        FirebaseDatabase.getInstance().getReference(modelDir + "userprofile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                invite.fetchDataFromUserProfile(userProfile);
                FirebaseDao.insert(invite, Const.MY_USER_KEY);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
