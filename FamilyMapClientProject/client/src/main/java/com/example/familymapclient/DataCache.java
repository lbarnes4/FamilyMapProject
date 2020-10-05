package com.example.familymapclient;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import model.AuthToken;
import model.Event;
import model.Person;
import model.User;

public class DataCache {
    private static DataCache instance;


    public Person userPerson;
    public String authString;
    public List<Person> persons;
    public List<Event> events = new ArrayList<Event>();
    public List<Event> allEvents;
    public boolean showFathersSide;
    public boolean showMothersSide;
    public boolean showMaleEvents;
    public boolean showFemaleEvents;
    public HashMap<String, Integer> eventTypeColors = new HashMap<String,Integer>();
    public final int BIRTH_COLOR = R.color.seagreen;
    public final int MARRIAGE_COLOR = R.color.gold;
    public final int DEATH_COLOR = R.color.black;

    public static DataCache getInstance() {
        synchronized (DataCache.class) {
            if (instance == null) {
                instance = new DataCache();
            }
        }

        return instance;
    }

    public Person getPerson(String personID) {
        if (personID == null) {
            return null;
        }
        for (Person person : persons) {
            if (person.getPersonID().equals(personID)) {
                return person;
            }
        }
        return null;
    }

    public Event getEvent(String eventID) {
        if (eventID == null) {
            return null;
        }
        for (Event event : events) {
            if (event.getEventID().equals(eventID)) {
                return event;
            }
        }
        return null;
    }

    public Event getFirstEventOfPerson(String personID) {
        if (personID == null) {
            return null;
        }
        Event earliestEvent = null;
        for (Event event : events) {
            if (event.getPersonID().equals(personID) && (earliestEvent == null || event.getYear() < earliestEvent.getYear())) {
                if (event.getEventType().equals("birth")) {
                    return event;
                }
                earliestEvent = event;
            }
        }
        return earliestEvent;
    }

    public Person getChildOf(String personID) {
        for (Person person : persons) {
            if ((person.getFatherID() != null && person.getFatherID().equals(personID)) ||
                (person.getMotherID() != null && person.getMotherID().equals(personID))) {
                return person;
            }
        }
        return null;
    }

    private DataCache() {
        eventTypeColors.put("BIRTH",BIRTH_COLOR);
        eventTypeColors.put("MARRIAGE",MARRIAGE_COLOR);
        eventTypeColors.put("DEATH",DEATH_COLOR);
    }

    public List<Event> getEventsOfPerson(String personID, List<Event> listToSearch) {
        List<Event> events = new ArrayList<Event>();
        for (Event event : listToSearch) {
            if (event.getPersonID().equals(personID)) {
                events.add(event);
            }
        }
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                if (event1.getEventType() != null && event1.getEventType().toUpperCase().equals("BIRTH")) {
                    return -1;
                }
                if (event1.getEventType() != null && event1.getEventType().toUpperCase().equals("DEATH")) {
                    return 1;
                }
                if (event1.getYear() < event2.getYear()) {
                    return -1;
                }
                if (event1.getYear() > event2.getYear()) {
                    return 1;
                }
                if (event1.getEventType() != null && event2.getEventType() != null) {
                    return event1.getEventType().toLowerCase().compareTo(event2.getEventType().toLowerCase());
                }
                return 0;
            }
        });
        return events;
    }

    public List<Event> getEventsOfPerson(String personID) {
        return getEventsOfPerson(personID,events);
    }

    public void filterEvents(boolean showFathersSide, boolean showMothersSide,
                             boolean showMaleEvents, boolean showFemaleEvents) {
        this.showFathersSide = showFathersSide;
        this.showMothersSide = showMothersSide;
        this.showMaleEvents = showMaleEvents;
        this.showFemaleEvents = showFemaleEvents;
        events.clear();

        addEventsOfPerson(userPerson);
        Person spouse = getPerson(userPerson.getSpouseID());
        if ((spouse.getGender().equals("f") && showFemaleEvents) ||
                (spouse.getGender().equals("m") && showMaleEvents)) {
            addEventsOfPerson(spouse);
        }
        if (showFathersSide) {
            addEventsOfSide(userPerson.getFatherID());
        }
        if (showMothersSide) {
            addEventsOfSide(userPerson.getMotherID());
        }
    }

    private void addEventsOfPerson(Person person) {
        if ((Objects.equals(person.getGender(),"m") && showMaleEvents) ||
        (Objects.equals(person.getGender(), "f") && showFemaleEvents)) {
            events.addAll(getEventsOfPerson(person.getPersonID(),allEvents));
        }
    }

    private void addEventsOfSide(String personID) {
        if (personID == null) {
            return;
        }
        Person person = getPerson(personID);
        addEventsOfPerson(person);
        addEventsOfSide(person.getFatherID());
        addEventsOfSide(person.getMotherID());
    }
}
