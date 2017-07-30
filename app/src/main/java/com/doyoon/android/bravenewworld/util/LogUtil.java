package com.doyoon.android.bravenewworld.util;

import android.util.Log;

/**
 * Created by DOYOON on 7/15/2017.
 */

public class LogUtil {

    /* TAG */
    public static final String LIFE_CYCLE_TAG = "Life Cycle : ";
    public static final String AUTH = "AUTH : ";

    public static void logLifeCycle(String tag, String lifecycleStatus){
        Log.i(StringUtil.padRight(LIFE_CYCLE_TAG + "     [ " + tag, 45) + " ] : ", lifecycleStatus);
    }

    public static void logErrLifeCycle(String tag, String lifecycleStatus){
        Log.e(StringUtil.padRight(LIFE_CYCLE_TAG + "     [ " + tag, 45) + " ] : ", lifecycleStatus);
    }

    public static void auth(String tag, String autStatus) {
        Log.i(StringUtil.padRight(AUTH + "     [ " + tag, 45) + " ] : ", autStatus);
    }
}
