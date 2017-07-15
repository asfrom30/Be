package com.doyoon.android.bravenewworld.util;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by DOYOON on 7/14/2017.
 */

public class DateUtil {

    public static String getCurrentDate(){
        return DateFormat.getDateTimeInstance().format(new Date());
    }
}
