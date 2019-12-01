package com.dev.auditiontech;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class AuditionTechApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
