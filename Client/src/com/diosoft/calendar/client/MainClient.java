package com.diosoft.calendar.client;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.service.CalendarService;
import com.diosoft.calendar.server.util.DateParser;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainClient {
    private static final Logger LOG = Logger.getLogger(MainClient.class);

    public static void main(String[] args) throws RemoteException, DateTimeFormatException {

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
// Create List attenders
        List<Person> attenders = new ArrayList<Person>();
        LOG.info("Addind 3 attenders...");
        attenders.add(person1);
        attenders.add(person2);
        attenders.add(person3);
        LOG.info("Added 3 attenders.");
// Create events
        String[] descriptions1 = {"Mega Party", "It will be a great party!", "2014-09-07 15:00", "2014-09-07 19:00"};
        Event event1 = calendarService.createEvent(descriptions1, attenders);
        String[] descriptions2 = {"Mega Party", "It will be a great party!", "2014-09-09 13:00", "2014-09-09 18:00"};
        Event event2 = calendarService.createEvent(descriptions2, attenders);
        String[] descriptions3 = {"New Year", "Happy New Year!", "2015-01-01 00:00", "2015-01-01 00:01"};
        Event event3 = calendarService.createEvent(descriptions3, attenders);
// add events
        LOG.info("Addind 3 events...");
        calendarService.add(event1);
        calendarService.add(event2);
        calendarService.add(event3);
        LOG.info("Added 3 events.");
// remove event
        LOG.info("Removing event...");
        calendarService.remove(event3.getId());
        LOG.info("Removed event.");
// searchByTitle
        LOG.info("Searching event by title 'Mega Party':");
        List<Event> events1 = calendarService.searchByTitle("Mega Party");
        for (Event event: events1) {
            System.out.println(event);
        }
// searchByDate
        LOG.info("Searching event by day '2014-09-07':");
        List<Event> events2 = calendarService.searchByDay(LocalDate.of(2014, 9, 7));
        for (Event event: events2) {
            System.out.println(event);
        }
    }
}
