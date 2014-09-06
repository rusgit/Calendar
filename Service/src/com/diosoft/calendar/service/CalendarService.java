package com.diosoft.calendar.service;

import com.diosoft.calendar.common.Event;
import org.joda.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CalendarService {

    void add(Event event);

    void remove(UUID id);

    List<Event> searchByTitle(String title);

    List<Event> searchByDay(LocalDate day);
}
