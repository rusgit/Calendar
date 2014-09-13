package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.*;

public class DataStoreImplTest {

    Person testPerson = new Person.PersonBuilder()
            .name("Denis")
            .lastName("Milyaev")
            .email("denis@ukr.net")
            .build();

    List<Person> attenders = new ArrayList<Person>();

    Event testEvent = new Event.EventBuilder()
            .id(UUID.randomUUID()).title("TestEvent")
            .description("Description of testEvent")
            .startDate(LocalDateTime.of(2014, 1, 1, 0, 0))
            .endDate(LocalDateTime.of(2014, 1, 2, 0, 0))
            .attendersList(attenders).build();

    @Test
    public void testPublish() throws IllegalArgumentException  {

        attenders.add(testPerson);
        Event expectedEvent = testEvent;

        DataStore dataStore = new DataStoreImpl();
        dataStore.publish(testEvent);
        Event actualEvent = dataStore.getEventById(testEvent.getId());

        assertEquals(expectedEvent,actualEvent);
    }

    @Test(expected = IllegalArgumentException.class )
    public void testPublishWithNullArg() throws IllegalArgumentException  {

        DataStore dataStore = new DataStoreImpl();
        dataStore.publish(null);
    }

    @Test
    public void testRemove() throws IllegalArgumentException {

        attenders.add(testPerson);
        Event expectedRemovedEvent = testEvent;

        DataStore dataStore = new DataStoreImpl();
        dataStore.publish(testEvent);
        Event actaulRemovedEvent = dataStore.remove(testEvent.getId());

        assertEquals(expectedRemovedEvent,actaulRemovedEvent);
    }

    @Test
    public void testRemoveEventNoInDataStore() throws IllegalArgumentException {

        DataStore dataStore = new DataStoreImpl();
        Event actaulRemovedEvent = dataStore.remove(testEvent.getId());

        assertNull(actaulRemovedEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithNullArg() throws IllegalArgumentException {

        DataStore dataStore = new DataStoreImpl();
        dataStore.remove(null);
    }

    @Test
    public void testgGetEventById() throws IllegalArgumentException  {

        attenders.add(testPerson);
        Event expectedEvent = testEvent;

        DataStore dataStore = new DataStoreImpl();
        dataStore.publish(testEvent);
        Event actualEvent = dataStore.getEventById(testEvent.getId());

        assertEquals(expectedEvent,actualEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testgGetEventByIdWithNullArg() throws IllegalArgumentException {

        DataStore dataStore = new DataStoreImpl();
        dataStore.getEventById(null);
    }

    @Test
    public void testGetEventByTitle() throws IllegalArgumentException  {

        attenders.add(testPerson);
        List<Event> expectedEvents = new ArrayList<Event>();
        expectedEvents.add(testEvent);

        DataStore dataStore = new DataStoreImpl();
        dataStore.publish(testEvent);
        List<Event> actualEvents = dataStore.getEventByTitle(testEvent.getTitle());

        assertEquals(expectedEvents,actualEvents);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByTitleWithNullArg() throws IllegalArgumentException  {

        DataStore dataStore = new DataStoreImpl();
        dataStore.getEventByTitle(null);
    }

    @Test
    public void testGetEventByDay() throws IllegalArgumentException  {

        attenders.add(testPerson);
        List<Event> expectedEvents = new ArrayList<Event>();
        expectedEvents.add(testEvent);

        DataStore dataStore = new DataStoreImpl();
        dataStore.publish(testEvent);
        List<Event> actualEvents = dataStore.getEventByDay(testEvent.getStartDate().toLocalDate());

        assertEquals(expectedEvents,actualEvents);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByDayWithNullArg() throws IllegalArgumentException  {

        DataStore dataStore = new DataStoreImpl();
        dataStore.getEventByDay(null);
    }
}
