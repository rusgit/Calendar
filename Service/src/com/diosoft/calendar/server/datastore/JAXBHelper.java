package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.exception.DateTimeFormatException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface JAXBHelper {
    void write(Event event) throws IOException, JAXBException;

    void writeEventsList(List<Event> events) throws IOException, JAXBException;

    Event read(UUID id) throws JAXBException, DateTimeFormatException;

    Event read(String pathToFile) throws JAXBException, DateTimeFormatException;

    List<Event> readEventsList(UUID id) throws JAXBException, DateTimeFormatException;

    boolean delete(UUID id) throws JAXBException, IOException;

    List<Event>  readAllEventsFromXMLResources() throws JAXBException, IOException, DateTimeFormatException;
}