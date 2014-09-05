package com.diosoft.calendar.datastore;

import com.diosoft.calendar.common.Event;
import java.util.*;

public class DataStoreImpl implements DataStore{

    private Map<UUID,Event> dataStore = new HashMap<UUID,Event>();
    private Map<String, List<UUID>> indexTitle = new HashMap<String, List<UUID>>();
    private Map<Calendar, List<UUID>> indexDate = new HashMap<Calendar, List<UUID>>();

    @Override
    public void publish(Event event) {
        if (event==null) throw new IllegalArgumentException();
        dataStore.put(event.getId(), event);

// index by title
        List<UUID> uuidsTitle = indexTitle.get(event.getTitle());
        if(uuidsTitle==null) {
            uuidsTitle = new ArrayList<UUID>();
            uuidsTitle.add(event.getId());
            indexTitle.put(event.getTitle(),uuidsTitle);
        } else {
            uuidsTitle.add(event.getId());
        }

// index by date
        List<UUID> uuidsDate = indexDate.get(event.getStartDate());
        if(uuidsDate==null) {
            uuidsDate = new ArrayList<UUID>();
            uuidsDate.add(event.getId());
            indexDate.put(event.getStartDate(),uuidsDate);
        } else {
            uuidsDate.add(event.getId());
        }

    }

    @Override
    public Event getEventById(UUID id) {
        if (id==null) throw new IllegalArgumentException();
        return dataStore.get(id);
    }

    @Override
    public List<Event> getEventByTitle(String title) {
        if (title==null) throw new IllegalArgumentException();

        List<UUID> ids = indexTitle.get(title);
        List<Event> events = new ArrayList<Event>();
        for (UUID id :ids){
            Event event = dataStore.get(id);
            events.add(event);
        }
        return events;
    }

    @Override
    public List<Event> getEventByDate(Calendar startDate) {
        if (startDate==null) throw new IllegalArgumentException();

        List<UUID> ids = indexDate.get(startDate);
        List<Event> events = new ArrayList<Event>();
        for (UUID id :ids){
            Event event = dataStore.get(id);
            events.add(event);
        }
        return events;
    }

    @Override
    public void remove(UUID id) {
        if (id==null) throw new IllegalArgumentException();
        dataStore.remove(id);
    }
}
