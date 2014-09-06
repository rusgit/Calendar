package com.diosoft.calendar.datastore;

import com.diosoft.calendar.common.Event;
import java.util.*;

public class DataStoreImpl implements DataStore {

    private Map<UUID,Event> eventStore = new HashMap<UUID,Event>();
    private Map<String, List<UUID>> indexTitle = new HashMap<String, List<UUID>>();
    private Map<Calendar, List<UUID>> indexDate = new HashMap<Calendar, List<UUID>>();

    @Override
    public void publish(Event event) {
        if (event==null) throw new IllegalArgumentException();

// add event
        eventStore.put(event.getId(), event);

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
   public void remove(UUID id) {
       if (id==null) throw new IllegalArgumentException();

       Event event = eventStore.get(id);
       if (event==null) return;

// remove index title. If indexTitle contained only one id for title(key), than we can remove key and value.
// if indexTitle contained list of id for title(more than one element), we remove only concrete id from list id.
        List<UUID> idsTitle = indexTitle.get(event.getTitle());
       if (idsTitle.size()==1) {
            indexTitle.remove(event.getTitle());
       } else {
           idsTitle.remove(id);
       }
// remove index date. If indexDate contained only one id for date(key), than we can remove key and value.
// if indexDate contained list of id for date(more than one element), we remove only concrete id from list id.
        List<UUID> idsDate = indexDate.get(event.getStartDate());
        if (idsDate.size()==1) {
            indexDate.remove(event.getStartDate());
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
   public List<Event> getEventByDate(Calendar startDate) {
        if (startDate==null) throw new IllegalArgumentException();

        List<UUID> ids = indexDate.get(startDate);
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
        sb.append("eventStore=").append(eventStore);
        sb.append(", indexTitle=").append(indexTitle);
        sb.append(", indexDate=").append(indexDate);
        sb.append('}');
        return sb.toString();
    }
}
