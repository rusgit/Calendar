package com.diosoft.calendar.server.adapter;

import com.diosoft.calendar.server.common.Event;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "events")
public class EventAdapterWrapper {

    @XmlElement(name = "event", type = EventAdapter.class)
    private List<EventAdapter> eventsAdapter = new ArrayList<EventAdapter>();

    public EventAdapterWrapper() {}

    public EventAdapterWrapper(List<Event> events) {
        for (Event event: events) {
            EventAdapter eventAdapter = new EventAdapter(event);
            eventsAdapter.add(eventAdapter);
        }
    }

    public List<EventAdapter> getEvents() {
        return eventsAdapter;
    }
    public void setEvents(List<EventAdapter> events) {
        this.eventsAdapter = events;
    }
}