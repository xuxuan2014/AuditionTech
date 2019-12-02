package com.dev.auditiontech.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dev.auditiontech.persistence.dao.AmbientVolumeDAO;
import com.dev.auditiontech.persistence.dao.ExposureDAO;
import com.dev.auditiontech.persistence.dao.MaxVolumeDAO;
import com.dev.auditiontech.persistence.entity.AmbientVolume;
import com.dev.auditiontech.persistence.entity.Exposure;
import com.dev.auditiontech.persistence.entity.MaxVolume;

@Database(entities = {AmbientVolume.class, Exposure.class, MaxVolume.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AmbientVolumeDAO ambientVolumeDAO();
    public abstract ExposureDAO exposureDAO();
    public abstract MaxVolumeDAO maxVolumeDAO();
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };
    public static final Migration MIGRATION_2_3 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE exposure");
        }
    };
}
