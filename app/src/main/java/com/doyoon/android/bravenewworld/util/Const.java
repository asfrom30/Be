package com.doyoon.android.bravenewworld.util;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class Const {

    /* not final */
    public static String MY_USER_KEY;

    /* DAO REF KEY */
    public static class RefKey {
        public static String CHAT_ROOM = "chatroom";
        public static String CHAT_PROFILE = "chatprofile";
    }

    /* final */
    public static final float DEFAULT_CAMERA_ZOOM = 15.0f;

    public static final int LOCATION_REQ_CODE = 100;

    public static final double DEFAULT_SEARCH_DISTANCE_KM = 2.0;  // 100m

    public static final int PAGING_NUMBER_AT_ONCE = 10;

    public static class QueryKey {
        public static String GIVER = "giver";
        public static String TAKER = "taker";
    }

    public static class UserType {
        public static int Taker = -1;
        public static int Giver = 1;
    }

    public static class Gender {
        public static int MALE = 1;
        public static int FEMALE = -1;
    }

    public static class DialogType {
        public static final int NOTICE = 1;
        public static final int INVITED = 2;
        public static final int INVITING = 3;
    }
}
