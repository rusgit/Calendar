package com.diosoft.calendar.service;

import com.diosoft.calendar.common.Event;
import com.diosoft.calendar.datastore.DataStore;
import org.joda.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CalendarServiceImpl implements CalendarService {

    private final DataStore dataStore;

    public CalendarServiceImpl(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void add(Event event) {
        if (event == null) throw new IllegalArgumentException();
        dataStore.publish(event);
    }

    @Override
    public void remove(UUID id) {
        if (id == null) throw new IllegalArgumentException();
        dataStore.remove(id);
    }

    @Override
    public List<Event> searchByTitle(String title) {
        if (title == null) throw new IllegalArgumentException();
        List<Event> events = dataStore.getEventByTitle(title);
        return events;
    }

    @Override
    public List<Event> searchByDay(LocalDate day) {
        if (day == null) throw new IllegalArgumentException();
        List<Event> events = dataStore.getEventByDate(day);
        return events;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CalendarServiceImpl{");
        sb.append("dataStore=").append(dataStore.toString()).append('}');
        return sb.toString();
    }
}
