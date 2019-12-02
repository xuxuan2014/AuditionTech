package com.dev.auditiontech.utils;

public class AmbientVolumeUtil {
    public static String getPrompt(int db) {
        if (db < 80) {
            return "You are in a safe listening environment.";
        } else if (80 <= db && db < 85) {
            return "Please leave in 7-8 hours/take hearing protection measures/lower the volume or your hearing could be damaged.";
        } else if (db >= 85 && db < 100) {
            return "Please leave in 15 minutes/take hearing protection measures/lower the volume or your hearing could be damaged";
        }
        return "Please leave in 2 minutes/take hearing protection measures/lower the volume or your hearing could be damaged";


    }

    public static double decibalToPascal(int dB) {
        double p0 = 2 * Math.pow(10, -5);
        return p0 * Math.pow(10, ((double)dB)/20);
    }
}
