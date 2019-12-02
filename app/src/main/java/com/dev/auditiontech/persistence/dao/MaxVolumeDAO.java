package com.dev.auditiontech.persistence.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dev.auditiontech.persistence.entity.MaxVolume;

import java.util.List;

@Dao
public interface MaxVolumeDAO {
    @Query("SELECT * FROM max_volume")
    List<MaxVolume> getAll();

    @Query("SELECT * FROM max_volume WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp")
    List<MaxVolume> getMaxVolumeWithin(long startTimestamp, long endTimestamp);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMaxVolume(MaxVolume maxVolume);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllMaxVolumes(MaxVolume... maxVolumes);

    @Delete
    void deleteMaxVolume(MaxVolume maxVolume);

    @Delete
    void deleteMaxVolumes(MaxVolume... maxVolumes);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMaxVolume(MaxVolume maxVolume);

    @Query("SELECT * FROM max_volume WHERE timestamp = :timestamp")
    MaxVolume getMaxVolume(long timestamp);
}
