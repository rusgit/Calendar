package com.diosoft.calendar.server.service;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.PeriodOfEvent;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CalendarServiceImpl implements CalendarService {

    private final static Logger logger = Logger.getLogger(CalendarServiceImpl.class);
    private final DataStore dataStore;
    final static int MINUTE_INTERVAL = 15;
    private final static int DESCRIPTION_LENGTH_ONE_DAY = 3;
    private final static int DESCRIPTION_LENGTH_SOME_DAYS = 4;

    public CalendarServiceImpl(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void add(Event event) throws RemoteException {
        if (event == null) throw new IllegalArgumentException();

        logger.info("Validation event with title '" + event.getTitle() + "'");
        try {
            EventValidator.validate(event);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        logger.info("Event successfully validated");
        logger.info("Adding event with title '" + event.getTitle() + "'");
        try {
            dataStore.publish(event);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
        logger.info("Event successfully added");
    }

    @Override
    public Event createEvent(String[] descriptions, Set<Person> attenders, Set<PeriodOfEvent> period) throws RemoteException {
        if (descriptions == null || attenders == null || period == null || descriptions.length != DESCRIPTION_LENGTH_SOME_DAYS) throw new IllegalArgumentException();

        LocalDateTime startDate = null;
        try {
            startDate = DateParser.stringToDate(descriptions[2]);
        } catch (DateTimeFormatException e) {
            e.printStackTrace();
        }
        LocalDateTime endDate = null;
        try {
            endDate = DateParser.stringToDate(descriptions[3]);
        } catch (DateTimeFormatException e) {
            e.printStackTrace();
        }

        logger.info("Creating event with title '" + descriptions[0] + "'");
        Event event = new Event.EventBuilder()
                .id(UUID.randomUUID()).title(descriptions[0])
                .description(descriptions[1])
                .startDate(startDate)
                .endDate(endDate)
                .periodSet(period)
                .attendersSet(attenders).build();
        logger.info("Event successfully created");

        add(event);
        return event;
    }

    @Override
    public Event createEventForAllDay(String[] descriptions, Set<Person> attenders, Set<PeriodOfEvent> period) throws RemoteException {
        if (descriptions == null || attenders == null || period == null
                || descriptions.length < DESCRIPTION_LENGTH_ONE_DAY
                || descriptions.length > DESCRIPTION_LENGTH_SOME_DAYS)
            throw new IllegalArgumentException();

        String startDay = descriptions[2] + " 00:00";
        String endDate = null;

        if (descriptions.length == DESCRIPTION_LENGTH_ONE_DAY) {
            LocalDateTime tempStartDate;
            try {
                tempStartDate = DateParser.stringToDate(startDay);
                LocalDateTime tempEndDate = tempStartDate.plusDays(1);
                endDate = DateParser.dateToString(tempEndDate);
            } catch (DateTimeFormatException e) {
                e.printStackTrace();
            }
        }

        if (descriptions.length == DESCRIPTION_LENGTH_SOME_DAYS) {
            String endDay = descriptions[3] + " 00:00";
            LocalDateTime tempEndDate;
            try {
                tempEndDate = DateParser.stringToDate(endDay);
                tempEndDate = tempEndDate.plusDays(1);
                endDate = DateParser.dateToString(tempEndDate);
            } catch (DateTimeFormatException e) {
                e.printStackTrace();
            }
        }

        String[] preparedDescriptions = {descriptions[0], descriptions[1], startDay, endDate};
        return createEvent(preparedDescriptions, attenders, period);
    }

    @Override
    public Event remove(UUID id) throws RemoteException {
        if (id == null) throw new IllegalArgumentException();

        logger.info("Removing event with id: '" + id + "'");
        Event event = null;
        try {
            event = dataStore.remove(id);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
        if (event == null) {
            logger.info("There is no such Event");
        } else {
            logger.info("Event successfully removed");
        }
        return event;
    }

    @Override
    public void edit(Event event) throws RemoteException {
        if (event == null) throw new IllegalArgumentException();

        logger.info("Edit event with title '" + event.getTitle() + "'");
        remove(event.getId());
        add(event);
        logger.info("Event successfully edited");
    }

    @Override
    public List<Event> searchByTitle(String title) throws RemoteException {
        if (title == null) throw new IllegalArgumentException();

        logger.info("Searching by title '" + title + "':");
        List<Event> events = dataStore.getEventByTitle(title);
        if (events.size() < 1) {
            logger.info("Events not found!");
        } else {
            logger.info("Found " + events.size() + " events");
        }
        return events;
    }

    @Override
    public List<Event> searchByDay(LocalDate day) throws RemoteException {
        if (day == null) throw new IllegalArgumentException();

        logger.info("Searching by day '" + day + "':");
        List<Event> events = dataStore.getEventByDay(day);
        if (events.size() < 1) {
            logger.info("Events not found!");
            return events;
        }
        logger.info("Found " + events.size() + " events");

        return events;
    }

    @Override
    public List<Event> searchByAttender(Person attender) throws RemoteException {
        if (attender == null) throw new IllegalArgumentException();

        logger.info("Searching by attender '" + attender.getName() + "':");
        List<Event> events = dataStore.getEventByAttender(attender);
        if (events.size() < 1) {
            logger.info("Events not found!");
            return events;
        }
        logger.info("Found " + events.size() + " events");

        return events;
    }

    @Override
    public List<Event> searchByAttenderIntoPeriod(Person attender, LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, OrderOfArgumentsException {
        if (attender == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        logger.info("Searching events by attender '" + attender.getName() + " " + attender.getLastName() + "' into period from " +
                DateParser.dateToString(startDate) + " to " + DateParser.dateToString(endDate));
        List<Event> eventListByAttender = searchByAttender(attender);
        List<Event> eventListByAttenderIntoPeriod = new ArrayList<>();
        for (Event event : eventListByAttender)
            if (isEventAndPeriodCrossing(event, startDate, endDate)) eventListByAttenderIntoPeriod.add(event);
        if (eventListByAttenderIntoPeriod.isEmpty())
            logger.info("Events not found!");
        else
            logger.info("Found " + eventListByAttenderIntoPeriod.size() + " events");

        return eventListByAttenderIntoPeriod;
    }

    @Override
    public Set<Event> searchIntoPeriod(LocalDate startDay, LocalDate endDay) throws RemoteException, OrderOfArgumentsException {
        if (startDay == null || endDay == null) throw new IllegalArgumentException();
        if (startDay.isAfter(endDay)) throw new OrderOfArgumentsException();

        logger.info("Searching events into period from '" + startDay + "' to" + endDay);
        Set<Event> eventSetIntoPeriod = new HashSet<>();

//  get all events from period without time (use getEventByDay method of DataStore which use index map)
        while (startDay.isBefore(endDay) || startDay.equals(endDay)) {
            List<Event> tempEventList = searchByDay(startDay);
            eventSetIntoPeriod.addAll(tempEventList);
            startDay = startDay.plusDays(1);
        }
        logger.info("Found " + eventSetIntoPeriod.size() + " events");
        return eventSetIntoPeriod;
    }

    @Override
    public List<List<LocalDateTime>> searchFreeTime(LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, OrderOfArgumentsException {
        if (startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        logger.info("Searching free time into period from '" + startDate + "' to '" + endDate + "'");
        Set<Event> eventSet = searchIntoPeriod(startDate.toLocalDate(), endDate.toLocalDate());
        List<List<LocalDateTime>> freeTimeList = searchFreeTimeBetweenEvents(eventSet, startDate, endDate);
        logger.info("Found "  + freeTimeList.size() + " free intervals");
        return freeTimeList;
    }

    @Override
    public List<List<LocalDateTime>> searchFreeTime2(LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, OrderOfArgumentsException {
        if (startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        Set<Event> eventListIntoPeriod = searchIntoPeriod(startDate.toLocalDate(), endDate.toLocalDate());

        List<List<LocalDateTime>> freeIntervalList = new ArrayList<>();
        LocalDateTime tempStartDate = startDate;
        logger.info("Searching free time into period from '" +
                DateParser.dateToString(startDate) + "' to '" + DateParser.dateToString(endDate) + "'");
        while (tempStartDate.isBefore(endDate)) {
            LocalDateTime tempEndDate = tempStartDate.plusMinutes(MINUTE_INTERVAL);
            boolean isFree = true;
            for (Event event : eventListIntoPeriod)
                if (isEventAndPeriodCrossing(event, tempStartDate, tempEndDate)) {
                    isFree = false;
                    break;
                }
            if (isFree) freeIntervalList.add(Arrays.asList(tempStartDate, tempEndDate));
            tempStartDate = tempStartDate.plusMinutes(MINUTE_INTERVAL);
        }
        logger.info("Found "  + mergeSolidInterval(freeIntervalList).size() + " free intervals");
        return mergeSolidInterval(freeIntervalList);
    }

    @Override
    public List<List<LocalDateTime>> searchFreeTimeForEvent(Event event, LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, OrderOfArgumentsException {
        if (event == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        logger.info("Searching free time for event '" +  event.getTitle() + "' into period from '" +
                DateParser.dateToString(startDate) + "' to '" + DateParser.dateToString(endDate) + "'");
        List<List<LocalDateTime>> freeIntervalListForEvent = searchFreeIntervalsForEvent(event, startDate, endDate, searchFreeTime(startDate, endDate));
        logger.info("Found "  + freeIntervalListForEvent.size() + " free intervals for event");
        return freeIntervalListForEvent;
    }

    @Override
    public List<List<LocalDateTime>> searchFreeTimeForEventWithAttenders(Event event, LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, OrderOfArgumentsException {
        if (event == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        logger.info("Searching free time for event '" +  event.getTitle() + "' into period from '" +
                DateParser.dateToString(startDate) + "' to '" + DateParser.dateToString(endDate) +
                "' with attenders: " + event.getAttenders().toString());

        Set<Event> attendersEvents = new HashSet<>();
        for (Person attender : event.getAttenders())
            attendersEvents.addAll(searchByAttenderIntoPeriod(attender, startDate, endDate));
        List<List<LocalDateTime>> freeIntervalListForEvent = searchFreeIntervalsForEvent(event, startDate, endDate, searchFreeTimeBetweenEvents(attendersEvents, startDate, endDate));
        logger.info("Found "  + freeIntervalListForEvent.size() + " free intervals for event with attenders");
        return freeIntervalListForEvent;
    }

    @Override
    public boolean isAttenderFree(Person attender, LocalDateTime startDate, LocalDateTime endDate) throws RemoteException, OrderOfArgumentsException {
        if (attender == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        logger.info("Checking is attender '" + attender.getName() + " " + attender.getLastName() + "' free from '" +
                DateParser.dateToString(startDate) + "' to '" + DateParser.dateToString(endDate) + "'");
        List<Event> eventListByAttender = searchByAttenderIntoPeriod(attender, startDate, endDate);
        if (eventListByAttender.isEmpty()) {
            logger.info("Attender free");
            return true;
        }
        logger.info("Attender not free");

        return false;
    }

    @Override
    public List<Event> searchEventByTitleStartWith(String prefix) throws RemoteException {
        if (prefix == null) throw new IllegalArgumentException();

        logger.info("Searching events by title start with '" + prefix + "'");
        List<Event> presentInEventList = dataStore.searchEventByTitleStartWith(prefix);

        if (presentInEventList.isEmpty())
            logger.info("Events not found!");
        else
            logger.info("Found " + presentInEventList.size() + " events");

        return presentInEventList;
    }

    private List<List<LocalDateTime>> mergeSolidInterval(List<List<LocalDateTime>> intervalList) {

        List<List<LocalDateTime>> solidFreeIntervalList = new ArrayList<>();
        LocalDateTime left  = intervalList.get(0).get(0);
        LocalDateTime right = intervalList.get(0).get(1);

        for (int i = 0; i < intervalList.size()-1; i++) {
            List<LocalDateTime> leftInterval = intervalList.get(i);
            List<LocalDateTime> rightInterval = intervalList.get(i+1);
            if (leftInterval.get(1).equals(rightInterval.get(0))) right = rightInterval.get(1);
            else {
                solidFreeIntervalList.add(Arrays.asList(left,right));
                left = intervalList.get(i+1).get(0);
                right = intervalList.get(i+1).get(1);
            }
        }

        solidFreeIntervalList.add(Arrays.asList(left,intervalList.get(intervalList.size()-1).get(1)));
        return solidFreeIntervalList;
    }

    private List<List<LocalDateTime>> searchFreeTimeBetweenEvents(Set<Event> events, LocalDateTime startDate, LocalDateTime endDate) throws OrderOfArgumentsException {
        if (events == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        List<List<LocalDateTime>> freeTimeList = new LinkedList<>();
        freeTimeList.add(Arrays.asList(startDate, endDate));
        for(Event event : events) {
            ListIterator<List<LocalDateTime>> it = freeTimeList.listIterator();
            //local code review (vtegza): make sure that you need iterator here @ 12.10.14
            // I need iterator for add/remove operations in freeTimeList
            while (it.hasNext()) {
                List<LocalDateTime> freeTimeInterval = it.next();
                //local code review (vtegza): java 8 stream api - try to use it @ 12.10.14
                // I don't understand where is try to use stream api
                if (isEventIncludesFreeInterval(event, freeTimeInterval))
                    it.remove();
                else {
                    //local code review (vtegza): extract to separate method (all block) @ 12.10.14
                    // why extract to separate method?  How am I going to use an iterator in this case?
                    if (isEventAndFreeIntervalCrossingInStartOfEvent(event, freeTimeInterval))
                        freeTimeList.get(freeTimeList.indexOf(freeTimeInterval)).set(0, event.getEndDate());

                    if (isEventAndFreeIntervalCrossingInEndOfEvent(event, freeTimeInterval))
                        freeTimeList.get(freeTimeList.indexOf(freeTimeInterval)).set(1, event.getStartDate());

                    if ( isFreeIntervalIncludesEvent(event, freeTimeInterval)) {
                        it.add(Arrays.asList(event.getEndDate(), freeTimeInterval.get(1)));
                        freeTimeList.get(freeTimeList.indexOf(freeTimeInterval)).set(1, event.getStartDate());
                    }
                }
            }
        }
        return freeTimeList;
    }

    private List<List<LocalDateTime>> searchFreeIntervalsForEvent(Event event, LocalDateTime startDate, LocalDateTime endDate, List<List<LocalDateTime>> freeIntervalList) throws OrderOfArgumentsException {
        if (event == null || startDate == null || endDate == null || freeIntervalList == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        List<List<LocalDateTime>> freeIntervalListForEvent = new ArrayList<>();

        Duration durationEvent = Duration.between(event.getStartDate(), event.getEndDate());
        for (List<LocalDateTime> freeInterval : freeIntervalList) {
            Duration durationFreeInterval = Duration.between(freeInterval.get(0), freeInterval.get(1));
            if (durationEvent.toMinutes() <= durationFreeInterval.toMinutes()) {
                freeIntervalListForEvent.add(freeInterval);
            }
        }
        return freeIntervalListForEvent;
    }

    private boolean isEventAndPeriodCrossing(Event event, LocalDateTime startDate, LocalDateTime endDate) throws OrderOfArgumentsException {
        if (event == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        if (event.getStartDate().equals(startDate)) return true;
        if (event.getEndDate().equals(endDate)) return true;
        if (isDateIntoPeriod(event.getStartDate(), startDate, endDate)) return true;
        if (isDateIntoPeriod(event.getEndDate(), startDate, endDate)) return true;

        return isPeriodIntoEvent(event, startDate, endDate);
    }

    private boolean isPeriodIntoEvent(Event event, LocalDateTime startDate, LocalDateTime endDate) throws OrderOfArgumentsException {
        if (event == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        return event.getStartDate().isBefore(startDate) && event.getEndDate().isAfter(endDate);
    }

    private boolean isDateIntoPeriod(LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate) throws OrderOfArgumentsException {
        if (date == null || startDate == null || endDate == null) throw new IllegalArgumentException();
        if (startDate.isAfter(endDate)) throw new OrderOfArgumentsException();

        return date.isAfter(startDate) && date.isBefore(endDate);
    }

    private boolean isEventIncludesFreeInterval(Event event, List<LocalDateTime> interval) {
        if (event == null || interval == null) throw new IllegalArgumentException();

       return event.getStartDate().isBefore(interval.get(0).plusMinutes(MINUTE_INTERVAL))
                && event.getEndDate().isAfter(interval.get(1).minusMinutes(MINUTE_INTERVAL));
    }

    private boolean isEventAndFreeIntervalCrossingInStartOfEvent(Event event, List<LocalDateTime> interval) {
        if (event == null || interval == null) throw new IllegalArgumentException();

        return event.getStartDate().isBefore(interval.get(0).plusMinutes(MINUTE_INTERVAL))
                && event.getEndDate().isAfter(interval.get(0))
                && isEndOfEventBeforeEndOfIntervalMinusMinuteInterval(event, interval);
    }

    private boolean isEventAndFreeIntervalCrossingInEndOfEvent(Event event, List<LocalDateTime> interval) {
        if (event == null || interval == null) throw new IllegalArgumentException();

        return isStartOfEventAfterStartOfIntervalPlusMinuteInterval(event, interval)
                && event.getStartDate().isBefore(interval.get(1))
                && event.getEndDate().isAfter(interval.get(1).minusMinutes(MINUTE_INTERVAL));
    }

    private boolean isFreeIntervalIncludesEvent(Event event, List<LocalDateTime> interval) {
        if (event == null || interval == null) throw new IllegalArgumentException();

        return isStartOfEventAfterStartOfIntervalPlusMinuteInterval(event, interval)
                && isEndOfEventBeforeEndOfIntervalMinusMinuteInterval(event, interval);
    }

    private boolean isStartOfEventAfterStartOfIntervalPlusMinuteInterval(Event event, List<LocalDateTime> interval) {
        if (event == null || interval == null) throw new IllegalArgumentException();

        return event.getStartDate().isAfter(interval.get(0).plusMinutes(MINUTE_INTERVAL))
                || event.getStartDate().isEqual(interval.get(0).plusMinutes(MINUTE_INTERVAL));
    }

    private boolean isEndOfEventBeforeEndOfIntervalMinusMinuteInterval(Event event, List<LocalDateTime> interval) {
        if (event == null || interval == null) throw new IllegalArgumentException();

        return event.getEndDate().isBefore(interval.get(1).minusMinutes(MINUTE_INTERVAL))
                || event.getEndDate().isEqual(interval.get(1).minusMinutes(MINUTE_INTERVAL));
    }
}