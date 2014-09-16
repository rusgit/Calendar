package com.diosoft.calendar.server.service;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.datastore.DataStore;
import com.diosoft.calendar.server.datastore.DataStoreImpl;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import org.junit.Before;
import org.junit.Test;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class CalendarServiceImplTest {

    private Person testPerson = new Person.PersonBuilder()
            .name("Denis")
            .lastName("Milyaev")
            .email("denis@ukr.net")
            .build();

    private List<Person> attenders = new ArrayList<Person>();

    private Event testEvent = new Event.EventBuilder()
            .id(UUID.randomUUID()).title("TestEvent")
            .description("Description of testEvent")
            .startDate(LocalDateTime.of(2014, 1, 1, 0, 0))
            .endDate(LocalDateTime.of(2014, 1, 2, 0, 0))
            .attendersList(attenders).build();

    private DataStore mockDataStore;
    private CalendarService calendarService;


    @Before
    public void setUp() {
        mockDataStore = mock(DataStoreImpl.class);
        calendarService = new CalendarServiceImpl(mockDataStore);
    }


    @Test
    public void testAdd() throws RemoteException, IllegalArgumentException {

        calendarService.add(testEvent);
        verify(mockDataStore,times(1)).publish(testEvent);
    }

    @Test(expected = IllegalArgumentException.class )
    public void testAddWithNullArg() throws RemoteException, IllegalArgumentException {

        doThrow(new IllegalArgumentException()).when(mockDataStore).publish(null);
        calendarService.add(null);
        verify(mockDataStore, times(1)).publish(null);
    }

    @Test
    public void testRemove() throws RemoteException, IllegalArgumentException {

        when(mockDataStore.remove(testEvent.getId())).thenReturn(testEvent);
        Event actualEvent = calendarService.remove(testEvent.getId());
        assertEquals(testEvent,actualEvent);
        verify(mockDataStore,times(1)).remove(testEvent.getId());
    }

    @Test
    public void testRemoveEventNoInDataStore() throws RemoteException, IllegalArgumentException {

        when(mockDataStore.remove(testEvent.getId())).thenReturn(null);
        Event actualEvent = calendarService.remove(testEvent.getId());
        assertNull(actualEvent);
        verify(mockDataStore,times(1)).remove(testEvent.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithNullArg() throws RemoteException, IllegalArgumentException {

        doThrow(new IllegalArgumentException()).when(mockDataStore).remove(null);
        calendarService.remove(null);
        verify(mockDataStore,times(1)).remove(null);
    }

    @Test
    public void testGetEventByTitle() throws RemoteException, IllegalArgumentException  {

        attenders.add(testPerson);
        List<Event> expectedEvents = new ArrayList<Event>();
        expectedEvents.add(testEvent);

        when(mockDataStore.getEventByTitle(testEvent.getTitle())).thenReturn(expectedEvents);
        List<Event> actualEvents = calendarService.searchByTitle(testEvent.getTitle());

        assertEquals(expectedEvents,actualEvents);
        verify(mockDataStore,times(1)).getEventByTitle(testEvent.getTitle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByTitleWithNullArg() throws RemoteException, IllegalArgumentException {

        when(mockDataStore.getEventByTitle(null)).thenThrow(new IllegalArgumentException());
        calendarService.searchByTitle(null);
    }

    @Test
    public void testGetEventByDay() throws RemoteException, IllegalArgumentException  {

        attenders.add(testPerson);
        List<Event> expectedEvents = new ArrayList<Event>();
        expectedEvents.add(testEvent);

        when(mockDataStore.getEventByDay(testEvent.getStartDate().toLocalDate())).thenReturn(expectedEvents);
        List<Event> actualEvents = calendarService.searchByDay(testEvent.getStartDate().toLocalDate());

        assertEquals(expectedEvents,actualEvents);
        verify(mockDataStore,times(1)).getEventByDay(testEvent.getStartDate().toLocalDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByDayWithNullArg() throws RemoteException, IllegalArgumentException {

        when(mockDataStore.getEventByDay(null)).thenThrow(new IllegalArgumentException());
        calendarService.searchByDay(null);
    }

    @Test
    public void testGetEventByAttender() throws RemoteException, IllegalArgumentException  {

        attenders.add(testPerson);
        List<Event> expectedEvents = new ArrayList<Event>();
        expectedEvents.add(testEvent);

        when(mockDataStore.getEventByAttender(testPerson)).thenReturn(expectedEvents);
        List<Event> actualEvents = calendarService.searchByAttender(testPerson);

        assertEquals(expectedEvents,actualEvents);
        verify(mockDataStore,times(1)).getEventByAttender(testPerson);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByAttenderWithNullArg() throws RemoteException, IllegalArgumentException {

        when(mockDataStore.getEventByAttender(null)).thenThrow(new IllegalArgumentException());
        calendarService.searchByAttender(null);
    }

    @Test
    public void testCreateEvent() throws RemoteException, IllegalArgumentException, DateTimeFormatException {

        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();

        List<Person> attendersTest = new ArrayList<Person>();
        attendersTest.add(attender);

        Event expectedEvent = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(LocalDateTime.of(2014, 10, 15, 15, 0))
                .endDate(LocalDateTime.of(2014, 10, 15, 20, 0))
                .attendersList(attendersTest).build();

        String[] descriptions = {"Happy Birthday", "Happy Birthday Denis", "2014-10-15 15:00", "2014-10-15 20:00"};
        Event createdEvent = calendarService.createAndAdd(descriptions, attendersTest);

        assertEquals(expectedEvent, createdEvent);
        verify(mockDataStore,times(1)).publish(createdEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEventWithIllegalArg() throws RemoteException, IllegalArgumentException, DateTimeFormatException {

        List<Person> attendersTest = new ArrayList<Person>();

        String[] descriptions = {"Happy Birthday", "2014-10-15 15:00", "2014-10-15 20:00"};
        calendarService.createAndAdd(descriptions, attendersTest);
    }

    @Test(expected = DateTimeFormatException.class)
    public void testCreateEventWithWrongDateFormat() throws RemoteException, IllegalArgumentException, DateTimeFormatException {

        List<Person> attendersTest = new ArrayList<Person>();

        String[] descriptions = {"Happy Birthday", "Happy Birthday Denis", "2014-20-15 15:00", "2014-10-15 20:00"};
        calendarService.createAndAdd(descriptions, attendersTest);
    }
}
