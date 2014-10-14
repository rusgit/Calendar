package com.diosoft.calendar.server.datastore;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.common.PeriodOfEvent;
import com.diosoft.calendar.server.common.Person;
import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.filesystem.FileSystem;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DataStoreImpl implements DataStore {
    private final static String DAY_MONTH_PATTERN = "dd-MM";

    private Map<UUID, Event> eventStore = new HashMap<>();
    private Map<String, List<UUID>> indexTitle = new HashMap<>();
    private Map<LocalDate, List<UUID>> indexDate = new HashMap<>();
    private Map<Person, List<UUID>> indexAttender = new HashMap<>();
    private Map<String, List<UUID>> indexPeriodYear = new HashMap<>();
    private Map<Integer, List<UUID>> indexPeriodMonth = new HashMap<>();
    private Map<DayOfWeek, List<UUID>> indexPeriodDayOfWeek = new HashMap<>();
    private Map<LocalDate, List<UUID>> indexPeriodDay = new HashMap<>();

    private final FileSystem fileSystem;

    public DataStoreImpl(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void initDataStoreFromXMLResources() throws IOException, DateTimeFormatException, ExecutionException, InterruptedException {
        List<Event> eventList = fileSystem.readAllEvents();
        for (Event event : eventList) {
            if (isEventDuplicate(event)) continue;
            eventStore.put(event.getId(), event);
            createIndexTitle(event);
            createIndexAttender(event);
            createIndexPeriod(event);
        }
    }

    @Override
    public void publish(Event event) throws IllegalArgumentException, IOException, JAXBException {
        if (event == null) throw new IllegalArgumentException();
        if (isEventDuplicate(event)) return;
        eventStore.put(event.getId(), event);
        createIndexTitle(event);
        createIndexPeriod(event);
        createIndexAttender(event);
        fileSystem.write(event);
    }

    @Override
    public Event remove(UUID id) throws IllegalArgumentException, JAXBException, IOException {
        if (id == null) throw new IllegalArgumentException();
        Event event = eventStore.remove(id);
        if (event != null) {
            removeIndexTitle(event);
            removeIndexAttender(event);
            removeIndexPeriod(event);
            fileSystem.delete(event.getId());
        }
        return event;
    }

    @Override
    public Event getEventById(UUID id) throws IllegalArgumentException {
        if (id == null) throw new IllegalArgumentException();
        return eventStore.get(id);
    }

    @Override
    public List<Event> getEventByTitle(String title) throws IllegalArgumentException {
        if (title == null) throw new IllegalArgumentException();

        List<UUID> ids = indexTitle.get(title);
        List<Event> events = new ArrayList<>();
        if (ids != null) events.addAll(ids.stream().map(eventStore::get).collect(Collectors.toList()));
        return events;
    }

    @Override
    public List<Event> getEventByDay(LocalDate day) throws IllegalArgumentException {
        if (day == null) throw new IllegalArgumentException();

        List<Event> events = new ArrayList<>();

        List<UUID> ids = indexDate.get(day);
        if (ids != null) events.addAll(ids.stream()
                .map(eventStore::get)
                .collect(Collectors.toList())
        );

        List<Event> eventWithDayOfWeekPeriod = getEventByDayWithDayOfWeekPeriod(day);
        if (eventWithDayOfWeekPeriod != null) events.addAll(eventWithDayOfWeekPeriod);

        List<Event> eventWithDayPeriod = getEventByDayWithDayPeriod(day);
        if (eventWithDayPeriod != null) events.addAll(eventWithDayPeriod);

        List<Event> eventWithMonthPeriod = getEventByDayWithMonthPeriod(day);
        if (eventWithMonthPeriod != null) events.addAll(eventWithMonthPeriod);

        List<Event> eventWithYearPeriod = getEventByDayWithYearPeriod(day);
        if (eventWithYearPeriod != null) events.addAll(eventWithYearPeriod);

        return events;
    }

    @Override
    public List<Event> getEventByAttender(Person attender) throws IllegalArgumentException {
        if (attender == null) throw new IllegalArgumentException();

        List<UUID> ids = indexAttender.get(attender);
        List<Event> events = new ArrayList<>();
        if (ids != null) events.addAll(ids.stream().map(eventStore::get).collect(Collectors.toList()));
        return events;
    }

    @Override
    public List<Event> searchEventByTitleStartWith(String prefix) throws IllegalArgumentException {
        if (prefix == null) throw new IllegalArgumentException();
        List<Event> presentInEventList = new ArrayList<>();

        indexTitle.keySet().stream().filter(title -> title.startsWith(prefix))
                .forEach(title -> presentInEventList.addAll(indexTitle.get(title).stream().map(eventStore::get).collect(Collectors.toList())));
        return presentInEventList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataStoreImpl{");
        sb.append("eventStore=").append(eventStore);
        sb.append(", indexTitle=").append(indexTitle);
        sb.append(", indexDate=").append(indexDate);
        sb.append(", indexPeriodYear=").append(indexPeriodYear);
        sb.append(", indexPeriodMonth=").append(indexPeriodMonth);
        sb.append(", indexPeriodDayOfWeek=").append(indexPeriodDayOfWeek);
        sb.append(", indexPeriodDay=").append(indexPeriodDay);
        sb.append(", indexAttender=").append(indexAttender);
        sb.append('}');
        return sb.toString();
    }

    private List<Event> getEventByDayWithYearPeriod(LocalDate date) {
        List<Event> eventList = new ArrayList<>();
        String dayAndMonth = date.format(DateTimeFormatter.ofPattern(DAY_MONTH_PATTERN));
        List<UUID> uuids = indexPeriodYear.get(dayAndMonth);
        if (uuids != null) eventList.addAll(uuids.stream()
                .filter(uuid -> eventStore.get(uuid).getStartDate().toLocalDate().isBefore(date)
                        || eventStore.get(uuid).getStartDate().toLocalDate().isEqual(date))
                .map(eventStore::get).collect(Collectors.toList()));

        return eventList;
    }

    private List<Event> getEventByDayWithMonthPeriod(LocalDate date) {
        List<Event> eventList = new ArrayList<>();
        List<UUID> uuids = indexPeriodMonth.get(date.getDayOfMonth());
        if (uuids != null) eventList.addAll(uuids.stream()
                .filter(uuid -> eventStore.get(uuid).getStartDate().toLocalDate().isBefore(date)
                        || eventStore.get(uuid).getStartDate().toLocalDate().isEqual(date))
                .map(eventStore::get).collect(Collectors.toList()));

        return eventList;
    }

    private List<Event> getEventByDayWithDayOfWeekPeriod(LocalDate date) {
        List<Event> eventList = new ArrayList<>();
        List<UUID> uuids = indexPeriodDayOfWeek.get(date.getDayOfWeek());
        if (uuids != null) eventList.addAll(uuids.stream()
                .filter(uuid -> eventStore.get(uuid).getStartDate().toLocalDate().isBefore(date)
                        || eventStore.get(uuid).getStartDate().toLocalDate().isEqual(date))
                .map(eventStore::get).collect(Collectors.toList()));

        return eventList;
    }

    private List<Event> getEventByDayWithDayPeriod(LocalDate date) {
        List<Event> eventList = new ArrayList<>();
        for (LocalDate eventStartDay : indexPeriodDay.keySet())
            if (date.isEqual(eventStartDay) || date.isAfter(eventStartDay)) {
                List<UUID> uuids = indexPeriodDay.get(eventStartDay);
                if (uuids != null) eventList.addAll(uuids.stream()
                        .map(eventStore::get).collect(Collectors.toList()));
            }

        return eventList;
    }

    private void createIndexTitle(Event event) {
        List<UUID> idsTitle = indexTitle.get(event.getTitle());
        if (!indexTitle.containsKey(event.getTitle())) {
            idsTitle = new ArrayList<>();
            idsTitle.add(event.getId());
            indexTitle.put(event.getTitle(), idsTitle);
        } else idsTitle.add(event.getId());
    }

    private void createIndexPeriodOnce(Event event) {
        LocalDate startDay = event.getStartDate().toLocalDate();
        LocalDate endDay = event.getEndDate().toLocalDate();

        while (startDay.isBefore(endDay) || startDay.equals(endDay)) {
            List<UUID> idsDate = indexDate.get(startDay);
            if (!indexDate.containsKey(startDay)) {
                idsDate = new ArrayList<>();
                idsDate.add(event.getId());
                indexDate.put(startDay, idsDate);
            } else idsDate.add(event.getId());
            startDay = startDay.plusDays(1);
        }
    }

    private void createIndexPeriod(Event event) {
        Set<PeriodOfEvent> periods = event.getPeriod();
        for (PeriodOfEvent period : periods)
            switch (period) {
                case ONCE:
                    createIndexPeriodOnce(event);
                    break;
                case EVERY_YEAR:
                    createIndexPeriodYear(event);
                    break;
                case EVERY_MONTH:
                    createIndexPeriodMonth(event);
                    break;
                case EVERY_DAY:
                    createIndexPeriodDay(event);
                    break;
                case MONDAY:
                case TUESDAY:
                case WEDNESDAY:
                case THURSDAY:
                case FRIDAY:
                case SATURDAY:
                case SUNDAY:
                    createIndexPeriodDayOfWeek(event, period);
                    break;
            }
    }

    private void createIndexPeriodDayOfWeek(Event event, PeriodOfEvent period) {
        List<UUID> idsDate = indexPeriodDayOfWeek.get(DayOfWeek.valueOf(period.name()));
        if (!indexPeriodDayOfWeek.containsKey(DayOfWeek.valueOf(period.name()))) {
            idsDate = new ArrayList<>();
            idsDate.add(event.getId());
            indexPeriodDayOfWeek.put(DayOfWeek.valueOf(period.name()), idsDate);
        } else idsDate.add(event.getId());
    }

    private void createIndexPeriodDay(Event event) {
        List<UUID> idsDate = indexPeriodDay.get(event.getStartDate().toLocalDate());
        if (!indexPeriodDay.containsKey(event.getStartDate().toLocalDate())) {
            idsDate = new ArrayList<>();
            idsDate.add(event.getId());
            indexPeriodDay.put(event.getStartDate().toLocalDate(), idsDate);
        } else idsDate.add(event.getId());
    }

    private void createIndexPeriodMonth(Event event) {
        LocalDate startDay = event.getStartDate().toLocalDate();
        LocalDate endDay = event.getEndDate().toLocalDate();

        while (startDay.isBefore(endDay) || startDay.equals(endDay)) {
            List<UUID> idsDate = indexPeriodMonth.get(startDay.getDayOfMonth());
            if (!indexPeriodMonth.containsKey(startDay.getDayOfMonth())) {
                idsDate = new ArrayList<>();
                idsDate.add(event.getId());
                indexPeriodMonth.put(startDay.getDayOfMonth(), idsDate);
            } else idsDate.add(event.getId());
            startDay = startDay.plusDays(1);
        }
    }

    private void createIndexPeriodYear(Event event) {
        LocalDate startDay = event.getStartDate().toLocalDate();
        LocalDate endDay = event.getEndDate().toLocalDate();

        while (startDay.isBefore(endDay) || startDay.equals(endDay)) {
            String dayAndMonth = startDay.format(DateTimeFormatter.ofPattern(DAY_MONTH_PATTERN));
            List<UUID> idsDate = indexPeriodYear.get(dayAndMonth);
            if (!indexPeriodYear.containsKey(dayAndMonth)) {
                idsDate = new ArrayList<>();
                idsDate.add(event.getId());
                indexPeriodYear.put(dayAndMonth, idsDate);
            } else idsDate.add(event.getId());
            startDay = startDay.plusDays(1);
        }
    }

    private void createIndexAttender(Event event) {
        Set<Person> attenders = event.getAttenders();
        for (Person attender : attenders) {
            List<UUID> idsAttender = indexAttender.get(attender);
            if (!indexAttender.containsKey(attender)) {
                idsAttender = new ArrayList<>();
                idsAttender.add(event.getId());
                indexAttender.put(attender, idsAttender);
            } else idsAttender.add(event.getId());
        }
    }

    private void removeIndexTitle(Event event) {
        List<UUID> idsTitle = indexTitle.get(event.getTitle());
        if (idsTitle.size() <= 1) {
            indexTitle.remove(event.getTitle());
        } else idsTitle.remove(event.getId());
    }

    private void removeIndexPeriodOnce(Event event) {
        LocalDate startDay = event.getStartDate().toLocalDate();
        LocalDate endDay = event.getEndDate().toLocalDate();

        while (startDay.isBefore(endDay) || startDay.equals(endDay)) {
            List<UUID> idsDate = indexDate.get(startDay);
            if (idsDate.size() <= 1) {
                indexDate.remove(startDay);
            } else idsDate.remove(event.getId());
            startDay = startDay.plusDays(1);
        }
    }

    private void removeIndexAttender(Event event) {
        Set<Person> attenders = event.getAttenders();
        for (Person attender : attenders) {
            List<UUID> idsAttender = indexAttender.get(attender);
            if (idsAttender.size() <= 1) {
                indexAttender.remove(attender);
            } else idsAttender.remove(event.getId());
        }
    }

    private void removeIndexPeriod(Event event) {
        Set<PeriodOfEvent> periods = event.getPeriod();
        for (PeriodOfEvent period : periods)
            switch (period) {
                case ONCE:
                    removeIndexPeriodOnce(event);
                    break;
                case EVERY_YEAR:
                    removeIndexPeriodYear(event);
                    break;
                case EVERY_MONTH:
                    removeIndexPeriodMonth(event);
                    break;
                case EVERY_DAY:
                    removeIndexPeriodDay(event);
                    break;
                case MONDAY:
                case TUESDAY:
                case WEDNESDAY:
                case THURSDAY:
                case FRIDAY:
                case SATURDAY:
                case SUNDAY:
                    removeIndexPeriodDayOfWeek(event, period);
                    break;
            }
    }

    private void removeIndexPeriodDayOfWeek(Event event, PeriodOfEvent period) {
        List<UUID> idsDate = indexPeriodDayOfWeek.get(DayOfWeek.valueOf(period.name()));
        if (idsDate.size() <= 1) {
            indexPeriodDayOfWeek.remove(DayOfWeek.valueOf(period.name()));
        } else idsDate.remove(event.getId());
    }

    private void removeIndexPeriodDay(Event event) {
        List<UUID> idsDate = indexPeriodDay.get(event.getStartDate().toLocalDate());
        if (idsDate.size() <= 1) {
            indexPeriodDay.remove(event.getStartDate().toLocalDate());
        } else idsDate.remove(event.getId());
    }

    private void removeIndexPeriodMonth(Event event) {
        LocalDate startDay = event.getStartDate().toLocalDate();
        LocalDate endDay = event.getEndDate().toLocalDate();

        while (startDay.isBefore(endDay) || startDay.equals(endDay)) {
            List<UUID> idsDate = indexPeriodMonth.get(startDay.getDayOfMonth());
            if (idsDate.size() <= 1) {
                indexPeriodMonth.remove(startDay.getDayOfMonth());
            } else idsDate.remove(event.getId());
            startDay = startDay.plusDays(1);
        }
    }

    private void removeIndexPeriodYear(Event event) {
        LocalDate startDay = event.getStartDate().toLocalDate();
        LocalDate endDay = event.getEndDate().toLocalDate();

        while (startDay.isBefore(endDay) || startDay.equals(endDay)) {
            String dayAndMonth = startDay.format(DateTimeFormatter.ofPattern(DAY_MONTH_PATTERN));
            List<UUID> idsDate = indexPeriodYear.get(dayAndMonth);
            if (idsDate.size() <= 1) {
                indexPeriodYear.remove(dayAndMonth);
            } else idsDate.remove(event.getId());
            startDay = startDay.plusDays(1);
        }
    }

    private boolean isEventDuplicate(Event event) {
        for (Event e : searchEventByTitleStartWith(event.getTitle()))
            if (e.equals(event))
                return true;
        return false;
    }
}
