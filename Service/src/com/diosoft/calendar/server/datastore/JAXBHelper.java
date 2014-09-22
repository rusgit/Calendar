package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.common.Event;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface JAXBHelper {

    /**
     * Adds event to persistence storage (cerate xml file)
     * @param event
     * @param file
     * @throws IOException
     * @throws JAXBException
     */
    void writeEvent(Event event) throws IOException, JAXBException;

    /**
     * Adds list of events to persistent storage (cerate xml file)
     * @param events
     * @param selectedFile
     * @throws IOException
     * @throws JAXBException
     */
    void writeEventsList(List<Event> events) throws IOException, JAXBException;

    /**
     * Read file and create Event (read xml file)
     * @param file
     * @return event
     * @throws JAXBException
     */
    Event readEvent(UUID id) throws JAXBException;

    /**
     * Reads file and create list of events (read xml file)
     * @param file
     * @return list of events
     * @throws JAXBException
     */
    List<Event> readEventsList(UUID id) throws JAXBException;

    /**
     * Deletes file from persistent storage (delete xml file)
     * @param file
     * @return true if file successfully deleted
     * @throws JAXBException
     */
    boolean deleteEvent(UUID id) throws JAXBException, IOException;
}