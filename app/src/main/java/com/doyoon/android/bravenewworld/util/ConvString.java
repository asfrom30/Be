package com.doyoon.android.bravenewworld.util;

/**
 * Created by DOYOON on 7/9/2017.
 */

public class ConvString {

    private static String COMMA = "_comma_";

    public static String commaSignToString(String email) {
        return email.replace(".", COMMA);
    }

    public static String commaStringToSign(String string) {
        return string.replace(COMMA, ".");
    }

    public static String getGender(int gender) {
        if (gender == Const.Gender.MALE) {
            return "남자";
        } else {
            return "여자";
        }
    }

    public static String getDistance(float distance, String unit) {
        return String.format("%3.0f", distance) + unit;
    }

    public static String getShortName(String name) {
        int index = name.indexOf("@");

        if(index == -1) {
            return name;
        } else {
            return name.substring(0, index);
        }
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
