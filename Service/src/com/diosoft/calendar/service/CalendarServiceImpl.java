package com.diosoft.calendar.service;

import com.diosoft.calendar.common.Event;
import com.diosoft.calendar.datastore.DataStore;
import java.util.Date;
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
        return null;
    }

    @Override
    public List<Event> searchByDay(Date day) {
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CalendarServiceImpl{");
        sb.append("dataStore=").append(dataStore.toString());
        sb.append('}');
        return sb.toString();
    }
}
