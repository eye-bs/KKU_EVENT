package com.sudjunham.boonyapon;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class UserCreateEvent {
    public String title,email;
    List<String> eventList;
    private static final UserCreateEvent instance = new UserCreateEvent();

    public UserCreateEvent(){

    }

    public UserCreateEvent(String title, String email){
        this.title = title;
        this.email = email;
    }
    @Exclude
    public Map<String , Object> toMap(){
        HashMap<String , Object> result = new HashMap<>();
        result.put("title",title);
        result.put("email",email);
        return result;
    }

    public static UserCreateEvent getInstance() {
        return instance;
    }

    public List<String> getEventList() {
        return eventList;
    }

    public void setEventList(List<String> likedList) {
        this.eventList = likedList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
