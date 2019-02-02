package com.zcmedical.common.utils;

import hirondelle.date4j.DateTime;

import java.util.Calendar;
import java.util.TimeZone;

public class DateUtils {
    public static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    public DateUtils() {
        // TODO Auto-generated constructor stub
    }

    public static DateTime timestamp2DateTime(long timestamp) {
        return DateTime.forInstant(timestamp * 1000L, TimeZone.getDefault());
    }

    public static int dateTime2Timestamp(DateTime d) {
        return (int) (d.getMilliseconds(TimeZone.getDefault()) / 1000);
    }

    public static int dateTime2HoleDayTimestamp(DateTime d) {
        return trimToHoleDay(dateTime2Timestamp(d));
    }

    public static int trimToHoleDay(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp * 1000);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return (int) (cal.getTimeInMillis() / 1000);
    }

}
