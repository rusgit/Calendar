package com.diosoft.calendar.server.service;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.datastore.DataStore;
import com.diosoft.calendar.server.datastore.DataStoreImpl;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.exception.OrderOfArgumentsException;
import com.diosoft.calendar.server.exception.ValidationException;
import com.diosoft.calendar.server.util.DateParser;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class CalendarServiceImplTest {

    private Person testPerson = new Person.PersonBuilder()
            .name("Denis")
            .lastName("Milyaev")
            .email("denis@ukr.net")
            .build();

    private Set<Person> attenders = new HashSet<Person>();

    private Event testEvent = new Event.EventBuilder()
            .id(UUID.randomUUID()).title("TestEvent")
            .description("Description of testEvent")
            .startDate(LocalDateTime.of(2014, 1, 1, 0, 0))
            .endDate(LocalDateTime.of(2014, 1, 2, 0, 0))
            .attendersSet(attenders).build();

    private DataStore mockDataStore;
    private CalendarService calendarService;


    @Before
    public void setUp() {
        mockDataStore = mock(DataStoreImpl.class);
        calendarService = new CalendarServiceImpl(mockDataStore);
    }


    @Test
    public void testAdd() throws RemoteException, IllegalArgumentException, ValidationException {

        calendarService.add(testEvent);
        verify(mockDataStore,times(1)).publish(testEvent);
    }

    @Test(expected = IllegalArgumentException.class )
    public void testAddWithNullArg() throws RemoteException, IllegalArgumentException, ValidationException {

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
    public void testCreateEvent() throws RemoteException, IllegalArgumentException, DateTimeFormatException, ValidationException {

        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();

        Set<Person> attendersTest = new HashSet<Person>();
        attendersTest.add(attender);

        Event expectedEvent = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(LocalDateTime.of(2014, 10, 15, 15, 0))
                .endDate(LocalDateTime.of(2014, 10, 15, 20, 0))
                .attendersSet(attendersTest).build();

        String[] descriptions = {"Happy Birthday", "Happy Birthday Denis", "2014-10-15 15:00", "2014-10-15 20:00"};
        Event createdEvent = calendarService.createEvent(descriptions, attendersTest);

        assertEquals(expectedEvent, createdEvent);
        verify(mockDataStore,times(1)).publish(createdEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEventWithIllegalArg() throws RemoteException, IllegalArgumentException, DateTimeFormatException, ValidationException {

        Set<Person> attendersTest = new HashSet<Person>();

        String[] descriptions = {"Happy Birthday", "2014-10-15 15:00", "2014-10-15 20:00"};
        calendarService.createEvent(descriptions, attendersTest);
    }

    @Test(expected = DateTimeFormatException.class)
    public void testCreateEventWithWrongDateFormat() throws RemoteException, IllegalArgumentException, DateTimeFormatException, ValidationException {

        Set<Person> attendersTest = new HashSet<Person>();

        String[] descriptions = {"Happy Birthday", "Happy Birthday Denis", "2014-20-15 15:00", "2014-10-15 20:00"};
        calendarService.createEvent(descriptions, attendersTest);
    }


    @Test
    public void testIsAttenderFreeWithoutEventIntoGivenPeriod() throws DateTimeFormatException, RemoteException, OrderOfArgumentsException, ValidationException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<Person>();
        attendersTest.add(attender);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2014-10-15 15:00"))
                .endDate(DateParser.stringToDate("2014-10-15 20:00"))
                .attendersSet(attendersTest).build();
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("New Year 2015")
                .description("Happy New Year 2015")
                .startDate(DateParser.stringToDate("2014-12-31 20:00"))
                .endDate(DateParser.stringToDate("2015-01-01 12:00"))
                .attendersSet(attendersTest).build();
        LocalDateTime startDate = DateParser.stringToDate("2014-10-15 20:00");
        LocalDateTime endDate = DateParser.stringToDate("2014-12-31 20:00");
        calendarService.add(event1);
        calendarService.add(event2);
        List<Event> eventList = new ArrayList<Event>();
        eventList.add(event1);
        eventList.add(event2);

        when(mockDataStore.getEventByAttender(attender)).thenReturn(eventList);
        boolean isFreeResult = calendarService.isAttenderFree(attender, startDate ,endDate);

        Assert.assertTrue(isFreeResult);
        verify(mockDataStore,times(1)).getEventByAttender(attender);
    }

    @Test
    public void testIsAttenderFreeWithStartEventIntoGivenPeriod() throws DateTimeFormatException, RemoteException, OrderOfArgumentsException, ValidationException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<Person>();
        attendersTest.add(attender);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2014-10-15 15:00"))
                .endDate(DateParser.stringToDate("2014-10-15 20:00"))
                .attendersSet(attendersTest).build();
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("New Year 2015")
                .description("Happy New Year 2015")
                .startDate(DateParser.stringToDate("2014-12-31 20:00"))
                .endDate(DateParser.stringToDate("2015-01-01 12:00"))
                .attendersSet(attendersTest).build();
        LocalDateTime startDate = DateParser.stringToDate("2014-09-20 15:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-10-15 15:45");
        calendarService.add(event1);
        calendarService.add(event2);
        List<Event> eventList = new ArrayList<Event>();
        eventList.add(event1);
        eventList.add(event2);

        when(mockDataStore.getEventByAttender(attender)).thenReturn(eventList);
        boolean isFreeResult = calendarService.isAttenderFree(attender, startDate ,endDate);

        Assert.assertFalse(isFreeResult);
        verify(mockDataStore,times(1)).getEventByAttender(attender);
    }

    @Test
    public void testIsAttenderFreeWithEndAndStartEventsIntoGivenPeriod() throws DateTimeFormatException, RemoteException, OrderOfArgumentsException, ValidationException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<Person>();
        attendersTest.add(attender);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2014-10-15 15:00"))
                .endDate(DateParser.stringToDate("2014-10-15 20:00"))
                .attendersSet(attendersTest).build();
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("New Year 2015")
                .description("Happy New Year 2015")
                .startDate(DateParser.stringToDate("2014-12-31 20:00"))
                .endDate(DateParser.stringToDate("2015-01-01 12:00"))
                .attendersSet(attendersTest).build();
        LocalDateTime startDate = DateParser.stringToDate("2014-10-15 18:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-12-31 21:45");
        calendarService.add(event1);
        calendarService.add(event2);
        List<Event> eventList = new ArrayList<Event>();
        eventList.add(event1);
        eventList.add(event2);

        when(mockDataStore.getEventByAttender(attender)).thenReturn(eventList);
        boolean isFreeResult = calendarService.isAttenderFree(attender, startDate ,endDate);

        Assert.assertFalse(isFreeResult);
        verify(mockDataStore,times(1)).getEventByAttender(attender);
    }

    @Test
    public void testIsAttenderFreeWithEventIntoGivenPeriod() throws DateTimeFormatException, RemoteException, OrderOfArgumentsException, ValidationException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<Person>();
        attendersTest.add(attender);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2014-10-15 15:00"))
                .endDate(DateParser.stringToDate("2014-10-15 20:00"))
                .attendersSet(attendersTest).build();
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("New Year 2015")
                .description("Happy New Year 2015")
                .startDate(DateParser.stringToDate("2014-12-31 20:00"))
                .endDate(DateParser.stringToDate("2015-01-01 12:00"))
                .attendersSet(attendersTest).build();
        LocalDateTime startDate = DateParser.stringToDate("2014-09-20 14:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-10-30 14:45");
        calendarService.add(event1);
        calendarService.add(event2);
        List<Event> eventList = new ArrayList<Event>();
        eventList.add(event1);
        eventList.add(event2);

        when(mockDataStore.getEventByAttender(attender)).thenReturn(eventList);
        boolean isFreeResult = calendarService.isAttenderFree(attender, startDate ,endDate);

        Assert.assertFalse(isFreeResult);
        verify(mockDataStore,times(1)).getEventByAttender(attender);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsAttenderFreeWithIllegalArg() throws RemoteException, IllegalArgumentException, DateTimeFormatException, OrderOfArgumentsException {
        LocalDateTime startDate = DateParser.stringToDate("2014-09-20 14:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-09-30 14:45");
        calendarService.isAttenderFree(null, startDate, endDate);
    }

    @Test(expected = OrderOfArgumentsException.class)
    public void testIsAttenderFreeWithWrongOrderOfDate() throws RemoteException, OrderOfArgumentsException, DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        LocalDateTime startDate = DateParser.stringToDate("2014-10-20 14:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-09-30 14:45");
        calendarService.isAttenderFree(attender, startDate, endDate);
    }

    //////////////////////////////////////////
    //////////////////////////////////////////
    //////////////////////////////////////////

    @Test
    public void testSearchByAttenderIntoPeriodWithoutEventIntoGivenPeriod() throws DateTimeFormatException, RemoteException, OrderOfArgumentsException, ValidationException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<Person>();
        attendersTest.add(attender);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2014-10-15 15:00"))
                .endDate(DateParser.stringToDate("2014-10-15 20:00"))
                .attendersSet(attendersTest).build();
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("New Year 2015")
                .description("Happy New Year 2015")
                .startDate(DateParser.stringToDate("2014-12-31 20:00"))
                .endDate(DateParser.stringToDate("2015-01-01 12:00"))
                .attendersSet(attendersTest).build();
        LocalDateTime startDate = DateParser.stringToDate("2014-10-15 20:00");
        LocalDateTime endDate = DateParser.stringToDate("2014-12-31 20:00");
        calendarService.add(event1);
        calendarService.add(event2);
        List<Event> expectedList = new ArrayList<Event>();

        when(mockDataStore.getEventByAttender(attender)).thenReturn(expectedList);
        List<Event> resultList = calendarService.searchByAttenderIntoPeriod(attender, startDate ,endDate);

        Assert.assertEquals(resultList, expectedList);
        verify(mockDataStore,times(1)).getEventByAttender(attender);
    }

    @Test
    public void testSearchByAttenderIntoPeriodWithStartEventIntoGivenPeriod() throws DateTimeFormatException, RemoteException, OrderOfArgumentsException, ValidationException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<Person>();
        attendersTest.add(attender);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2014-10-15 15:00"))
                .endDate(DateParser.stringToDate("2014-10-15 20:00"))
                .attendersSet(attendersTest).build();
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("New Year 2015")
                .description("Happy New Year 2015")
                .startDate(DateParser.stringToDate("2014-12-31 20:00"))
                .endDate(DateParser.stringToDate("2015-01-01 12:00"))
                .attendersSet(attendersTest).build();
        LocalDateTime startDate = DateParser.stringToDate("2014-09-20 15:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-10-15 15:45");
        calendarService.add(event1);
        calendarService.add(event2);
        List<Event> expectedList = new ArrayList<Event>();
        expectedList.add(event1);

        when(mockDataStore.getEventByAttender(attender)).thenReturn(expectedList);
        List<Event> resultList = calendarService.searchByAttenderIntoPeriod(attender, startDate ,endDate);

        Assert.assertEquals(resultList, expectedList);
        verify(mockDataStore,times(1)).getEventByAttender(attender);
    }

    @Test
    public void testSearchByAttenderIntoPeriodWithEndAndStartEventsIntoGivenPeriod() throws DateTimeFormatException, RemoteException, OrderOfArgumentsException, ValidationException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<Person>();
        attendersTest.add(attender);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2014-10-15 15:00"))
                .endDate(DateParser.stringToDate("2014-10-15 20:00"))
                .attendersSet(attendersTest).build();
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("New Year 2015")
                .description("Happy New Year 2015")
                .startDate(DateParser.stringToDate("2014-12-31 20:00"))
                .endDate(DateParser.stringToDate("2015-01-01 12:00"))
                .attendersSet(attendersTest).build();
        LocalDateTime startDate = DateParser.stringToDate("2014-10-15 18:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-12-31 21:45");
        calendarService.add(event1);
        calendarService.add(event2);
        List<Event> expectedList = new ArrayList<Event>();
        expectedList.add(event1);
        expectedList.add(event2);

        when(mockDataStore.getEventByAttender(attender)).thenReturn(expectedList);
        List<Event> resultList = calendarService.searchByAttenderIntoPeriod(attender, startDate ,endDate);

        Assert.assertEquals(resultList, expectedList);
        verify(mockDataStore,times(1)).getEventByAttender(attender);
    }

    @Test
    public void testSearchByAttenderIntoPeriodWithEventIntoGivenPeriod() throws DateTimeFormatException, RemoteException, OrderOfArgumentsException, ValidationException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        Set<Person> attendersTest = new HashSet<Person>();
        attendersTest.add(attender);
        Event event1 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Happy Birthday")
                .description("Happy Birthday Denis")
                .startDate(DateParser.stringToDate("2014-10-15 15:00"))
                .endDate(DateParser.stringToDate("2014-10-15 20:00"))
                .attendersSet(attendersTest).build();
        Event event2 = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("New Year 2015")
                .description("Happy New Year 2015")
                .startDate(DateParser.stringToDate("2014-12-31 20:00"))
                .endDate(DateParser.stringToDate("2015-01-01 12:00"))
                .attendersSet(attendersTest).build();
        LocalDateTime startDate = DateParser.stringToDate("2014-09-20 14:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-10-30 14:45");
        calendarService.add(event1);
        calendarService.add(event2);
        List<Event> expectedList = new ArrayList<Event>();
        expectedList.add(event1);

        when(mockDataStore.getEventByAttender(attender)).thenReturn(expectedList);
        List<Event> resultList = calendarService.searchByAttenderIntoPeriod(attender, startDate ,endDate);

        Assert.assertEquals(resultList, expectedList);
        verify(mockDataStore,times(1)).getEventByAttender(attender);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByAttenderIntoPeriodWithIllegalArg() throws RemoteException, IllegalArgumentException, DateTimeFormatException, OrderOfArgumentsException {
        LocalDateTime startDate = DateParser.stringToDate("2014-09-20 14:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-09-30 14:45");
        calendarService.isAttenderFree(null, startDate, endDate);
    }

    @Test(expected = OrderOfArgumentsException.class)
    public void testSearchByAttenderIntoPeriodWithWrongOrderOfDate() throws RemoteException, OrderOfArgumentsException, DateTimeFormatException {
        Person attender = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();
        LocalDateTime startDate = DateParser.stringToDate("2014-10-20 14:45");
        LocalDateTime endDate = DateParser.stringToDate("2014-09-30 14:45");
        calendarService.isAttenderFree(attender, startDate, endDate);
    }
}
