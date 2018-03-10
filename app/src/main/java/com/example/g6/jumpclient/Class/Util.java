package com.example.g6.jumpclient.Class;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by g6 on 10-Mar-18.
 */

public class Util {
    public static final String SGTIME = "GMT+8";
    public static String getTimeString(Long unixSeconds) {
        // convert seconds to milliseconds
        Date date = new Date(unixSeconds);
        // the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm:ss   dd-MM-yy");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(TimeZone.getTimeZone("Singapore"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
