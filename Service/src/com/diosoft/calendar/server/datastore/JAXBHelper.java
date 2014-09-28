package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.exception.DateTimeFormatException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

//local code review (vtegza): remove "Event" from method names - it is already in declaration (parameters) @ 9/28/2014
public interface JAXBHelper {
//local code review (vtegza): no need in docs if method names tell everything what method do @ 9/28/2014
    /**
     * Adds event to persistence storage (create xml file)
     * @param event
     * @throws IOException
     * @throws JAXBException
     */
    void writeEvent(Event event) throws IOException, JAXBException;

    /**
     * Adds list of events to persistent storage (create xml file)
     * @param events
     * @throws IOException
     * @throws JAXBException
     */
    void writeEventsList(List<Event> events) throws IOException, JAXBException;

    /**
     * Read file and create Event (read xml file)
     * @param id of event
     * @return event
     * @throws JAXBException
     */
    Event readEvent(UUID id) throws JAXBException, DateTimeFormatException;

    /**
     * Read file and create Event (read xml file)
     * @param pathToFile
     * @return event
     * @throws JAXBException
     */
    Event readEvent(String pathToFile) throws JAXBException, DateTimeFormatException;

    /**
     * Reads file and create list of events (read xml file)
     * @param id
     * @return list of events
     * @throws JAXBException
     */
    List<Event> readEventsList(UUID id) throws JAXBException, DateTimeFormatException;

    /**
     * Deletes file from persistent storage (delete xml file)
     * @param id
     * @return true if file successfully deleted
     * @throws JAXBException
     */
    boolean deleteEvent(UUID id) throws JAXBException, IOException;

    /**
     * Reads all xml files with events
     * @return List of events
     * @throws IOException
     * @throws JAXBException
     */
    List<Event>  readAllEventsFromXMLResources() throws JAXBException, IOException, DateTimeFormatException;
}