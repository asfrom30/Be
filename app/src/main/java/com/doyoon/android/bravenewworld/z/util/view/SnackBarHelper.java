package com.doyoon.android.bravenewworld.z.util.view;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by DOYOON on 7/21/2017.
 */

public class SnackBarHelper {

    public static void show(Activity activity, String mainTextStringId, String actionStringId,
                                    View.OnClickListener listener) {
        Snackbar.make(
                activity.findViewById(android.R.id.content),
                mainTextStringId,
                Snackbar.LENGTH_SHORT)
                .setAction(actionStringId, listener).show();

    }
}
