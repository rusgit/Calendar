package com.diosoft.calendar.datastore;

import com.diosoft.calendar.common.Event;
import org.joda.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DataStore {

    void publish(Event event);

    void remove(UUID id);

    Event getEventById(UUID id);

    List<Event> getEventByTitle(String title);

    List<Event> getEventByDate(LocalDate day);
}
