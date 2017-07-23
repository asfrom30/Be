package com.doyoon.android.bravenewworld.z.util;

/**
 * Created by DOYOON on 7/15/2017.
 */

public class StringUtil {

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

}
