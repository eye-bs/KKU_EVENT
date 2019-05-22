package com.sudjunham.boonyapon;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */

/**
 * Created by miguel on 5/29/15.
 */

public class ApiAsyncTaskForUser extends AsyncTask<Void, Void, Void> {
    private UserActivity userActivity;
    String uri = null;
    List<String> eventStrings;
    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTaskForUser(UserActivity activity) {
        this.userActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
          getDataFromApi();

        } catch (IOException e) {
            System.out.printf("The following error occurred: " +
                    e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        userActivity.tv_num_join.setText(Integer.toString(eventStrings.size()));

        for (int i = 0 ; i < userActivity.event_kku.size();i++){
            for(int j = 0 ; j < eventStrings.size() ; j++){
              //  Log.d("TAG123" , "event: " + userActivity.event_kku.get(i).name + "\ncalendar: " + eventStrings.get(j));
                if(eventStrings.get(j).equals(userActivity.event_kku.get(i).name)){
                    userActivity.upComing.add(userActivity.event_kku.get(i));
                    userActivity.adapter.notifyDataSetChanged();
                }
            }

        }

        userActivity.progressBar.setVisibility(View.INVISIBLE);
        userActivity.scrollView.setVisibility(View.VISIBLE);

    }

    private List<String> getDataFromApi() throws IOException {
        try {
            DateTime now = new DateTime(System.currentTimeMillis());
            eventStrings = new ArrayList<String>();
            Events events = userActivity.mService.events().list("primary")
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            int i = 0;
            for (final Event event : items) {
                uri = event.getHtmlLink();
                String getTAG = event.getDescription();
                if(getTAG!= null && getTAG.contains("#KKUEvent")) {
                    eventStrings.add(event.getSummary());
                }
            }
            return eventStrings;
        }catch (Exception e){
            return eventStrings;
        }

    }



}