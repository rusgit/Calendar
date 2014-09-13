package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.common.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DataStore {

    /**
     * Adds given event to the data store
     * @param event which adds
     * @throws IllegalArgumentException
     */
    void publish(Event event) throws IllegalArgumentException;

    /**
     * Removes event for given id from the data store
     * @param id of event
     * @throws IllegalArgumentException
     */
    void remove(UUID id) throws IllegalArgumentException;

    /**
     * Search event for given id in the data store and return it
     * @param id for search
     * @return event by id
     * @throws IllegalArgumentException
     */
    Event getEventById(UUID id) throws IllegalArgumentException;

    /**
     * Search event for given title in the data store and return it.
     * Uses index map.
     * @param title for search
     * @return List of events by title
     * @throws IllegalArgumentException
     */
    List<Event> getEventByTitle(String title) throws IllegalArgumentException;

    /**
     * Search event for given particular day in the data store and return it.
     * Uses index map.
     * @param day for search
     * @return List of events by date
     * @throws IllegalArgumentException
     */
    List<Event> getEventByDay(LocalDate day) throws IllegalArgumentException;
}
