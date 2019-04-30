package com.sudjunham.boonyapon;

import java.util.ArrayList;
import java.util.List;

public class Event_all {
    List<Event_list> eventLists = new ArrayList<>();
    private static final Event_all instance = new Event_all();

    public Event_all() {
        this.eventLists = eventLists;
    }

    public static Event_all getInstance() {
        return instance;
    }

    public List<Event_list> getEventLists() {
        return eventLists;
    }

    public void setEventLists(List<Event_list> eventLists) {
        this.eventLists = eventLists;
    }
}
