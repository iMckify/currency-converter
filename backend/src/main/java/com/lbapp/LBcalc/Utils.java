package com.lbapp.LBcalc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Date longToDate(long timestamp) {
        int len = String.valueOf(timestamp).length();
        ZonedDateTime zdt;
        if (len == 9 || len == 10) {
            zdt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("EST", ZoneId.SHORT_IDS));
        } else if (len == 13) {
            zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("EST", ZoneId.SHORT_IDS));
        } else {
            zdt = ZonedDateTime.now();
        }

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        String dateReadable = zdt.format(formatter2);

        return strToDate(dateReadable);
    }

    public static Date strToDate(String str) {
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static long dateStringToEpochEST(String dateString) {
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        Date date = new Date();
        try {
            date = sdf.parse(dateString);
        } catch (ParseException ignored) {}
        return date.toInstant().toEpochMilli();
    }
}
