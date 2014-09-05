package com.diosoft.calendar;

import com.diosoft.calendar.common.Event;
import com.diosoft.calendar.common.Person;
import com.diosoft.calendar.datastore.DataStore;
import com.diosoft.calendar.datastore.DataStoreImpl;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

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
                .id().title("Mega Party")
                .description("It will be a great party!")
                .startDate(new GregorianCalendar(2014, 9, 10))
                .endDate(new GregorianCalendar(2014, 10, 11))
                .attendersList(attenders).build();

// Create event2
        Event event2 = new Event.EventBuilder()
                .id().title("Mega Part")
                .description("It will be a great party!")
                .startDate(new GregorianCalendar(2014, 9, 10))
                .endDate(new GregorianCalendar(2014, 10, 11))
                .attendersList(attenders).build();

// Create DateStore

   DataStore dataStore = new DataStoreImpl();

// Test add event
        dataStore.publish(event1);
        Event eventTest1 = dataStore.getEventById(event1.getId());
        System.out.println("Added event");
        System.out.println(eventTest1);

// Test remove event
        dataStore.remove(event1.getId());
        Event eventTest2 = dataStore.getEventById(event1.getId());
        System.out.println("Remove event");
        System.out.println(eventTest2);

// Test search by title
       dataStore.publish(event1);
       dataStore.publish(event2);
       System.out.println("Search by title:");
       List<Event> ev2 = dataStore.getEventByTitle("Mega Party");
       for (Event e:ev2) {
           System.out.println(e);
       }

    }
}