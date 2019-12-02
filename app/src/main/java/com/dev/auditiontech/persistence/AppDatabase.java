package com.dev.auditiontech.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dev.auditiontech.persistence.dao.AmbientVolumeDAO;
import com.dev.auditiontech.persistence.dao.ExposureDAO;
import com.dev.auditiontech.persistence.dao.MaxVolumeDAO;
import com.dev.auditiontech.persistence.entity.AmbientVolume;
import com.dev.auditiontech.persistence.entity.Exposure;
import com.dev.auditiontech.persistence.entity.MaxVolume;

@Database(entities = {AmbientVolume.class, Exposure.class, MaxVolume.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AmbientVolumeDAO ambientVolumeDAO();
    public abstract ExposureDAO exposureDAO();
    public abstract MaxVolumeDAO maxVolumeDAO();
}
