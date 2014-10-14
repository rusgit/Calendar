package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.PeriodOfEvent;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.filesystem.FileSystem;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class DataStoreImplTest {

    private Person testPerson = new Person.PersonBuilder()
            .name("Denis")
            .lastName("Milyaev")
            .email("denis@ukr.net")
            .build();

    private Set<Person> attenders = new HashSet<>();
    private Set<PeriodOfEvent> period = new HashSet<>();

    private Event testEvent = new Event.EventBuilder()
            .id(UUID.randomUUID()).title("TestEvent")
            .description("Description of testEvent")
            .startDate(LocalDateTime.of(2020, 1, 1, 0, 0))
            .endDate(LocalDateTime.of(2020, 1, 2, 0, 0))
            .periodSet(period)
            .attendersSet(attenders).build();

    private FileSystem mockFileSystem;
    private DataStore dataStore;

    @Before
    public void setUp() {
        mockFileSystem = mock(FileSystem.class);
        dataStore = new DataStoreImpl(mockFileSystem);
    }

    @Test
    public void testPublish() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        Event expectedEvent = testEvent;

        dataStore.publish(testEvent);
        Event actualEvent = dataStore.getEventById(testEvent.getId());

        assertEquals(expectedEvent,actualEvent);
        verify(mockFileSystem).write(testEvent);
    }

    @Test(expected = IllegalArgumentException.class )
    public void testPublishWithNullArg() throws IllegalArgumentException, IOException, JAXBException {

        dataStore.publish(null);
        verify(mockFileSystem, never()).write(testEvent);
    }

    @Test
    public void testRemove() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        Event expectedRemovedEvent = testEvent;

        dataStore.publish(testEvent);
        Event actualRemovedEvent = dataStore.remove(testEvent.getId());

        assertEquals(expectedRemovedEvent,actualRemovedEvent);
        verify(mockFileSystem).delete(testEvent.getId());
    }

    @Test
    public void testRemoveNotExistsEvent() throws IllegalArgumentException, JAXBException, IOException {

        Event actualRemovedEvent = dataStore.remove(testEvent.getId());
        assertNull(actualRemovedEvent);
        verify(mockFileSystem, never()).delete(testEvent.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithNullArg() throws IllegalArgumentException, JAXBException, IOException {

        dataStore.remove(null);
        verify(mockFileSystem, never()).delete(null);
    }

    @Test
    public void testGetEventById() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        Event expectedEvent = testEvent;

        dataStore.publish(testEvent);
        Event actualEvent = dataStore.getEventById(testEvent.getId());

        assertEquals(expectedEvent, actualEvent);
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
        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(testEvent);

        dataStore.publish(testEvent);
        List<Event> actualEvents = dataStore.getEventByTitle(testEvent.getTitle());

        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    public void testGetEventByTitleNotExistsEvent() throws IllegalArgumentException  {

        List<Event> expectedEvents = Collections.emptyList();

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
        period.add(PeriodOfEvent.ONCE);
        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(testEvent);

        dataStore.publish(testEvent);
        List<Event> actualEvents = dataStore.getEventByDay(testEvent.getStartDate().toLocalDate());

        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    public void testGetEventByDayWithPeriod() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);

        Set<PeriodOfEvent> periodOnce = new HashSet<>();
        periodOnce.add(PeriodOfEvent.ONCE);
        Event eventOnce = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Event")
                .description("Description of event")
                .startDate(LocalDateTime.of(2020, 1, 1, 8, 0))
                .endDate(LocalDateTime.of(2020, 1, 2, 10, 0))
                .periodSet(periodOnce)
                .attendersSet(attenders).build();

        Set<PeriodOfEvent> periodYear = new HashSet<>();
        periodYear.add(PeriodOfEvent.EVERY_YEAR);
        Event eventYear = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Event")
                .description("Description of event")
                .startDate(LocalDateTime.of(2019, 1, 1, 10, 0))
                .endDate(LocalDateTime.of(2019, 1, 2, 20, 0))
                .periodSet(periodYear)
                .attendersSet(attenders).build();

        Set<PeriodOfEvent> periodMonth = new HashSet<>();
        periodMonth.add(PeriodOfEvent.EVERY_MONTH);
        Event eventMonth = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Event")
                .description("Description of event")
                .startDate(LocalDateTime.of(2019, 12, 1, 7, 0))
                .endDate(LocalDateTime.of(2019, 12, 1, 9, 0))
                .periodSet(periodMonth)
                .attendersSet(attenders).build();

        Set<PeriodOfEvent> periodDay = new HashSet<>();
        periodDay.add(PeriodOfEvent.EVERY_DAY);
        Event eventDay = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Event")
                .description("Description of event")
                .startDate(LocalDateTime.of(2019, 12, 23, 10, 0))
                .endDate(LocalDateTime.of(2019, 12, 23, 20, 0))
                .periodSet(periodDay)
                .attendersSet(attenders).build();

        Set<PeriodOfEvent> periodWeek = new HashSet<>();
        periodWeek.add(PeriodOfEvent.WEDNESDAY);
        periodWeek.add(PeriodOfEvent.FRIDAY);
        Event eventWeek = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("Event")
                .description("Description of event")
                .startDate(LocalDateTime.of(2020, 1, 1, 20, 0))
                .endDate(LocalDateTime.of(2020, 1, 1, 22, 0))
                .periodSet(periodWeek)
                .attendersSet(attenders).build();

        Set<Event> expectedEvents = new TreeSet<>();
        expectedEvents.add(eventOnce);
        expectedEvents.add(eventYear);
        expectedEvents.add(eventMonth);
        expectedEvents.add(eventDay);
        expectedEvents.add(eventWeek);

        dataStore.publish(eventOnce);
        dataStore.publish(eventYear);
        dataStore.publish(eventMonth);
        dataStore.publish(eventDay);
        dataStore.publish(eventWeek);
        Set<Event> actualEvents = new TreeSet<>();
        actualEvents.addAll(dataStore.getEventByDay(LocalDateTime.of(2020, 1, 1, 22, 0).toLocalDate()));

        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    public void testGetEventByDayNotExistsEvent() throws IllegalArgumentException  {

        List<Event> expectedEvents = Collections.emptyList();

        List<Event> actualEvents = dataStore.getEventByDay(testEvent.getStartDate().toLocalDate());

        assertEquals(expectedEvents, actualEvents);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByDayWithNullArg() throws IllegalArgumentException  {

        dataStore.getEventByDay(null);
    }

    @Test
    public void testGetEventByAttender() throws IllegalArgumentException, IOException, JAXBException {

        attenders.add(testPerson);
        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(testEvent);

        dataStore.publish(testEvent);
        List<Event> actualEvents = dataStore.getEventByAttender(testPerson);

        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    public void testGetEventByAttenderNotExistsEvent() throws IllegalArgumentException  {

        List<Event> expectedEvents = Collections.emptyList();

        List<Event> actualEvents = dataStore.getEventByAttender(testPerson);

        assertEquals(expectedEvents, actualEvents);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetEventByAttenderWithNullArg() throws IllegalArgumentException  {

        dataStore.getEventByAttender(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchEventByTitleStartWithNullString() throws IllegalArgumentException  {

        dataStore.searchEventByTitleStartWith(null);
    }


    @Test
    public void testSearchEventByTitleStartWith () throws IOException, JAXBException {
        Person testPerson = new Person.PersonBuilder()
                .name("Denis")
                .lastName("Milyaev")
                .email("denis@ukr.net")
                .build();

        Set<Person> attenders = new HashSet<>();
        attenders.add(testPerson);

        Event testEvent = new Event.EventBuilder()
                .id(UUID.randomUUID()).title("TestEvent")
                .description("Description of testEvent")
                .startDate(LocalDateTime.of(2020, 1, 1, 0, 0))
                .endDate(LocalDateTime.of(2020, 1, 2, 0, 0))
                .periodSet(period)
                .attendersSet(attenders).build();

        List<Event> expectedEventList = new ArrayList<>();
        expectedEventList.add(testEvent);
        String prefix = "Tes";
        dataStore.publish(testEvent);

        List<Event> resultEventList = dataStore.searchEventByTitleStartWith(prefix);

        assertEquals(expectedEventList, resultEventList);
    }
}
