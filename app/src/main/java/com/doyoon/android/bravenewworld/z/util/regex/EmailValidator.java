package com.doyoon.android.bravenewworld.z.util.regex;

import java.util.regex.Pattern;

/**
 * Created by DOYOON on 7/20/2017.
 */

public class EmailValidator {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean validate(final String hex) {
        return pattern.matcher(hex).matches();
    }

}
