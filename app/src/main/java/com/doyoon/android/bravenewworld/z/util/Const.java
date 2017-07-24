package com.doyoon.android.bravenewworld.z.util;

import com.doyoon.android.bravenewworld.R;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class Const {

    /* Extra Key */
    public static final class ExtraKey {
        public static final String USER_ACCESS_KEY = "user_access_key";
    }

    /* LocationFragment Setting */
    public static class GoogleMap {
        public static final int REQUEST_CHECK_SETTING = 0x01;
        public static final int UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
        public static final int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
        public static final int PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    }

    /* View Resource ID */
    public static class LOCATION_FRAG {
        public static float MARKER_ALPHA = 1.0f;
        public static int DEFAULT_MAP_PIN_RES_ID = R.drawable.map_icon_mylocation;
        public static int MY_MAP_PIN_RES_ID = R.drawable.map_icon_mylocation;
        public static int OTHER_MAP_PIN_RES_ID = R.drawable.map_icon_location_rain;
    }


    public static class MapSetting {

    }

    public static class ActiveUserMapSetting {
        public static float MARKER_ALPHA = 1.0f;

        public static int getMapPinResId(int activeUserType){
            if (activeUserType == Const.ActiveUserType.Taker) {
                return R.drawable.map_pin_umbrella;
            } else if (activeUserType == Const.ActiveUserType.Giver) {
                return R.drawable.map_pin_rain;
            } else {
                throw new IllegalStateException("AppPresenter User Type is not declared, can't get Map Pin Resource ID`");
            }
        }
    }


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

        public static String LOCATION_SERCIVE = "locationservice";
        public static String LOCATION_FINDER = "locationfinder";

        public static String PROFILE_NAME = "name";
        public static String PROFILE_WORK = "work";
        public static String PROFILE_AGE = "age";
        public static String PROFILE_COMMENT = "comment";
        public static String PROFILE_GENDER = "gender";
        public static String PROFILE_RAIN_COUNT = "rainCount";
        public static String PROFILE_UMB_COUNT = "umbCount";

    }

    public static class StorageRefKey {
        public static String USER_PROFILE = "userprofile";
    }

    /* final */
    public static final float DEFAULT_CAMERA_ZOOM = 15.0f;

    public static final int LOCATION_REQ_CODE = 100;

    public static final float DEFAULT_FINDING_DISTANCE_M = 50;
    public static final double DEFAULT_SEARCH_DISTANCE_KM = 2.0;  // 100m

    public static final int PAGING_NUMBER_AT_ONCE = 10;


    public static class ACTIVITY_REQ_CODE {
        public static final int CAMERA = 101;
        public static final int SELECT_PROFILE_IMAGE = 102;
    }

    public static class QueryKey {
        public static String GIVER = "giver";
        public static String TAKER = "taker";
    }

    public static class ActiveUserType {
        public static final int NOT_YET_CHOOSED = 0;
        public static final int Taker = -1;
        public static final int Giver = 1;
    }

    public static class UserStatus {
        public static final int USER_NOT_YET_MATCHED = 0;
        public static final int ON_MATCHING = 1;
        public static final int ON_FINDING = 2;
        public static final int USER_ON_TOGHETHER = 3;
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

    public static class DefaultLatlng {
        public static double latitude = 37.575801;
        public static double longitude = 126.976719;
    }

}
