package com.diosoft.calendar.server.util;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.PeriodOfEvent;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.exception.ValidationException;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class EventValidatorTest {

    @Test
    public void testValidate() throws DateTimeFormatException, ValidationException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.ONCE);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2020-10-15 15:00"))
                .endDate(DateParser.stringToDate("2020-10-15 20:00"))
                .periodSet(period)
                .attendersSet(attendersTest).build();

        EventValidator.validate(event1);
    }

    @Test
    public void testValidateWithNullAttenders() throws DateTimeFormatException {
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.ONCE);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2020-10-15 15:00"))
                .endDate(DateParser.stringToDate("2020-10-15 20:00"))
                .periodSet(period)
                .attendersSet(null).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("Null value of Attenders of event", e.getMessage());
        }
    }

    @Test
    public void testValidateWithNullTitleOfEvent() throws DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.ONCE);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title(null)
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2020-10-15 15:00"))
                .endDate(DateParser.stringToDate("2020-10-15 20:00"))
                .periodSet(period)
                .attendersSet(attendersTest).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("Null value of Title of event", e.getMessage());
        }
    }

    @Test
    public void testValidateWithNotSpecifiedTitle() throws DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.ONCE);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2020-10-15 15:00"))
                .endDate(DateParser.stringToDate("2020-10-15 20:00"))
                .periodSet(period)
                .attendersSet(attendersTest).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("Not specified event title", e.getMessage());
        }
    }

    @Test
    public void testValidateWithNotSpecifiedDescription() throws DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.ONCE);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("")
                .startDate(DateParser.stringToDate("2020-10-15 15:00"))
                .endDate(DateParser.stringToDate("2020-10-15 20:00"))
                .periodSet(period)
                .attendersSet(attendersTest).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("Not specified event description", e.getMessage());
        }
    }

    @Test
    public void testValidateWithStartDateAfterEndDate() throws DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.ONCE);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2020-10-15 15:00"))
                .endDate(DateParser.stringToDate("2020-10-15 10:00"))
                .periodSet(period)
                .attendersSet(attendersTest).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("startDate after endDate", e.getMessage());
        }
    }

    @Test
    public void testValidateWithStartDateBeforeCurrent() throws DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.ONCE);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2012-10-15 15:00"))
                .endDate(DateParser.stringToDate("2015-10-15 10:00"))
                .periodSet(period)
                .attendersSet(attendersTest).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("startDate before current date", e.getMessage());
        }
    }

    @Test
    public void testValidateWithStartDateNotEqualsDayOfWeekPeriod() throws DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.MONDAY);
        period.add(PeriodOfEvent.WEDNESDAY);
        period.add(PeriodOfEvent.FRIDAY);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2015-10-21 15:00"))
                .endDate(DateParser.stringToDate("2015-10-21 19:00"))
                .periodSet(period)
                .attendersSet(attendersTest).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("Start date not equals day of week in period", e.getMessage());
        }
    }

    @Test
    public void testValidateWithNullPeriod() throws DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2015-10-15 15:00"))
                .endDate(DateParser.stringToDate("2015-10-15 10:00"))
                .periodSet(null)
                .attendersSet(attendersTest).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("Null value of Period of event", e.getMessage());
        }
    }

    @Test
    public void testValidateWithStartDateNotEqualsEndDateInEveryDayEvent() throws DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.EVERY_DAY);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2015-10-15 15:00"))
                .endDate(DateParser.stringToDate("2015-10-16 10:00"))
                .periodSet(period)
                .attendersSet(attendersTest).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("Start and End date in EveryDay event not equal", e.getMessage());
        }
    }

    @Test
    public void testValidateWithStartDateNotEqualsEndDateInEventWithDayOfWeekPeriod() throws DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<>();
        attendersTest.add(attender);
        Set<PeriodOfEvent> period = new HashSet<>();
        period.add(PeriodOfEvent.MONDAY);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2015-10-14 15:00"))
                .endDate(DateParser.stringToDate("2015-10-15 10:00"))
                .periodSet(period)
                .attendersSet(attendersTest).build();

        try {
            EventValidator.validate(event1);
        } catch (ValidationException e) {
            assertEquals("Start and End date in event with DayOfWeek period not equal", e.getMessage());
        }
    }


}
