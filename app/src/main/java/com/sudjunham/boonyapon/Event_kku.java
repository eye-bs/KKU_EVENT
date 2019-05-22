package com.sudjunham.boonyapon;

import java.util.ArrayList;
import java.util.List;

public class Event_kku {
    List<Event_list> eventLists = new ArrayList<>();
    private static final Event_kku instance = new Event_kku();

    public Event_kku() {
        this.eventLists = eventLists;
    }

    public static Event_kku getInstance() {
        return instance;
    }

    public List<Event_list> getEventLists() {
        return eventLists;
    }

    public void setEventLists(List<Event_list> eventLists) {
        this.eventLists = eventLists;
    }
}
