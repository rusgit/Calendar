package com.diosoft.calendar.server.adapter;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.PeriodOfEvent;
import com.diosoft.calendar.server.util.DateParser;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@XmlRootElement(name = "event")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventAdapter implements Comparable<EventAdapter>, Serializable {

    @XmlAttribute
    private UUID id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;

    @XmlElementWrapper(name = "attenders")
    @XmlElement(name = "attender")
    private Set<PersonAdapter> attenders = new HashSet<>();

    @XmlElementWrapper(name = "periods")
    @XmlElement(name = "period")
    private Set<PeriodOfEvent> period = new HashSet<>();

    public UUID getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getStartDate() {
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public Set<PersonAdapter> getAttenders() {
        return attenders;
    }
    public Set<PeriodOfEvent> getPeriod() {
        return period;
    }

    public EventAdapter(){};

    public EventAdapter(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.startDate = DateParser.dateToString(event.getStartDate());
        this.endDate = DateParser.dateToString(event.getEndDate());
        this.attenders.addAll(event.getAttenders().stream().map(PersonAdapter::new).collect(Collectors.toList()));
        this.period = event.getPeriod();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof EventAdapter)) return false;
        if (this == obj) return true;

        EventAdapter event = (EventAdapter) obj;

        if (period != null ? !period.equals(event.period) : event.period != null) return false;
        if (attenders != null ? !attenders.equals(event.attenders) : event.attenders != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null) return false;
        if (endDate != null ? !endDate.equals(event.endDate) : event.endDate != null) return false;
        if (startDate != null ? !startDate.equals(event.startDate) : event.startDate != null) return false;
        if (title != null ? !title.equals(event.title) : event.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (attenders != null ? attenders.hashCode() : 0);
        result = 31 * result + (period != null ? period.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(EventAdapter event) {
        if (event == null) return 1;
        int result = startDate.compareTo(event.startDate);
        if (result != 0) return result / Math.abs(result);
        result = endDate.compareTo(event.endDate);
        if (result != 0) return result / Math.abs(result);
        result = title.compareTo(event.title);
        if (result != 0) return result / Math.abs(result);
        result = description.compareTo(event.description);

        return (result != 0) ? result / Math.abs(result) : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EventAdapter { ");
        sb.append(id).append(", ")
                .append(title).append(", ")
                .append(description).append(", ")
                .append(startDate).append(", ")
                .append(endDate).append(", ")
                .append(period).append(", ")
                .append(attenders).append(" } \n");

        return sb.toString();
    }
}