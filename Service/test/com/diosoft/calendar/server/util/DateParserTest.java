package com.diosoft.calendar.server.util;

import com.diosoft.calendar.server.exception.DateTimeFormatException;
import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class DateParserTest {

    @Test
    public void testStringToDate() throws DateTimeFormatException {

        String stringDate = "2015-01-01 00:00";
        LocalDateTime expectedDateTime = LocalDateTime.of(2015,1,1,0,0);

        LocalDateTime actualDateTime = DateParser.stringToDate(stringDate);

        assertEquals(expectedDateTime,actualDateTime);
    }

    @Test(expected = DateTimeFormatException.class)
    public void testStringToDateWrongFormat() throws DateTimeFormatException {

        String stringDate = "2015-1-1 00:00";
        LocalDateTime actualDateTime = DateParser.stringToDate(stringDate);
    }

    @Test
    public void testDateToString() throws DateTimeFormatException {

        LocalDateTime dateTime = LocalDateTime.of(2015,1,1,0,0);
        String expectedStrindDateTime = "2015-01-01 00:00";
        String actualDateTime = DateParser.dateToString(dateTime);

        assertEquals(expectedStrindDateTime,actualDateTime);

    }
}
