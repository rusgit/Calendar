package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.service.CalendarService;
import com.diosoft.calendar.server.service.CalendarServiceImpl;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DataStoreImplTest {

    Person testPerson = new Person.PersonBuilder()
            .name("Denis")
            .lastName("Milyaev")
            .email("denis@ukr.net")
            .build();

    Set<Person> attenders = new HashSet<Person>();

    Event testEvent = new Event.EventBuilder()
            .id(UUID.randomUUID()).title("TestEvent")
            .description("Description of testEvent")
            .startDate(LocalDateTime.of(2020, 1, 1, 0, 0))
            .endDate(LocalDateTime.of(2020, 1, 2, 0, 0))
            .attendersSet(attenders).build();

    private JAXBHelper mockJaxbHelper;
    private DataStore dataStore;

    @Before
    public void setUp() {
        mockJaxbHelper =  mock(JAXBHelper.class);
        dataStore = new DataStoreImpl(mockJaxbHelper);
    }

    @Test
    public void testPublish() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        Event expectedEvent = testEvent;

        dataStore.publish(testEvent);
        Event actualEvent = dataStore.getEventById(testEvent.getId());

        assertEquals(expectedEvent,actualEvent);
        verify(mockJaxbHelper,times(1)).writeEvent(testEvent);
    }

    @Test(expected = IllegalArgumentException.class )
    public void testPublishWithNullArg() throws IllegalArgumentException, IOException, JAXBException {

        dataStore.publish(null);
        verify(mockJaxbHelper,never()).writeEvent(testEvent);;
    }

    @Test
    public void testRemove() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        Event expectedRemovedEvent = testEvent;

        dataStore.publish(testEvent);
        Event actualRemovedEvent = dataStore.remove(testEvent.getId());

        assertEquals(expectedRemovedEvent,actualRemovedEvent);
        verify(mockJaxbHelper,times(1)).deleteEvent(testEvent.getId());
    }

    @Test
    public void testRemoveNotExistsEvent() throws IllegalArgumentException, JAXBException, IOException {

        Event actualRemovedEvent = dataStore.remove(testEvent.getId());
        assertNull(actualRemovedEvent);
        verify(mockJaxbHelper,never()).deleteEvent(testEvent.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithNullArg() throws IllegalArgumentException, JAXBException, IOException {

        dataStore.remove(null);
        verify(mockJaxbHelper,never());
    }

    @Test
    public void testGetEventById() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        Event expectedEvent = testEvent;

        dataStore.publish(testEvent);
        Event actualEvent = dataStore.getEventById(testEvent.getId());

        assertEquals(expectedEvent,actualEvent);
    }

    @Test
    public void testGetEventByIdNotExistsEvent() throws IllegalArgumentException  {

        Event actualEvent = dataStore.getEventById(testEvent.getId());
        assertNull(actualEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByIdWithNullArg() throws IllegalArgumentException {

        dataStore.getEventById(null);
    }

    @Test
    public void testGetEventByTitle() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        List<Event> expectedEvents = new ArrayList<Event>();
        expectedEvents.add(testEvent);

        dataStore.publish(testEvent);
        List<Event> actualEvents = dataStore.getEventByTitle(testEvent.getTitle());

        assertEquals(expectedEvents,actualEvents);
    }

    @Test
    public void testGetEventByTitleNotExistsEvent() throws IllegalArgumentException  {

//  empty list
        List<Event> expectedEvents = new ArrayList<Event>();

        List<Event> actualEvents = dataStore.getEventByTitle(testEvent.getTitle());

        assertEquals(expectedEvents,actualEvents);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByTitleWithNullArg() throws IllegalArgumentException  {

        dataStore.getEventByTitle(null);
    }

    @Test
    public void testGetEventByDay() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        List<Event> expectedEvents = new ArrayList<Event>();
        expectedEvents.add(testEvent);

        dataStore.publish(testEvent);
        List<Event> actualEvents = dataStore.getEventByDay(testEvent.getStartDate().toLocalDate());

        assertEquals(expectedEvents,actualEvents);
    }

    @Test
    public void testGetEventByDayNotExistsEvent() throws IllegalArgumentException  {

//  empty list
        List<Event> expectedEvents = new ArrayList<Event>();

        List<Event> actualEvents = dataStore.getEventByDay(testEvent.getStartDate().toLocalDate());

        assertEquals(expectedEvents,actualEvents);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByDayWithNullArg() throws IllegalArgumentException  {

        dataStore.getEventByDay(null);
    }

    @Test
    public void testGetEventByAttender() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        List<Event> expectedEvents = new ArrayList<Event>();
        expectedEvents.add(testEvent);

        dataStore.publish(testEvent);
        List<Event> actualEvents = dataStore.getEventByAttender(testPerson);

        assertEquals(expectedEvents,actualEvents);
    }

    @Test
    public void testGetEventByAttenderNotExistsEvent() throws IllegalArgumentException  {

// empty list
        List<Event> expectedEvents = new ArrayList<Event>();

        List<Event> actualEvents = dataStore.getEventByAttender(testPerson);

        assertEquals(expectedEvents,actualEvents);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByAttenderWithNullArg() throws IllegalArgumentException  {

        dataStore.getEventByAttender(null);
    }
}
