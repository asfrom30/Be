package com.doyoon.android.bravenewworld;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by DOYOON on 7/20/2017.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "AppleSDGothicNeoM00.ttf"))
                .addBold(Typekit.createFromAsset(this, "AppleSDGothicNeoR00.ttf"));
    }
}
