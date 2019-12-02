package com.dev.auditiontech;

import android.app.Application;

import androidx.room.Room;

import com.dev.auditiontech.persistence.AppDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;

import static com.dev.auditiontech.persistence.AppDatabase.MIGRATION_1_2;
import static com.dev.auditiontech.persistence.AppDatabase.MIGRATION_2_3;

public class AuditionTechApplication extends Application {
    private AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }

    public AppDatabase getAppDatabase() {
        if (appDatabase == null) {
            appDatabase = Room
                    .databaseBuilder(this, AppDatabase.class, "logs")
                    .build();
        }
        return appDatabase;
    }
}
