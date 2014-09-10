package com.diosoft.calendar.service;

import com.diosoft.calendar.common.Event;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CalendarService {

    /**
     * Provides ability to publish event to the data store.
     * Uses method of dataStoreImpl: void publish(Event event)
     * @param event which adds
     */
    void add(Event event);

    /**
     * Provides ability to remove event from the data store.
     * Uses method of dataStoreImpl: void remove(UUID id)
     * @param id of event
     */
    void remove(UUID id);

    /**
     * Provides ability to search by title event from the data store.
     * Uses method of dataStoreImpl: List<Event> searchByTitle(String title)
     * @param title for search
     * @return List of events by title
     */
    List<Event> searchByTitle(String title);

    /**
     * Provides ability to search by date event from the data store.
     * Uses method of dataStoreImpl: List<Event> searchByDay(LocalDate day)
     * @param day for search
     * @return List of events by day
     */
    List<Event> searchByDay(LocalDate day);
}
