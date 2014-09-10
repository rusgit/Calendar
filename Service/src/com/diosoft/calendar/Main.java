package com.diosoft.calendar;

import com.diosoft.calendar.common.Event;
import com.diosoft.calendar.common.Person;
import com.diosoft.calendar.datastore.DataStore;
import com.diosoft.calendar.datastore.DataStoreImpl;
import com.diosoft.calendar.service.CalendarServiceImpl;
import com.diosoft.calendar.util.DateParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    public static void main(String[] args) {

// Create DataStore
        DataStore dataStore = new DataStoreImpl();
// Create CalendarServiceImpl
        CalendarServiceImpl calendarService = new CalendarServiceImpl(dataStore);

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
// Create List attenders
        List<Person> attenders = new ArrayList<Person>();
        attenders.add(person1);
        attenders.add(person2);
        attenders.add(person3);
// Create events
        String[] descriptions1 = {"Mega Party", "It will be a great party!", "2014, 9, 7, 15, 0", "2014, 9, 7, 19, 0"};
        Event event1 = calendarService.createEvent(descriptions1, attenders);
        String[] descriptions2 = {"Mega Party", "It will be a great party!", "2014, 9, 9, 13, 0", "2014, 9, 9, 18, 0"};
        Event event2 = calendarService.createEvent(descriptions2, attenders);
        String[] descriptions3 = {"New Year", "Happy New Year!", "2015, 1, 1, 0, 0", "2015, 1, 1, 0, 1"};
        Event event3 = calendarService.createEvent(descriptions3, attenders);
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