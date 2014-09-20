package com.diosoft.calendar.server.service;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.datastore.DataStore;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.exception.OrderOfArgumentsException;
import com.diosoft.calendar.server.exception.ValidationException;
import com.diosoft.calendar.server.util.DateParser;
import com.diosoft.calendar.server.util.EventValidator;
import org.apache.log4j.Logger;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CalendarServiceImpl implements CalendarService {

    private static final Logger LOG = Logger.getLogger(CalendarServiceImpl.class);
    private final DataStore dataStore;

    public CalendarServiceImpl(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void add(Event event) throws RemoteException, IllegalArgumentException, ValidationException {
        if (event == null) throw new IllegalArgumentException();

// Validate
        LOG.info("Validation event with title '" + event.getTitle() + "'");
        EventValidator.validate(event);
        LOG.info("Event successfully validated");
// Add
        LOG.info("Adding event with title '" + event.getTitle() + "'");
        dataStore.publish(event);
        LOG.info("Event successfully added");
    }

   @Override
   public Event createEvent(String[] descriptions, Set<Person> attenders) throws RemoteException, IllegalArgumentException, DateTimeFormatException, ValidationException {
        if (descriptions == null || attenders == null || descriptions.length!=4) throw new IllegalArgumentException();

        LocalDateTime startDate = DateParser.stringToDate(descriptions[2]);
        LocalDateTime endDate = DateParser.stringToDate(descriptions[3]);

        LOG.info("Creating event with title '" + descriptions[0] + "'");
        Event event = new Event.EventBuilder()
                .id(UUID.randomUUID()).title(descriptions[0])
                .description(descriptions[1])
                .startDate(startDate)
                .endDate(endDate)
                .attendersSet(attenders).build();
        LOG.info("Event successfully created");

        add(event);
        return event;
   }

   @Override
   public Event createEventForAllDay(String[] descriptions, Set<Person> attenders) throws RemoteException, IllegalArgumentException, DateTimeFormatException, ValidationException {
        if (descriptions == null || attenders == null || descriptions.length < 3 || descriptions.length > 4 ) throw new IllegalArgumentException();

        String startDay = descriptions[2] + " 00:00";
        String endDate = null;

// one day "for all day"
        if (descriptions.length==3) {
            LocalDateTime tempStartDate = DateParser.stringToDate(startDay);
            LocalDateTime tempEndDate = tempStartDate.plusDays(1);
            endDate = DateParser.dateToString(tempEndDate);
        }

// interval of days "for all day"
        if (descriptions.length==4) {
            String endDay = descriptions[3] + " 00:00";
            LocalDateTime tempEndDate = DateParser.stringToDate(endDay);
            tempEndDate = tempEndDate.plusDays(1);
            endDate = DateParser.dateToString(tempEndDate);
        }

        String[] preparedDescriptions = { descriptions[0], descriptions[1], startDay, endDate };
        return createEvent(preparedDescriptions,attenders);
   }

   @Override
   public Event remove(UUID id) throws RemoteException, IllegalArgumentException {
        if (id == null) throw new IllegalArgumentException();

        LOG.info("Removing event with id: '" + id + "'");
        Event event = dataStore.remove(id);
        if (event==null) {
            LOG.info("There is no such Event");
        } else {
            LOG.info("Event successfully removed");
        }
        return event;
   }

   @Override
   public List<Event> searchByTitle(String title) throws RemoteException, IllegalArgumentException {
        if (title == null) throw new IllegalArgumentException();

        LOG.info("Searching by title '" + title +"':");
        List<Event> events = dataStore.getEventByTitle(title);
        if (events.size()<1) {
            LOG.info("Events not found!");
        } else {
            LOG.info("Found " + events.size()+ " events");
        }
        return events;
   }

   @Override
   public List<Event> searchByDay(LocalDate day) throws RemoteException, IllegalArgumentException {
        if (day == null) throw new IllegalArgumentException();

        LOG.info("Searching by day '" + day +"':");
        List<Event> events = dataStore.getEventByDay(day);
        if (events.size()<1) {
            LOG.info("Events not found!");
            return events;
        }
        LOG.info("Found " + events.size()+ " events");

        return events;
   }

   @Override
   public List<Event> searchByAttender(Person attender) throws RemoteException, IllegalArgumentException {
        if (attender == null) throw new IllegalArgumentException();

        LOG.info("Searching by attender '" + attender.getName() +"':");
        List<Event> events = dataStore.getEventByAttender(attender);
        if (events.size()<1) {
            LOG.info("Events not found!");
            return events;
        }
        LOG.info("Found " + events.size()+ " events");

        return events;
   }

   @Override
   public List<Event> searchByAttenderIntoPeriod(Person attender, LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, IllegalArgumentException, OrderOfArgumentsException {
        if (attender == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        LOG.info("Searching events by attender '" + attender.getName() + " " + attender.getLastName() + "' into period from " + startDate + " to " + endDate);
        List<Event> eventListByAttender = searchByAttender(attender);
        List<Event> eventListByAttenderIntoPeriod = new ArrayList<Event>();
        for (Event event : eventListByAttender) {
            if (event.getStartDate().isAfter(startDate) && event.getStartDate().isBefore(endDate) || // event start date into period
                    event.getEndDate().isAfter(startDate) && event.getEndDate().isBefore(endDate) || // event end date into period
                    event.getStartDate().isBefore(startDate) && event.getEndDate().isAfter(endDate)) { // period into event
                    eventListByAttenderIntoPeriod.add(event);
            }
        }
        if (eventListByAttenderIntoPeriod.isEmpty())
            LOG.info("Events not found!");
        else
            LOG.info("Found " + eventListByAttenderIntoPeriod.size()+ " events");

        return eventListByAttenderIntoPeriod;
   }

   @Override
   public boolean isAttenderFree(Person attender, LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, IllegalArgumentException, OrderOfArgumentsException {
        if (attender == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        LOG.info("Checking is attender '" + attender.getName() + " " + attender.getLastName() + "' free from " + startDate + " to " + endDate);
        List<Event> eventListByAttender = searchByAttenderIntoPeriod(attender, startDate, endDate);
        if (eventListByAttender.isEmpty()) {
                LOG.info("Attender free");
                return true;
        }
        LOG.info("Attender not free");

        return false;
   }
}
