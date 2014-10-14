package com.diosoft.calendar.server.util;

import com.diosoft.calendar.server.exception.DateTimeFormatException;
import org.apache.log4j.Logger;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {
    private final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

    private static final Logger logger = Logger.getLogger(DateParser.class);

    public static LocalDateTime stringToDate(String stringDate) throws IllegalArgumentException, DateTimeFormatException {

        if (stringDate==null) throw new IllegalArgumentException();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        LocalDateTime dateTime;

        try {
            dateTime = LocalDateTime.parse(stringDate, formatter);
        } catch (DateTimeParseException dtpe) {
            logger.error(dtpe.getMessage());
            throw new DateTimeFormatException("Wrong format of date/time");
        }

        return dateTime;
    }

    public static String dateToString(LocalDateTime dateTime) throws DateTimeException, IllegalArgumentException {

        if (dateTime==null) throw new IllegalArgumentException();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

        try {
            return dateTime.format(formatter);
        } catch (DateTimeException dte) {
            logger.error(dte.getMessage());
            throw new DateTimeException("Wrong value of year, month, day, hour or minute");
        }
    }
}
