package com.diosoft.calendar.server.util;

import java.time.LocalDateTime;

public class DateParser {

    public static LocalDateTime StringToDate(String date) {

        String separator = ", ";
        String[] tempStrings = date.split(separator);

        if (tempStrings.length != 5) {
            throw new IllegalArgumentException("Illegate date format");
        }

        int year = Integer.valueOf(tempStrings[0]);
        int month = Integer.valueOf(tempStrings[1]);
        int day = Integer.valueOf(tempStrings[2]);
        int hour = Integer.valueOf(tempStrings[3]);
        int minute = Integer.valueOf(tempStrings[4]);

        return LocalDateTime.of(year,month,day,hour,minute);
    }

    public static String DateToString(LocalDateTime date) {

        String strindDate = date.getYear() + ", "
                + date.getMonthValue() + ", "
                + date.getDayOfMonth() + ", "
                + date.getHour() + ", "
                + date.getMinute();

        return strindDate;
    }


}
