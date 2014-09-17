package com.diosoft.calendar.server.service;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.datastore.DataStore;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.exception.OrderOfArgumentsException;
import com.diosoft.calendar.server.util.DateParser;
import org.apache.log4j.Logger;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CalendarServiceImpl implements CalendarService {

    private static final Logger LOG = Logger.getLogger(CalendarServiceImpl.class);
    private final DataStore dataStore;

    public CalendarServiceImpl(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public Event createAndAdd(String[] descriptions, List<Person> attenders) throws RemoteException, IllegalArgumentException, DateTimeFormatException {

        if (descriptions.length!=4) throw new IllegalArgumentException();

        LocalDateTime startDate = DateParser.stringToDate(descriptions[2]);
        LocalDateTime endDate = DateParser.stringToDate(descriptions[3]);

        LOG.info("Creating event with title '" + descriptions[0] + "'");
        Event event = new Event.EventBuilder()
                .id(UUID.randomUUID()).title(descriptions[0])
                .description(descriptions[1])
                .startDate(startDate)
                .endDate(endDate)
                .attendersList(attenders).build();
        LOG.info("Event successfully created");

        LOG.info("Adding event with title '" + descriptions[0] + "'");
        dataStore.publish(event);
        LOG.info("Event successfully added");

        return event;
    }

    @Override
    public void add(Event event) throws RemoteException, IllegalArgumentException {
        if (event == null) throw new IllegalArgumentException();

        LOG.info("Adding event with title '" + event.getTitle() + "'");
        dataStore.publish(event);
        LOG.info("Event successfully added");
    }

    @Override
    public Event remove(UUID id) throws RemoteException, IllegalArgumentException {
        if (id == null) throw new IllegalArgumentException();

        LOG.info("Removing event with id: '" + id + "'");
        Event event = dataStore.remove(id);
        if (event==null) {
            LOG.info("There is no such Event");
        } else {
            LOG.info("Event successfully removed");
        }
        return event;
    }

    @Override
    public List<Event> searchByTitle(String title) throws RemoteException, IllegalArgumentException {
        if (title == null) throw new IllegalArgumentException();

        LOG.info("Searching by title '" + title +"':");
        List<Event> events = dataStore.getEventByTitle(title);
        if (events.size()<1) {
            LOG.info("Events not found!");
            return events;
        }
        LOG.info("found " + events.size()+ " events");

        return events;
    }

    @Override
    public List<Event> searchByDay(LocalDate day) throws RemoteException, IllegalArgumentException {
        if (day == null) throw new IllegalArgumentException();

        LOG.info("Searching by day '" + day +"':");
        List<Event> events = dataStore.getEventByDay(day);
        if (events.size()<1) {
            LOG.info("Events not found!");
            return events;
        }
        LOG.info("found " + events.size()+ " events");

        return events;
    }

    @Override
    public List<Event> searchByAttender(Person attender) throws RemoteException, IllegalArgumentException {
        if (attender == null) throw new IllegalArgumentException();

        LOG.info("Searching by attender '" + attender.getName() +"':");
        List<Event> events = dataStore.getEventByAttender(attender);
        if (events.size()<1) {
            LOG.info("Events not found!");
            return events;
        }
        LOG.info("found " + events.size()+ " events");

        return events;
    }

    @Override
    public boolean isAttenderFree(Person attender, LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, IllegalArgumentException, OrderOfArgumentsException {
        if (attender == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        LOG.info("Checking is attender '" + attender.getName() + " " + attender.getLastName() + "' free from " + startDate + " to " + endDate);
        List<Event> eventListByAttender = searchByAttender(attender);
        for (Event event : eventListByAttender) {
            if (event.getStartDate().isAfter(startDate) && event.getStartDate().isBefore(endDate) ||
                    event.getEndDate().isAfter(startDate) && event.getEndDate().isBefore(endDate) ||
                    event.getStartDate().isBefore(startDate) && event.getEndDate().isAfter(endDate)) {
                LOG.info("Attender not free");
                return false;
            }
        }
        LOG.info("Attender free");

        return true;
    }
}
