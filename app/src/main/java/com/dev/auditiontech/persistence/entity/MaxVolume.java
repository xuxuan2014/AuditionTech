package com.dev.auditiontech.persistence.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "max_volume")
public class MaxVolume {
    @PrimaryKey
    private long timestamp;

    private int volume;

    public MaxVolume(long timestamp, int volume) {
        this.timestamp = timestamp;
        this.volume = volume;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
