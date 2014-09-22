package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.adapter.EventAdapter;
import com.diosoft.calendar.server.adapter.EventAdapterWrapper;
import com.diosoft.calendar.server.adapter.PersonAdapter;
import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
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

    @Override
    public void writeEvent(Event event) throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        sb.append("Service/resources/events/").append(event.getId()).append(".xml");
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
    public Event readEvent(UUID id) throws JAXBException {
        StringBuilder sb = new StringBuilder();
        sb.append("Service/resources/events/").append(id).append(".xml");
        File file = new File(sb.toString());

        EventAdapter eventAdapter = new EventAdapter();
        JAXBContext context = JAXBContext.newInstance(EventAdapterWrapper.class);
        Unmarshaller um = context.createUnmarshaller();
        eventAdapter = (EventAdapter) um.unmarshal(file);

        return eventAdapterToEvent(eventAdapter);
    }

    @Override
    public void writeEventsList(List<Event> events) throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        sb.append("Service/resources/events/").append("ListOfEvents_").append(UUID.randomUUID()).append(".xml");
        File file = new File(sb.toString());

        JAXBContext context;
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(file));
        context = JAXBContext.newInstance(EventAdapterWrapper.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(new EventAdapterWrapper(events), writer);
        writer.close();
    }

    @Override
    public List<Event> readEventsList(UUID id) throws JAXBException {
        StringBuilder sb = new StringBuilder();
        sb.append("Service/resources/events/").append("ListOfEvents_").append(id).append(".xml");
        File file = new File(sb.toString());

        EventAdapterWrapper eventAdapterWrapper = new EventAdapterWrapper();
        JAXBContext context = JAXBContext.newInstance(EventAdapterWrapper.class);
        Unmarshaller um = context.createUnmarshaller();
        eventAdapterWrapper = (EventAdapterWrapper) um.unmarshal(file);

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
        sb.append("Service/resources/events/").append(id).append(".xml");
        Path path = Paths.get(sb.toString());

        Files.delete(path);
        return true;
    }

    private Event eventAdapterToEvent(EventAdapter eventAdapter) {

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
                .startDate(eventAdapter.getStartDate())
                .endDate(eventAdapter.getEndDate())
                .attendersSet(attenderSet)
                .build();

        return event;
    }
}