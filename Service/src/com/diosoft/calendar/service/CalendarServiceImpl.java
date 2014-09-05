package com.diosoft.calendar.service;

import com.diosoft.calendar.common.Event;
import com.diosoft.calendar.datastore.DataStore;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CalendarServiceImpl implements CalendarService {

    private DataStore dataStore;

    public CalendarServiceImpl(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void add(Event event) {

    }

    @Override
    public void remove(UUID id) {

    }

    @Override
    public List<Event> searchByTitle(String title) {
        return null;
    }

    @Override
    public List<Event> searchByDay(Date day) {
        return null;
    }
}
