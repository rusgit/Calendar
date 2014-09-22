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

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CalendarServiceImpl implements CalendarService {

    private static final Logger LOG = Logger.getLogger(CalendarServiceImpl.class);
    private final DataStore dataStore;

    public CalendarServiceImpl(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void add(Event event) throws RemoteException, IOException, IllegalArgumentException,
            ValidationException, JAXBException {
        if (event == null) throw new IllegalArgumentException();

//  Validate
        LOG.info("Validation event with title '" + event.getTitle() + "'");
        EventValidator.validate(event);
        LOG.info("Event successfully validated");
//  Add
        LOG.info("Adding event with title '" + event.getTitle() + "'");
        dataStore.publish(event);
        LOG.info("Event successfully added");
    }

    @Override
    public Event createEvent(String[] descriptions, Set<Person> attenders) throws RemoteException, IOException,
            IllegalArgumentException, DateTimeFormatException, ValidationException, JAXBException {
        if (descriptions == null || attenders == null || descriptions.length != 4) throw new IllegalArgumentException();

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
    public Event createEventForAllDay(String[] descriptions, Set<Person> attenders) throws RemoteException, IOException,
            IllegalArgumentException, DateTimeFormatException, ValidationException, JAXBException {
        if (descriptions == null || attenders == null || descriptions.length < 3 || descriptions.length > 4)
            throw new IllegalArgumentException();

        String startDay = descriptions[2] + " 00:00";
        String endDate = null;

//  one day "for all day"
        if (descriptions.length == 3) {
            LocalDateTime tempStartDate = DateParser.stringToDate(startDay);
            LocalDateTime tempEndDate = tempStartDate.plusDays(1);
            endDate = DateParser.dateToString(tempEndDate);
        }

//  interval of days "for all day"
        if (descriptions.length == 4) {
            String endDay = descriptions[3] + " 00:00";
            LocalDateTime tempEndDate = DateParser.stringToDate(endDay);
            tempEndDate = tempEndDate.plusDays(1);
            endDate = DateParser.dateToString(tempEndDate);
        }

        String[] preparedDescriptions = {descriptions[0], descriptions[1], startDay, endDate};
        return createEvent(preparedDescriptions, attenders);
    }

    @Override
    public Event remove(UUID id) throws RemoteException, IOException, IllegalArgumentException, JAXBException {
        if (id == null) throw new IllegalArgumentException();

        LOG.info("Removing event with id: '" + id + "'");
        Event event = dataStore.remove(id);
        if (event == null) {
            LOG.info("There is no such Event");
        } else {
            LOG.info("Event successfully removed");
        }
        return event;
    }

    @Override
    public List<Event> searchByTitle(String title) throws RemoteException, IllegalArgumentException {
        if (title == null) throw new IllegalArgumentException();

        LOG.info("Searching by title '" + title + "':");
        List<Event> events = dataStore.getEventByTitle(title);
        if (events.size() < 1) {
            LOG.info("Events not found!");
        } else {
            LOG.info("Found " + events.size() + " events");
        }
        return events;
    }

    @Override
    public List<Event> searchByDay(LocalDate day) throws RemoteException, IllegalArgumentException {
        if (day == null) throw new IllegalArgumentException();

        LOG.info("Searching by day '" + day + "':");
        List<Event> events = dataStore.getEventByDay(day);
        if (events.size() < 1) {
            LOG.info("Events not found!");
            return events;
        }
        LOG.info("Found " + events.size() + " events");

        return events;
    }

    @Override
    public List<Event> searchByAttender(Person attender) throws RemoteException, IllegalArgumentException {
        if (attender == null) throw new IllegalArgumentException();

        LOG.info("Searching by attender '" + attender.getName() + "':");
        List<Event> events = dataStore.getEventByAttender(attender);
        if (events.size() < 1) {
            LOG.info("Events not found!");
            return events;
        }
        LOG.info("Found " + events.size() + " events");

        return events;
    }

    @Override
    public List<Event> searchByAttenderIntoPeriod(Person attender, LocalDateTime startDate, LocalDateTime endDate)
            throws RemoteException, IllegalArgumentException, OrderOfArgumentsException {
        if (attender == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        LOG.info("Searching events by attender '" + attender.getName() + " " + attender.getLastName() + "' into period from " +
                DateParser.dateToString(startDate) + " to " + DateParser.dateToString(endDate));
        List<Event> eventListByAttender = searchByAttender(attender);
        List<Event> eventListByAttenderIntoPeriod = new ArrayList<Event>();
        for (Event event : eventListByAttender) {
            if (event.getStartDate().isAfter(startDate) && event.getStartDate().isBefore(endDate) // event start date into period
                    || event.getEndDate().isAfter(startDate) && event.getEndDate().isBefore(endDate) // event end date into period
                    || event.getStartDate().isBefore(startDate) && event.getEndDate().isAfter(endDate) // period into event
                    || event.getStartDate().equals(startDate) || event.getEndDate().equals(endDate)) {
                eventListByAttenderIntoPeriod.add(event);
            }
        }
        if (eventListByAttenderIntoPeriod.isEmpty())
            LOG.info("Events not found!");
        else
            LOG.info("Found " + eventListByAttenderIntoPeriod.size() + " events");

        return eventListByAttenderIntoPeriod;
    }

    @Override
    public Set<Event> searchIntoPeriod(LocalDate startDay, LocalDate endDay) throws RemoteException, IllegalArgumentException, OrderOfArgumentsException {
        if (startDay == null || endDay == null) throw new IllegalArgumentException();
        if (startDay.isAfter(endDay)) throw new OrderOfArgumentsException();

        LOG.info("Searching events into period from '" + startDay + "' to" + endDay);
        Set<Event> eventSetIntoPeriod = new HashSet<Event>();

//  get all events from period without time (use getEventByDay method of DataStore which use index map)
        while (startDay.isBefore(endDay) || startDay.equals(endDay)) {
            List<Event> tempEventList = searchByDay(startDay);
            eventSetIntoPeriod.addAll(tempEventList);
            startDay = startDay.plusDays(1);
        }
        LOG.info("Found " + eventSetIntoPeriod.size() + " events");
        return eventSetIntoPeriod;
    }

    @Override
    public List<List<LocalDateTime>> searchFreeTime(LocalDateTime startDate, LocalDateTime endDate) throws IllegalArgumentException, OrderOfArgumentsException, RemoteException {
        if (startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        final int MINUTE_INTERVAL = 15;
        List<List<LocalDateTime>> freeTimeList = new LinkedList<List<LocalDateTime>>();
        freeTimeList.add(Arrays.asList(startDate, endDate));
        Set<Event> eventSet = searchIntoPeriod(startDate.toLocalDate(), endDate.toLocalDate());

        LOG.info("Searching free time into period from '" + startDate + "' to" + endDate);
        for(Event event:eventSet) {
            ListIterator<List<LocalDateTime>> it = freeTimeList.listIterator();
            while (it.hasNext()) {
                List<LocalDateTime> freeTimeInterval = it.next();
                // remove current freeTimeInterval if event overlaps it whit +- MINUTE_INTERVAL
                if (event.getStartDate().isBefore(freeTimeInterval.get(0).plusMinutes(MINUTE_INTERVAL))
                        && event.getEndDate().isAfter(freeTimeInterval.get(1).minusMinutes(MINUTE_INTERVAL)))
                    it.remove();
                // shorten current freeTimeInterval from the beginning, if the event overlaps the beginning of his
                if (event.getStartDate().isBefore(freeTimeInterval.get(0).plusMinutes(MINUTE_INTERVAL))
                        && event.getEndDate().isAfter(freeTimeInterval.get(0))
                        && (event.getEndDate().isBefore(freeTimeInterval.get(1).minusMinutes(MINUTE_INTERVAL))
                        || event.getEndDate().isEqual(freeTimeInterval.get(1).minusMinutes(MINUTE_INTERVAL))))
                    freeTimeList.get(freeTimeList.indexOf(freeTimeInterval)).set(0, event.getEndDate());
                // shorten current freeTimeInterval from the ending, if the event overlaps the ending of his
                if ((event.getStartDate().isAfter(freeTimeInterval.get(0).plusMinutes(MINUTE_INTERVAL))
                        || event.getStartDate().isEqual(freeTimeInterval.get(0).plusMinutes(MINUTE_INTERVAL)))
                        && event.getStartDate().isBefore(freeTimeInterval.get(1))
                        && event.getEndDate().isAfter(freeTimeInterval.get(1).minusMinutes(MINUTE_INTERVAL)))
                    freeTimeList.get(freeTimeList.indexOf(freeTimeInterval)).set(1, event.getStartDate());
                // add new interval and shorten current freeTimeInterval
                if ( (event.getStartDate().isAfter(freeTimeInterval.get(0).plusMinutes(MINUTE_INTERVAL))
                        || event.getStartDate().isEqual(freeTimeInterval.get(0).plusMinutes(MINUTE_INTERVAL)))
                        && (event.getEndDate().isBefore(freeTimeInterval.get(1).minusMinutes(MINUTE_INTERVAL))
                        || event.getEndDate().isEqual(freeTimeInterval.get(1).minusMinutes(MINUTE_INTERVAL)))) {
                    it.add(Arrays.asList(event.getEndDate(), freeTimeInterval.get(1)));
                    freeTimeList.get(freeTimeList.indexOf(freeTimeInterval)).set(1, event.getStartDate());
                }
            }
        }
        LOG.info("Found "  + freeTimeList.size() + " free intervals");
        return freeTimeList;
    }

    @Override
    public List<List<LocalDateTime>> searchFreeTime2(LocalDateTime startDate, LocalDateTime endDate)
            throws RemoteException, IllegalArgumentException, OrderOfArgumentsException {
        if (startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        final int DISCRET_OF_SEARCH = 15;

        Set<Event> eventListIntoPeriod = searchIntoPeriod(startDate.toLocalDate(), endDate.toLocalDate());

        List<List<LocalDateTime>> freeIntervalList = new ArrayList<List<LocalDateTime>>();
        LocalDateTime tempStartDate = LocalDateTime.from(startDate);
        LOG.info("Searching free time into period from " +
                DateParser.dateToString(startDate) + " to " + DateParser.dateToString(endDate));
        while (tempStartDate.isBefore(endDate)) {
            LocalDateTime tempEndDate = tempStartDate.plusMinutes(DISCRET_OF_SEARCH);
            boolean isFree = true;
            for (Event event : eventListIntoPeriod) {
                if (event.getStartDate().isAfter(tempStartDate) && event.getStartDate().isBefore(tempEndDate) ||
                        event.getEndDate().isAfter(tempStartDate) && event.getEndDate().isBefore(tempEndDate) ||
                        event.getStartDate().isBefore(tempStartDate) && event.getEndDate().isAfter(tempEndDate) ||
                        event.getStartDate().equals(tempStartDate) || event.getEndDate().equals(tempEndDate)) {
                    isFree = false;
                }
            }
            if (isFree) {
                freeIntervalList.add(Arrays.asList(tempStartDate,tempEndDate));
            }
            tempStartDate = tempStartDate.plusMinutes(DISCRET_OF_SEARCH);
        }
        LOG.info("Found "  + mergeSolidInterval(freeIntervalList).size() + " free intervals");
        return mergeSolidInterval(freeIntervalList);
    }

    @Override
    public boolean isAttenderFree(Person attender, LocalDateTime startDate, LocalDateTime endDate)
            throws RemoteException, IllegalArgumentException, OrderOfArgumentsException {
        if (attender == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        LOG.info("Checking is attender '" + attender.getName() + " " + attender.getLastName() + "' free from " +
                DateParser.dateToString(startDate) + " to " + DateParser.dateToString(endDate));
        List<Event> eventListByAttender = searchByAttenderIntoPeriod(attender, startDate, endDate);
        if (eventListByAttender.isEmpty()) {
            LOG.info("Attender free");
            return true;
        }
        LOG.info("Attender not free");

        return false;
    }

    private List<List<LocalDateTime>> mergeSolidInterval(List<List<LocalDateTime>> intervalList) {

        List<List<LocalDateTime>> solidFreeIntervalList = new ArrayList<List<LocalDateTime>>();

        LocalDateTime left  = intervalList.get(0).get(0);
        LocalDateTime rigth = intervalList.get(0).get(1);

        for (int i = 0; i < intervalList.size(); i++) {
//  if this NOT last iteration
            if (!(i==intervalList.size()-1)) {
                List<LocalDateTime> leftInterval = intervalList.get(i);
                List<LocalDateTime> rigthInterval = intervalList.get(i+1);
                if (leftInterval.get(1).equals(rigthInterval.get(0))) {
                    rigth = rigthInterval.get(1);
                } else {
                    solidFreeIntervalList.add(Arrays.asList(left,rigth));
                    left = intervalList.get(i+1).get(0);
                    rigth = intervalList.get(i+1).get(1);
                }
//  if this LAST iteration
            } else {
                solidFreeIntervalList.add(Arrays.asList(left,intervalList.get(i).get(1)));
            }
        }
        return solidFreeIntervalList;
    }
    @Override
    public List<List<LocalDateTime>>searchFreeTimeForEvent(LocalDateTime startDate, LocalDateTime endDate, int duration)
            throws RemoteException, IllegalArgumentException, OrderOfArgumentsException {
        if (startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        Set<Event> eventListIntoPeriod = searchIntoPeriod(startDate.toLocalDate(), endDate.toLocalDate());

        List<List<LocalDateTime>> freeIntervalList = new ArrayList<List<LocalDateTime>>();
        LocalDateTime tempStartDate = LocalDateTime.from(startDate);
        LOG.info("Searching free time into period from " +
                DateParser.dateToString(startDate) + " to " + DateParser.dateToString(endDate));
        while (tempStartDate.isBefore(endDate)) {
            LocalDateTime tempEndDate = tempStartDate.plusMinutes(duration);
            boolean isFree = true;
            for (Event event : eventListIntoPeriod) {
                if (event.getStartDate().isAfter(tempStartDate) && event.getStartDate().isBefore(tempEndDate) ||
                        event.getEndDate().isAfter(tempStartDate) && event.getEndDate().isBefore(tempEndDate) ||
                        event.getStartDate().isBefore(tempStartDate) && event.getEndDate().isAfter(tempEndDate) ||
                        event.getStartDate().equals(tempStartDate) || event.getEndDate().equals(tempEndDate)) {
                    isFree = false;
                }
            }
            if (isFree) {
                freeIntervalList.add(Arrays.asList(tempStartDate,tempEndDate));
            }
            tempStartDate = tempStartDate.plusMinutes(duration);
        }
        LOG.info("Found "  + mergeSolidInterval(freeIntervalList).size() + " free intervals");
        return mergeSolidInterval(freeIntervalList);
    }

}


