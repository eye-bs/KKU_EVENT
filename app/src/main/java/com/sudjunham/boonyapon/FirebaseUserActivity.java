package com.sudjunham.boonyapon;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

    @IgnoreExtraProperties
    public class FirebaseUserActivity {
        public String name;
        public String displayName;
        public String location;
        public String credit;
        public String faculty;
        public String phone;
        public String website;
        public String dateSt;
        public String dateEd;
        public String timeSt;
        public String timeEd;
        public String detail;
        public String url;
        public String email;
        List<String> create_event_list;
        private static final FirebaseUserActivity instance = new FirebaseUserActivity();

        public  FirebaseUserActivity() {

        }

        public FirebaseUserActivity(String name, String displayName, String dateSt, String dateEd, String timeSt, String timeEd, String location,
                    String credit, String faculty, String phone, String website, String detail, String url, String email) {
            this.name = name;
            this.displayName = displayName;
            this.dateSt = dateSt;
            this.dateEd = dateEd;
            this.timeSt = timeSt;
            this.timeEd = timeEd;
            this.location = location;
            this.credit = credit;
            this.faculty = faculty;
            this.phone = phone;
            this.website = website;
            this.detail = detail;
            this.url = url;
            this.email = email;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result;
            result = new HashMap<>();
            result.put("title", name);
            result.put("sponsor", displayName);
            result.put("dateSt", dateSt);
            result.put("dateEd", dateEd);
            result.put("timeSt", timeSt);
            result.put("timeEd", timeEd);
            result.put("place", location);
            result.put("credit", credit);
            result.put("faculty", faculty);
            result.put("phone", phone);
            result.put("website", website);
            result.put("content", detail);
            result.put("image", url);
            result.put("email", email);
            return result;
        }

        public static FirebaseUserActivity getInstance() {
            return instance;
        }

    }
