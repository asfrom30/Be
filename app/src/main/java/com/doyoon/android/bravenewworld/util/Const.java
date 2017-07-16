package com.doyoon.android.bravenewworld.util;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.value.UserProfile;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class Const {

    /* not final */
    public static String MY_USER_KEY;
    public static UserProfile MY_USER_PROFILE;

    /* TAG */
    public static final String LIFE_CYCLE_TAG = "Life Cycle : ";


    /* DAO REF KEY */
    public static class RefKey {
        public static String ACTIVE_USER_TYPE_GIVER = "giver";
        public static String ACTIVE_USER_TYPE_TAKER = "taker";

        public static String CHAT_ROOM = "chatroom";
        public static String CHAT_PROFILE = "chatprofile";
        public static String PICK_ME_REQUEST = "pickmerequest";
        public static String MATCHING_COMPLETE = "matchingcomplete";
        public static String CHAT = "chat";

        public static String USER_PROFILE = "userprofile";
        public static String USER_PROFILE_IMAGE_URI = "imageUri";
    }
    public static class StorageRefKey {
        public static String USER_PROFILE = "userprofile";
    }

    /* final */
    public static final float DEFAULT_CAMERA_ZOOM = 15.0f;

    public static final int LOCATION_REQ_CODE = 100;

    public static final double DEFAULT_SEARCH_DISTANCE_KM = 2.0;  // 100m

    public static final int PAGING_NUMBER_AT_ONCE = 10;

    public static class MAP_SETTING {
        public static float MARKER_ALPHA = 1.0f;
        public static int GIVER_MAP_PIN_RES_ID = R.drawable.map_pin_umbrella;
        public static int TAKER_MAP_PIN_RES_ID = R.drawable.map_pin_rain;
    }

    public static class ACTIVITY_REQ_CODE {
        public static final int CAMERA = 101;
        public static final int GALLERY = 102;
    }

    public static class QueryKey {
        public static String GIVER = "giver";
        public static String TAKER = "taker";
    }

    public static class UserType {
        public static int Taker = -1;
        public static int NOT_YET_CHOOSED = 0;
        public static int Giver = 1;
    }

    public static class Gender {
        public static int MALE = 1;
        public static int FEMALE = -1;
    }

    public static class ViewPagerIndex {
        public static int SELECT = 0;
        public static int MAP = 1;
        public static int CHAT = 2;
        public static int PROFILE = 3;
    }

}
