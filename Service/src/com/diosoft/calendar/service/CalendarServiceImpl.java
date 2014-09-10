package com.diosoft.calendar.service;

import com.diosoft.calendar.common.Event;
import com.diosoft.calendar.datastore.DataStore;
import org.apache.log4j.Logger;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CalendarServiceImpl implements CalendarService {

    private static final Logger LOG = Logger.getLogger(CalendarServiceImpl.class);
    private final DataStore dataStore;

    public CalendarServiceImpl(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void add(Event event) {
        if (event == null) throw new IllegalArgumentException();

        LOG.info("Adding event with title '" + event.getTitle() + "'");
        dataStore.publish(event);
        LOG.info("Event successfully added");
    }

    @Override
    public void remove(UUID id) {
        if (id == null) throw new IllegalArgumentException();

        LOG.info("Removing event with id: '" + id + "'");
        dataStore.remove(id);
        LOG.info("Event successfully removed");
    }

    @Override
    public List<Event> searchByTitle(String title) {
        if (title == null) throw new IllegalArgumentException();

        LOG.info("Searching by title '" + title +"':");
        List<Event> events = dataStore.getEventByTitle(title);
        if (events.size()<1) {
            LOG.info("Events not found!");
            return events;
        }
        LOG.info("Result: ");
        LOG.info(events);

        return events;
    }

    @Override
    public List<Event> searchByDay(LocalDate day) {
        if (day == null) throw new IllegalArgumentException();

        LOG.info("Searching by day '" + day +"':");
        List<Event> events = dataStore.getEventByDay(day);
        if (events.size()<1) {
            LOG.info("Events not found!");
            return events;
        }
        LOG.info("Result: ");
        LOG.info(events);

        return events;
    }
}
