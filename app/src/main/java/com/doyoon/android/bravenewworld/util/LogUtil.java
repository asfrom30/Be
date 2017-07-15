package com.doyoon.android.bravenewworld.util;

import android.util.Log;

/**
 * Created by DOYOON on 7/15/2017.
 */

public class LogUtil {

    public static void logLifeCycle(String tag, String lifecycleStatus){
        Log.i(StringUtil.padRight(Const.LIFE_CYCLE_TAG + "     [ " + tag, 45) + " ] : ", lifecycleStatus);
    }
}
