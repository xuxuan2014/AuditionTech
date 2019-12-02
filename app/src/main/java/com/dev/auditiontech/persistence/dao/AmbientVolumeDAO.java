package com.dev.auditiontech.persistence.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dev.auditiontech.persistence.entity.AmbientVolume;

import java.util.List;

@Dao
public interface AmbientVolumeDAO {
    @Query("SELECT * FROM ambient_volume")
    List<AmbientVolume> getAll();

    @Query("SELECT * FROM ambient_volume WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp")
    List<AmbientVolume> getVolumeWithin(long startTimestamp, long endTimestamp);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAmbientVolume(AmbientVolume ambientVolume);

    @Delete
    void deleteAmbientVolume(AmbientVolume... ambientVolumes);

}
