package com.diosoft.calendar.common;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class Event implements Comparable<Event> {

    private final UUID id;
    private final String title;
    private final String description;
    private final Calendar startDate;
    private final Calendar endDate;
    private final List<Person> attenders;

    public UUID getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public Calendar getStartDate() {
        return startDate;
    }
    public Calendar getEndDate() {
        return endDate;
    }
    public List<Person> getAttender() {
        return attenders;
    }

    private Event(EventBuilder eventBuilder) {
        this.id = eventBuilder.id;
        this.title = eventBuilder.title;
        this.description = eventBuilder.description;
        this.startDate = eventBuilder.startDate;
        this.endDate = eventBuilder.endDate;
        this.attenders = eventBuilder.attenders;
    }

    // We have UUID, that's why we can use only compare by id in equals method
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Event)) return false;
        if (this == obj) return true;

        Event event = (Event) obj;

        if (!id.equals(event.id)) return false;

        return true;
    }

    // We have UUID, that's why we can use only id in hashCode method
    @Override
    public int hashCode() {
        return id.hashCode();
    }


    // Compare: first by startDate, after that by endDate, after that by title and for end - by description.
    // Without compare by id and list of attenders
    @Override
    public int compareTo(Event event) {
        if (event == null) return 1;
        int result = startDate.compareTo(event.startDate);
        if (result != 0) return (int) (result / Math.abs(result));
        result = endDate.compareTo(event.endDate);
        if (result != 0) return (int) (result / Math.abs(result));
        result = title.compareTo(event.title);
        if (result != 0) return (int) (result / Math.abs(result));
        result = description.compareTo(event.description);

        return (result != 0) ? (int) (result / Math.abs(result)) : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Event { ");
        sb.append(id).append(", ")
                .append(title).append(", ")
                .append(description).append(", ")
                .append(startDate.getTime()).append(", ")
                .append(endDate.getTime()).append(", ")
                .append(attenders).append(" } \n");

        return sb.toString();
    }

    public static class EventBuilder {
        private UUID id;
        private String title;
        private String description;
        private Calendar startDate;
        private Calendar endDate;
        private List<Person> attenders;

        public EventBuilder() {
        }

        public EventBuilder(Event originalEvent) {
            this.id = originalEvent.id;
            this.title = originalEvent.title;
            this.description = originalEvent.description;
            this.startDate = originalEvent.startDate;
            this.endDate = originalEvent.endDate;
            this.attenders = originalEvent.attenders;
        }

        public EventBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public EventBuilder title(String title) {
            this.title = title;
            return this;
        }

        public EventBuilder description(String description) {
            this.description = description;
            return this;
        }

        public EventBuilder startDate(Calendar startDate) {
            this.startDate = startDate;
            return this;
        }

        public EventBuilder endDate(Calendar endDate) {
            this.endDate = endDate;
            return this;
        }

        public EventBuilder attendersList(List<Person> attenders) {
            this.attenders = attenders;
            return this;
        }

        public Event build() {
            return new Event(this);
        }


    }
}



