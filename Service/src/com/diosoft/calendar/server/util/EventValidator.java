package com.diosoft.calendar.server.util;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.PeriodOfEvent;
import com.diosoft.calendar.server.exception.ValidationException;

import java.time.LocalDateTime;

import static com.diosoft.calendar.server.common.PeriodOfEvent.*;

public class EventValidator {
    private final static String ID = "Id";
    private final static String TITLE = "Title";
    private final static String DESCRIPTION = "Description";
    private final static String START_DATE = "StartDate";
    private final static String END_DATE = "EndDate";
    private final static String ATTENDERS = "Attenders";
    private final static String PERIOD = "Period";

    public static void validate(Event event) throws IllegalArgumentException, ValidationException {
//null check
        if (event == null) throw new IllegalArgumentException();
        checkMandatoryField(event.getId(), ID);
        checkMandatoryField(event.getTitle(), TITLE);
        checkMandatoryField(event.getDescription(), DESCRIPTION);
        checkMandatoryField(event.getStartDate(), START_DATE);
        checkMandatoryField(event.getEndDate(), END_DATE);
        checkMandatoryField(event.getAttenders(), ATTENDERS);
        checkMandatoryField(event.getPeriod(), PERIOD);

//not specified
        if (event.getTitle().length() == 0) throw new ValidationException("Not specified event title");
        if (event.getDescription().length() == 0) throw new ValidationException("Not specified event description");

//mistakes of logic
        if (event.getStartDate().isAfter(event.getEndDate())) throw new ValidationException("startDate after endDate");
        if (event.getStartDate().isBefore(LocalDateTime.now())) throw new ValidationException("startDate before current date");

        if (event.getPeriod().contains(EVERY_DAY) && !event.getStartDate().toLocalDate().isEqual(event.getEndDate().toLocalDate()))
            throw new ValidationException("Start and End date in EveryDay event not equal");

        if ((event.getPeriod().contains(MONDAY) || event.getPeriod().contains(TUESDAY) || event.getPeriod().contains(WEDNESDAY)
                || event.getPeriod().contains(THURSDAY) || event.getPeriod().contains(FRIDAY) || event.getPeriod().contains(SATURDAY)
                || event.getPeriod().contains(SUNDAY)) && !event.getStartDate().toLocalDate().isEqual(event.getEndDate().toLocalDate()))
            throw new ValidationException("Start and End date in event with DayOfWeek period not equal");

        if ((event.getPeriod().contains(MONDAY) || event.getPeriod().contains(TUESDAY) || event.getPeriod().contains(WEDNESDAY)
                || event.getPeriod().contains(THURSDAY) || event.getPeriod().contains(FRIDAY) || event.getPeriod().contains(SATURDAY)
                || event.getPeriod().contains(SUNDAY))) {
            boolean isEqual = false;
            for (PeriodOfEvent period : event.getPeriod())
                if (period.name().equals(event.getStartDate().getDayOfWeek().name())) {
                    isEqual = true;
                    break;
                }
            if (!isEqual) throw new ValidationException("Start date not equals day of week in period");
        }
    }

    private static void checkMandatoryField(Object value, String fieldName) throws ValidationException {
        if (value == null) throw new ValidationException("Null value of " + fieldName + " of event");
    }
}
