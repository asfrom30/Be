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
}
