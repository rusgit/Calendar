package com.diosoft.calendar.server.util;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.PeriodOfEvent;
import com.diosoft.calendar.server.exception.ValidationException;

import java.time.LocalDateTime;

import static com.diosoft.calendar.server.common.PeriodOfEvent.*;

public class EventValidator {

    public static void validate(Event event) throws IllegalArgumentException, ValidationException {
//null check
        if (event == null) throw new IllegalArgumentException();
        if (event.getId() == null) throw new ValidationException("Null value of Id of event");
        if (event.getTitle() == null) throw new ValidationException("Null value of Title of event");
        if (event.getDescription() == null) throw new ValidationException("Null value of Description of event");
        if (event.getStartDate() == null) throw new ValidationException("Null value of StartDate of event");
        if (event.getEndDate() == null) throw new ValidationException("Null value of EndDate of event");
        if (event.getAttenders() == null) throw new ValidationException("Null value of Attenders of event");
        if (event.getPeriod() == null) throw new ValidationException("Null value of Period of event");

//not specified
        if (event.getTitle().length() == 0) throw new ValidationException("Not specified event name");
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
}
