package com.diosoft.calendar.server.service;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.exception.DateTimeFormatException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CalendarService extends Remote {

    /**
     * Provides ability to publish event to the data store.
     * Uses method of dataStoreImpl: void publish(Event event)
     * @param event which adds
     */
    void add(Event event) throws RemoteException, IllegalArgumentException;

    /**
     * Provides ability to remove event from the data store.
     * Uses method of dataStoreImpl: void remove(UUID id)
     * @param id of event
     */
    void remove(UUID id) throws RemoteException, IllegalArgumentException;

    /**
     * Provides ability to search by title event from the data store.
     * Uses method of dataStoreImpl: List<Event> searchByTitle(String title)
     * @param title for search
     * @return List of events by title
     */
    List<Event> searchByTitle(String title) throws RemoteException, IllegalArgumentException;

    /**
     * Provides ability to search by date event from the data store.
     * Uses method of dataStoreImpl: List<Event> searchByDay(LocalDate day)
     * @param day for search
     * @return List of events by day
     */
    List<Event> searchByDay(LocalDate day) throws RemoteException, IllegalArgumentException;

    /**
     * Create event with given array descriptions.
     * @param descriptions [0]: "title", [1]: "description", [2]: "startDate" , [3]: "endDate";
     * format of 'startDate' and 'endDate': "yyyy, MM, dd, hh, mm". Example: "2014, 1, 5, 10, 0".
     * @param persons attenders
     * @return event
     */
    Event createEvent(String[] descriptions, List<Person> attenders) throws RemoteException, IllegalArgumentException, DateTimeFormatException;
}
