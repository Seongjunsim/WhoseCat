package com.jmsmart.whosecat.data.commondata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormat {
    public final static String CALENDAR_HEADER_FORMAT = "yyyy년 MM월";
    public final static String MONTH_DAY_FORMAT = "MM월 dd일";
    public final static String DAY_FORMAT = "d";

    public static String getDate(long date, String pattern) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);
            Date d = new Date(date);
            return formatter.format(d).toUpperCase();
        } catch (Exception e) {
            return " ";
        }
    }

}