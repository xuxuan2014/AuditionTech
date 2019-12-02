package com.dev.auditiontech;

import android.app.Application;

import androidx.room.Room;

import com.dev.auditiontech.persistence.AppDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class AuditionTechApplication extends Application {
    private AppDatabase appDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }

    public AppDatabase getAppDatabase() {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(this, AppDatabase.class, "logs").build();
        }
        return appDatabase;
    }
}
