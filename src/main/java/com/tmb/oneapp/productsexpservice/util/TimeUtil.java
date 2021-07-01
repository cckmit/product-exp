package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    private TimeUtil() {
    }

    private static final TMBLogger<TimeUtil> logger = new TMBLogger<>(TimeUtil.class);

    /**
     * Generic Method to validate available time for open portfolio
     *
     * @param argStartTime the start HHMM
     * @param argEndTime   the end HHMM
     * @return boolean
     */
    public static boolean isTimeBetweenTwoTime(String argStartTime,
                                               String argEndTime, String argCurrentTime) throws ParseException {
        String regex = "^([0-1][0-9]|2[0-3]):([0-5][0-9])$";
        if (argStartTime.matches(regex) && argEndTime.matches(regex) && argCurrentTime.matches(regex)) {
            boolean valid;
            // Start Time
            Calendar startCalendar = Calendar.getInstance();
            Date startTime = formatDateTime(argStartTime);
            startCalendar.setTime(startTime);

            // Current Time
            Calendar currentCalendar = Calendar.getInstance();
            Date currentTime = formatDateTime(argCurrentTime);
            currentCalendar.setTime(currentTime);

            // End Time
            Calendar endCalendar = Calendar.getInstance();
            Date endTime = formatDateTime(argEndTime);
            endCalendar.setTime(endTime);

            currentTime = compareTimeSlower(currentCalendar, currentTime, currentTime.compareTo(endTime) < 0);
            startTime = compareTimeSlower(startCalendar, startTime, startTime.compareTo(endTime) < 0);

            valid = compareTimeFaster(startTime, currentTime, endCalendar, endTime);
            return valid;
        } else {
            throw new IllegalArgumentException("Not a valid time, expecting HH:MM:SS format");
        }
    }

    private static boolean compareTimeFaster(Date startTime, Date currentTime, Calendar endCalendar, Date endTime) {
        boolean valid;
        if (currentTime.before(startTime)) {
            valid = false;
        } else {
            endTime = compareTimeSlower(endCalendar, endTime, currentTime.after(endTime));
            if (currentTime.before(endTime)) {
                valid = true;
            } else {
                valid = false;
            }
        }
        return valid;
    }

    private static Date compareTimeSlower(Calendar calendar, Date time, boolean comparing) {
        if (comparing) {
            calendar.add(Calendar.DATE, 1);
            time = calendar.getTime();
        }
        return time;
    }

    private static Date formatDateTime(String argStartTime) throws ParseException {
        return new SimpleDateFormat(ProductsExpServiceConstant.MF_TIME_WITH_COLON_HHMM).parse(argStartTime);
    }
}
