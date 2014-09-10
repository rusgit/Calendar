package com.diosoft.calendar.datastore;

import com.diosoft.calendar.common.Event;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DataStore {

    /**
     * Adds given event to the data store
     * @param event which adds
     */
    void publish(Event event);

    /**
     * Removes event for given id from the data store
     * @param id of event
     */
    void remove(UUID id);

    /**
     * Search event for given id in the data store and return it
     * @param id for search
     * @return event by id
     */
    Event getEventById(UUID id);

    /**
     * Search event for given title in the data store and return it.
     * Uses index map.
     * @param title for search
     * @return List of events by title
     */
    List<Event> getEventByTitle(String title);

    /**
     * Search event for given particular day in the data store and return it.
     * Uses index map.
     * @param day for search
     * @return List of events by date
     */
    List<Event> getEventByDay(LocalDate day);
}
