package com.diosoft.calendar.server.service;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.exception.OrderOfArgumentsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CalendarService extends Remote {

    /**
     * Provides ability to publish event to the data store.
     * Uses method of dataStoreImpl: void publish(Event event)
     * @param event which adds
     * @throws RemoteException, IllegalArgumentException
     */
    void add(Event event) throws RemoteException, IllegalArgumentException;

    /**
     * Creates event with given array descriptions and adds it into data store.
     * @param descriptions [0]: "title", [1]: "description", [2]: "startDate" , [3]: "endDate";
     * format of 'startDate' and 'endDate': "yyyy-MM-dd HH:mm". Example: "2014-01-05 10:00".
     * @param persons attenders
     * @return event
     * @throws RemoteException, IllegalArgumentException, DateTimeFormatException
     */
    Event createAndAdd(String[] descriptions, List<Person> attenders) throws RemoteException, IllegalArgumentException, DateTimeFormatException;

    /**
     * Provides ability to remove event from the data store.
     * Uses method of dataStoreImpl: void remove(UUID id)
     * @param id of event
     * @return removed event or null if there was no mapping for
     * @throws RemoteException, IllegalArgumentException
     */
    Event remove(UUID id) throws RemoteException, IllegalArgumentException;

    /**
     * Provides ability to search by title event from the data store.
     * Uses method of dataStoreImpl: List<Event> searchByTitle(String title)
     * @param title for search
     * @return List of events by title
     * @throws RemoteException, IllegalArgumentException
     */
    List<Event> searchByTitle(String title) throws RemoteException, IllegalArgumentException;

    /**
     * Provides ability to search by date event from the data store.
     * Uses method of dataStoreImpl: List<Event> searchByDay(LocalDate day)
     * @param day for search
     * @return List of events by day
     * @throws RemoteException, IllegalArgumentException
     */
    List<Event> searchByDay(LocalDate day) throws RemoteException, IllegalArgumentException;

    /**
     * Provides ability to search by attender event from the data store.
     * Uses method of dataStoreImpl: List<Event> searchByAttender(Person attender)
     * @param attender for search
     * @return List of events by attender
     * @throws RemoteException, IllegalArgumentException
     */
    List<Event> searchByAttender(Person attender) throws RemoteException, IllegalArgumentException;

    /**
     * Check whether a person is free to participate in events in a given period
     * @param attender for search
     * @param startDate for search
     * @param endDate for search
     * @return true or false
     * @throws RemoteException, IllegalArgumentException
     */
    boolean isAttenderFree(Person attender, LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, IllegalArgumentException, OrderOfArgumentsException;


}
