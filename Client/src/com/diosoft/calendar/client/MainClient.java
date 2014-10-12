package com.diosoft.calendar.client;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.PeriodOfEvent;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.exception.OrderOfArgumentsException;
import com.diosoft.calendar.server.exception.ValidationException;
import com.diosoft.calendar.server.service.CalendarService;
import com.diosoft.calendar.server.util.DateParser;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.log4j.Logger.*;

public class MainClient {
    private static final Logger logger = getLogger(MainClient.class);

        //local code review (vtegza): extract functionality to separated class @ 12.10.14
    public static void main(String[] args) throws IOException, DateTimeFormatException, OrderOfArgumentsException, ValidationException, JAXBException {

        ApplicationContext factory = new ClassPathXmlApplicationContext("app-context-client.xml");
        CalendarService calendarService = (CalendarService) factory.getBean("calendarService");

// Create person1 (attender)
        Person person1 = new Person.PersonBuilder()
                .name("Alexandr").lastName("Alexandrenko")
                .email("alex_alex@ukr.net")
                .build();

// Create person2 (attender)
        Person person2 = new Person.PersonBuilder()
                .name("Igor").lastName("Igorov")
                .email("igor_igor@ukr.net")
                .build();

// Create person3 (attender)
        Person person3 = new Person.PersonBuilder()
                .name("Sergey").lastName("Sergeev")
                .email("sergey_sergey@ukr.net")
                .build();

// Create Set attenders
        Set<Person> attenders = new HashSet<>();
        logger.info("Addind 3 attenders...");
        attenders.add(person1);
        attenders.add(person2);
        attenders.add(person3);
        logger.info("Added 3 attenders.");

// Create Sets of periods
        Set<PeriodOfEvent> periodOnce = new HashSet<>();
        periodOnce.add(PeriodOfEvent.ONCE);

        Set<PeriodOfEvent> periodEveryDay = new HashSet<>();
        periodEveryDay.add(PeriodOfEvent.EVERY_DAY);

        Set<PeriodOfEvent> periodEveryMonth = new HashSet<>();
        periodEveryMonth.add(PeriodOfEvent.EVERY_MONTH);

        Set<PeriodOfEvent> periodEveryYear = new HashSet<>();
        periodEveryYear.add(PeriodOfEvent.EVERY_YEAR);

        Set<PeriodOfEvent> periodDayOfWeek = new HashSet<>();
        periodDayOfWeek.add(PeriodOfEvent.TUESDAY);
        periodDayOfWeek.add(PeriodOfEvent.THURSDAY);

// Create and add events
        logger.info("Creating and adding event...");
        String[] descriptions1 = {"Mega Party", "It will be a great party!", "2020-09-07 15:00", "2020-09-07 19:00"};
        Event event1 = calendarService.createEvent(descriptions1, attenders, periodOnce);
        logger.info("event created and added.");

        logger.info("Creating and adding event...");
        String[] descriptions2 = {"Mega Party 2", "It will be a second great party!", "2020-09-09 13:00", "2020-09-09 18:00"};
        Event event2 = calendarService.createEvent(descriptions2, attenders, periodOnce);
        logger.info("event created and added.");

        logger.info("Creating and adding event...");
        String[] descriptions3 = {"New Year", "Happy New Year!", "2019-12-31 22:00", "2020-01-01 02:00"};
        Event event3 = calendarService.createEvent(descriptions3, attenders, periodEveryYear);
        logger.info("event created and added.");

        logger.info("Creating and adding EveryDay event...");
        String[] descriptionsDay = {"EveryDay", "Every day event", "2020-01-01 09:00", "2020-01-01 12:00"};
        Event event4 = calendarService.createEvent(descriptionsDay, attenders, periodEveryDay);
        logger.info("EveryDay event created and added.");

        logger.info("Creating and adding EveryMonth event...");
        String[] descriptionsMonth = {"Apartment rent", "Pay the rent", "2020-10-20 10:00", "2020-10-20 12:00"};
        Event event5 = calendarService.createEvent(descriptionsMonth, attenders, periodEveryMonth);
        logger.info("EveryMonth event created and added.");

        logger.info("Creating and adding EveryWeek event...");
        String[] descriptionsWeek = {"Gym", "Gym", "2020-10-20 18:00", "2020-10-20 20:00"};
        Event event6 = calendarService.createEvent(descriptionsWeek, attenders, periodDayOfWeek);
        logger.info("EveryWeek event created and added.");

// remove event
        logger.info("Removing event...");
        calendarService.remove(event3.getId());
        logger.info("Event removed");

// searchByTitle
        logger.info("Searching event by title 'Mega Party':");
        List<Event> events1 = calendarService.searchByTitle("Mega Party");
        events1.forEach(System.out::println);

// searchByDate
        logger.info("Searching event by day '2020-09-07':");
        List<Event> events2 = calendarService.searchByDay(LocalDate.of(2020, 9, 7));
        events2.forEach(System.out::println);

// searchByAttender
        logger.info("Searching event by attender 'Alexandr':");
        List<Event> events3 = calendarService.searchByAttender(person1);
        events3.forEach(System.out::println);

// searchByAttenderIntoPeriod
        logger.info("Searching event by attender 'Alexandr' from 2020-09-07 12:00 to 2020-09-09 16:00:");
        List<Event> events4 = calendarService.searchByAttenderIntoPeriod(person1, DateParser.stringToDate("2020-09-07 12:00"), DateParser.stringToDate("2020-09-09 16:00"));
        events4.forEach(System.out::println);

// isAttenderFree
        logger.info("Checking is attender 'Alexandr' free from 2020-09-07 19:00 to 2020-09-09 13:00:");
        boolean isFree = calendarService.isAttenderFree(person1, DateParser.stringToDate("2020-09-07 19:00"), DateParser.stringToDate("2020-09-09 13:00"));
        System.out.println(isFree?"Free":"Not free");

// Create event "for all day"
        logger.info("Creating event 'for all day':");
        String[] descriptions4 = {"Mega Party", "It will be a great party!", "2020-09-07"};
        Event event = calendarService.createEventForAllDay(descriptions4, attenders, periodOnce);
        System.out.println(event);

// SearchFreeTime1 into period
        logger.info("SearchFreeTime1 into period from 2020-09-08 12:00 to 2020-09-10 21:00");
        List<List<LocalDateTime>> freeTimeIntervalList1 = calendarService.searchFreeTime(DateParser.stringToDate("2020-09-08 12:00"), DateParser.stringToDate("2020-09-10 21:00"));
        freeTimeIntervalList1.forEach(System.out::println);

// SearchFreeTime2 into period
        logger.info("SearchFreeTime2 into period from 2020-09-08 12:00 to 2020-09-10 21:00");
        List<List<LocalDateTime>> freeTimeIntervalList2 = calendarService.searchFreeTime2(DateParser.stringToDate("2020-09-08 12:00"), DateParser.stringToDate("2020-09-10 21:00"));
        freeTimeIntervalList2.forEach(System.out::println);

// SearchFreeTimeForEvent into period
        logger.info("SearchFreeTime for Event into period:");
        List<List<LocalDateTime>> freeTimeForEventIntervalList = calendarService.searchFreeTimeForEvent(event1, DateParser.stringToDate("2020-09-08 12:00"), DateParser.stringToDate("2020-09-10 21:00"));
        freeTimeForEventIntervalList.forEach(System.out::println);

// Edit event
        Event eventForEdit = calendarService.searchByTitle("Edited event").get(0);
        logger.info("Edit event id=" + eventForEdit.getId());
        Event eventEdited = new Event.EventBuilder()
                .id(eventForEdit.getId()).title(eventForEdit.getTitle())
                .description(eventForEdit.getDescription() + "!")
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusHours(2))
                .periodSet(eventForEdit.getPeriod())
                .attendersSet(attenders).build();
        calendarService.edit(eventEdited);
        logger.info("event edited");

// SearchFreeTimeForEventWithAttenders into period
        logger.info("SearchFreeTime for Event with attenders into period:");
        List<List<LocalDateTime>> freeTimeForEventWithAttenders = calendarService.searchFreeTimeForEventWithAttenders(event2, DateParser.stringToDate("2020-09-08 12:00"), DateParser.stringToDate("2020-09-10 21:00"));
        freeTimeForEventWithAttenders.forEach(System.out::println);
    }
}
