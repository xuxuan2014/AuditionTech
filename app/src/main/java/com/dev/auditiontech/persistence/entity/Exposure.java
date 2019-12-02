package com.dev.auditiontech.persistence.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exposure")
public class Exposure {
    @PrimaryKey
    private long timestamp;

    private double exposure;
    private long seconds;

    public Exposure(long timestamp, double exposure, long seconds) {
        this.timestamp = timestamp;
        this.exposure = exposure;
        this.seconds = seconds;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }
}
