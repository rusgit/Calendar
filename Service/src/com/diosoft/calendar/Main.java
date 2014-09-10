package com.diosoft.calendar;

import com.diosoft.calendar.common.Event;
import com.diosoft.calendar.common.Person;
import com.diosoft.calendar.datastore.DataStore;
import com.diosoft.calendar.datastore.DataStoreImpl;
import com.diosoft.calendar.service.CalendarServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                .startDate(LocalDateTime.of(2014,9,7,15,0))
                .endDate(LocalDateTime.of(2014,9,7,19,0))
                .attendersList(attenders).build();

// Create event2
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Mega Party")
                .description("It will be a great party!")
                .startDate(LocalDateTime.of(2014,9,7,15,0))
                .endDate(LocalDateTime.of(2014,9,7,18,0))
                .attendersList(attenders).build();

// Create event3
        Event event3 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("New Year")
                .description("It will be a great party!")
                .startDate(LocalDateTime.of(2015,1,1,0,0))
                .endDate(LocalDateTime.of(2015,1,1,0,1))
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
// remove event
        calendarService.remove(event3.getId());
// searchByTitle
        calendarService.searchByTitle("Mega Party");
// searchByDate
        calendarService.searchByDay(LocalDate.of(2014,9,7));
    }
}