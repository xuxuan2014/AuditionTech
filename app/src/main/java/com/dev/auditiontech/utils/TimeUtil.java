package com.dev.auditiontech.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtil {
    private static Calendar calendar = Calendar.getInstance();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    public static String getDate() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return simpleDateFormat.format(calendar.getTime());
    }

    public static Integer getHour() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static Integer getMinute()    {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.MINUTE);
    }

    public static Integer getSecond() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.SECOND);
    }

    public static Integer getSecondsWithinDay() {
        return getHour() * 3600 + getMinute() * 60 + getSecond();
    }
}
