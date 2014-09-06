package com.diosoft.calendar;

import com.diosoft.calendar.common.Event;
import com.diosoft.calendar.common.Person;
import com.diosoft.calendar.datastore.DataStore;
import com.diosoft.calendar.datastore.DataStoreImpl;
import com.diosoft.calendar.service.CalendarServiceImpl;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import java.util.*;

public class Main {

    public static void main(String[] args) {

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

        List<Person> attenders = new ArrayList<Person>();
        attenders.add(person1);
        attenders.add(person2);

// Create event1
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Mega Party")
                .description("It will be a great party!")
                .startDate(new LocalDateTime().withDate(2014, 9, 7).withTime(12, 15, 12, 0))
                .endDate(new LocalDateTime().withDate(2014, 9, 7).withTime(15, 15, 12, 0))
                .attendersList(attenders).build();

// Create event2
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Mega Party")
                .description("It will be a great party!")
                .startDate(new LocalDateTime().withDate(2014,9,7).withTime(12,15,12,0))
                .endDate(new LocalDateTime().withDate(2014,9,7).withTime(15,15,12,0))
                .attendersList(attenders).build();

// Create event3
        Event event3 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Ney Year")
                .description("It will be a great party!")
                .startDate(new LocalDateTime().withDate(2015,1,1).withTime(0,0,0,0))
                .endDate(new LocalDateTime().withDate(2015,1,1).withTime(0,0,1,0))
                .attendersList(attenders).build();

// Test CalendarServiceImpl
// Create DataStore
        DataStore dataStore = new DataStoreImpl();
// Create CalendarServiceImpl
        CalendarServiceImpl calendarService = new CalendarServiceImpl(dataStore);
// add events
        calendarService.add(event1);
        calendarService.add(event2);
        calendarService.add(event3);
        System.out.println("Added 3 events");
        System.out.println(dataStore);
// remove event
        calendarService.remove(event3.getId());
        System.out.println("Remove 1 event");
        System.out.println(dataStore);
// searchByTitle
        List<Event> eventsByTitle = calendarService.searchByTitle("Mega Party");
        System.out.println("searchByTitle");
        System.out.println(eventsByTitle);
// searchByDate
        List<Event> eventsByDate = calendarService.searchByDay(new LocalDate(2014,9,7));
        System.out.println("searchByDate");
        System.out.println(eventsByDate);
    }
}