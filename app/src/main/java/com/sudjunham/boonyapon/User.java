package com.sudjunham.boonyapon;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public String username,email;

    public User(){

    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }
    @Exclude
    public Map<String , Object> toMap(){
        HashMap<String , Object> result = new HashMap<>();
        result.put("username",username);
        result.put("email",email);
        return result;
    }
}
