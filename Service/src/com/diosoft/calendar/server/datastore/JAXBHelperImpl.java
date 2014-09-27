package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.adapter.EventAdapter;
import com.diosoft.calendar.server.adapter.EventListAdapter;
import com.diosoft.calendar.server.adapter.PersonAdapter;
import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.util.DateParser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JAXBHelperImpl implements JAXBHelper {
    private final static String PATH_TO_EVENTS = "Service/resources/events/";

    @Override
    public List<Event> readAllEventsFromXMLResources() throws JAXBException, IOException, DateTimeFormatException {
        List<Event> eventList = new ArrayList<Event>();
        for (Path eventFile : Files.newDirectoryStream(Paths.get(PATH_TO_EVENTS))) eventList.add(readEvent(eventFile.toString()));
        return eventList;
    }

    @Override
    public void writeEvent(Event event) throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_TO_EVENTS).append(event.getId()).append(".xml");
        File file = new File(sb.toString());

        JAXBContext context;
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(file));
        context = JAXBContext.newInstance(EventAdapter.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        EventAdapter eventAdapter = new EventAdapter(event);
        m.marshal(eventAdapter, writer);
        writer.close();
    }

    @Override
    public Event readEvent(UUID id) throws JAXBException, DateTimeFormatException {
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_TO_EVENTS).append(id).append(".xml");
        return readEvent(sb.toString());
    }

    @Override
    public Event readEvent(String pathToFile) throws JAXBException, DateTimeFormatException {
        File file = new File(pathToFile);

        JAXBContext context = JAXBContext.newInstance(EventListAdapter.class);
        Unmarshaller um = context.createUnmarshaller();
        EventAdapter eventAdapter = (EventAdapter) um.unmarshal(file);

        return eventAdapterToEvent(eventAdapter);
    }

    @Override
    public void writeEventsList(List<Event> events) throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_TO_EVENTS).append("ListOfEvents_").append(UUID.randomUUID()).append(".xml");
        File file = new File(sb.toString());

        JAXBContext context;
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(file));
        context = JAXBContext.newInstance(EventListAdapter.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(new EventListAdapter(events), writer);
        writer.close();
    }

    @Override
    public List<Event> readEventsList(UUID id) throws JAXBException, DateTimeFormatException {
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_TO_EVENTS).append("ListOfEvents_").append(id).append(".xml");
        File file = new File(sb.toString());

        EventListAdapter eventAdapterWrapper = new EventListAdapter();
        JAXBContext context = JAXBContext.newInstance(EventListAdapter.class);
        Unmarshaller um = context.createUnmarshaller();
        eventAdapterWrapper = (EventListAdapter) um.unmarshal(file);

        List<EventAdapter> eventAdapterList = eventAdapterWrapper.getEvents();
        List<Event> eventsList = new ArrayList<Event>();

        for (EventAdapter eventAdapter : eventAdapterList) {

            Event event = eventAdapterToEvent(eventAdapter);
            eventsList.add(event);
        }

        return eventsList;
    }

    @Override
    public boolean deleteEvent(UUID id) throws JAXBException, IOException {

        StringBuilder sb = new StringBuilder();
        sb.append(PATH_TO_EVENTS).append(id).append(".xml");
        Path path = Paths.get(sb.toString());

        Files.delete(path);
        return true;
    }

    private Event eventAdapterToEvent(EventAdapter eventAdapter) throws DateTimeFormatException {

        Set<PersonAdapter> personAdapterList = eventAdapter.getAttenders();
        Set<Person> attenderSet = new HashSet<Person>();

        for (PersonAdapter personAdapter: personAdapterList){
            Person attender = new Person.PersonBuilder()
                    .name(personAdapter.getName())
                    .lastName(personAdapter.getLastName())
                    .email(personAdapter.getEmail())
                    .build();
            attenderSet.add(attender);
        }

        Event event = new Event.EventBuilder()
                .id(eventAdapter.getId())
                .title(eventAdapter.getTitle())
                .description(eventAdapter.getDescription())
                .startDate(DateParser.stringToDate(eventAdapter.getStartDate()))
                .endDate(DateParser.stringToDate(eventAdapter.getEndDate()))
                .attendersSet(attenderSet)
                .build();

        return event;
    }
}