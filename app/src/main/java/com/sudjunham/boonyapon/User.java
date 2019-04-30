package com.sudjunham.boonyapon;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public String title,email;
    List<String> likedList;
    private static final User instance = new User();

    public User(){

    }

    public User(String title, String email){
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

    public static User getInstance() {
        return instance;
    }

    public List<String> getLikedList() {
        return likedList;
    }

    public void setLikedList(List<String> likedList) {
        this.likedList = likedList;
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
