package com.dev.auditiontech.persistence.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dev.auditiontech.persistence.entity.Exposure;

import java.util.List;

@Dao
public interface ExposureDAO {
    @Query("SELECT * FROM exposure")
    List<Exposure> getAll();

    @Query("SELECT * FROM exposure WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ")
    List<Exposure> getExposureBetween(long startTimestamp, long endTimestamp);

    @Query("SELECT * FROM exposure WHERE timestamp = :timestamp")
    Exposure getExposure(long timestamp);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateExposure(Exposure exposure);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertExposure(Exposure exposure);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertExposures(Exposure... exposures);
}
