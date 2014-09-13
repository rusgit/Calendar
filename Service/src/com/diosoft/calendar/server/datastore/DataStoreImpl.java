package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.common.Event;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.*;

public class DataStoreImpl implements DataStore {

    private Map<UUID,Event> eventStore = new HashMap<UUID,Event>();
    private Map<String, List<UUID>> indexTitle = new HashMap<String, List<UUID>>();
    private Map<LocalDate, List<UUID>> indexDate = new HashMap<LocalDate, List<UUID>>();

    @Override
    public void publish(Event event) throws IllegalArgumentException  {
        if (event==null) throw new IllegalArgumentException();
// add event
        eventStore.put(event.getId(), event);
// index by title
        createIndexTitle(event);
// index by date
        createIndexDate(event);
   }

   @Override
   public Event remove(UUID id) throws IllegalArgumentException {
       if (id==null) throw new IllegalArgumentException();
// remove event
       Event event = eventStore.remove(id);
       if (event!=null) {
// remove index date
           removeIndexDate(event);
// remove index title
           removeIndexTitle(event);
       }
      return event;
   }

   @Override
   public Event getEventById(UUID id) throws IllegalArgumentException  {
        if (id==null) throw new IllegalArgumentException();
        return eventStore.get(id);
   }

   @Override
   public List<Event> getEventByTitle(String title) throws IllegalArgumentException  {
        if (title==null) throw new IllegalArgumentException();

        List<UUID> ids = indexTitle.get(title);
        List<Event> events = new ArrayList<Event>();
        if (ids!=null) {
            for (UUID id : ids) {
                Event event = eventStore.get(id);
                events.add(event);
            }
        }
        return events;
   }

   @Override
   public List<Event> getEventByDay(LocalDate day) throws IllegalArgumentException   {
        if (day==null) throw new IllegalArgumentException();

        List<UUID> ids = indexDate.get(day);
        List<Event> events = new ArrayList<Event>();
        if (ids!=null) {
            for (UUID id : ids) {
                Event event = eventStore.get(id);
                events.add(event);
            }
        }
        return events;
   }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataStoreImpl{");
        sb.append("eventStore=").append(eventStore)
          .append(", indexTitle=").append(indexTitle)
          .append(", indexDate=").append(indexDate)
          .append('}');
        return sb.toString();
    }

    private void createIndexTitle(Event event) {
        List<UUID> idsTitle = indexTitle.get(event.getTitle());
        if (idsTitle == null) {
            idsTitle = new ArrayList<UUID>();
            idsTitle.add(event.getId());
            indexTitle.put(event.getTitle(), idsTitle);
        } else {
            idsTitle.add(event.getId());
        }
    }
    private void createIndexDate(Event event) {
        LocalDate localDate = event.getStartDate().toLocalDate();
        List<UUID> idsDate = indexDate.get(localDate);
        if(idsDate==null) {
            idsDate = new ArrayList<UUID>();
            idsDate.add(event.getId());
            indexDate.put(localDate, idsDate);
        } else {
            idsDate.add(event.getId());
        }
    }
    private void removeIndexTitle(Event event) {
        List<UUID> idsTitle = indexTitle.get(event.getTitle());
        if (idsTitle.size() <= 1) {
            indexTitle.remove(event.getTitle());
        } else {
            idsTitle.remove(event.getId());
        }
    }
    private void removeIndexDate(Event event) {
        LocalDate localDate = event.getStartDate().toLocalDate();
        List<UUID> idsDate = indexDate.get(localDate);
        if (idsDate.size() <= 1) {
            indexDate.remove(localDate);
        } else {
            idsDate.remove(event.getId());
        }
    }
}
