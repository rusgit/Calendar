package com.diosoft.calendar.server.util;

import com.diosoft.calendar.server.exception.DateTimeFormatException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {

    public static LocalDateTime stringToDate(String stringDate) throws DateTimeFormatException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = null;

        try {
            dateTime = LocalDateTime.parse(stringDate, formatter);
        } catch (DateTimeParseException e) {
            throw new DateTimeFormatException("Wrong format of date/time");
        }

        return dateTime;
    }

    public static String dateToString(LocalDateTime dateTime) throws DateTimeFormatException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String stringDate = null;
        try {
            stringDate = dateTime.format(formatter);
        } catch (DateTimeParseException e) {
            throw new DateTimeFormatException("Wrong format of date/time");
        }

        return stringDate;
    }


}
