package com.diosoft.calendar.datastore;

import com.diosoft.calendar.common.Event;
import org.joda.time.LocalDate;
import java.util.*;

public class DataStoreImpl implements DataStore {

    private Map<UUID,Event> eventStore = new HashMap<UUID,Event>();
    private Map<String, List<UUID>> indexTitle = new HashMap<String, List<UUID>>();
    private Map<LocalDate, List<UUID>> indexDate = new HashMap<LocalDate, List<UUID>>();

    @Override
    public void publish(Event event) {
        if (event==null) throw new IllegalArgumentException();

// add event
        UUID id = event.getId();
        eventStore.put(id, event);

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
        LocalDate localDate = event.getStartDate().toLocalDate();
        List<UUID> uuidsDate = indexDate.get(localDate);
        if(uuidsDate==null) {
            uuidsDate = new ArrayList<UUID>();
            uuidsDate.add(event.getId());
            indexDate.put(localDate, uuidsDate);
        } else {
            uuidsDate.add(event.getId());
        }
   }

   @Override
   public void remove(UUID id) {
       if (id==null) throw new IllegalArgumentException();

       Event event = eventStore.get(id);
       if (event==null) return;

// remove index title
       List<UUID> idsTitle = indexTitle.get(event.getTitle());
       if (idsTitle.size()==1) {
            indexTitle.remove(event.getTitle());
       } else {
           idsTitle.remove(id);
       }
// remove index date
       LocalDate localDate = event.getStartDate().toLocalDate();
       List<UUID> idsDate = indexDate.get(localDate);
       if (idsDate.size()==1) {
           indexDate.remove(localDate);
       } else {
           idsDate.remove(id);
       }
// remove event in eventStore
        eventStore.remove(id);
   }

   @Override
   public Event getEventById(UUID id) {
        if (id==null) throw new IllegalArgumentException();
        return eventStore.get(id);
   }

   @Override
   public List<Event> getEventByTitle(String title) {
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
   public List<Event> getEventByDate(LocalDate day) {
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
}
