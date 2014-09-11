package com.diosoft.calendar.server.service;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.datastore.DataStore;
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
    public Event createEvent(String[] descriptions, List<Person> attenders) throws RemoteException, IllegalArgumentException {

        if (descriptions.length!=4) throw new IllegalArgumentException();

        LocalDateTime startDate = DateParser.StringToDate(descriptions[2]);
        LocalDateTime endDate = DateParser.StringToDate(descriptions[3]);

        Event event = new Event.EventBuilder()
                .id(UUID.randomUUID()).title(descriptions[0])
                .description(descriptions[1])
                .startDate(startDate)
                .endDate(endDate)
                .attendersList(attenders).build();

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
    public void remove(UUID id) throws RemoteException, IllegalArgumentException {
        if (id == null) throw new IllegalArgumentException();

        LOG.info("Removing event with id: '" + id + "'");
        dataStore.remove(id);
        LOG.info("Event successfully removed");
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
}
