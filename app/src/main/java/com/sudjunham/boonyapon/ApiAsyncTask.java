package com.sudjunham.boonyapon;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */

/**
 * Created by miguel on 5/29/15.
 */

public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private InfoEventActivity iActivity;
    boolean checkSummary = false;
    String uri = null;
    List<String> eventStrings;
    Intent intent;

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(InfoEventActivity activity) {
        this.iActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {

            if(iActivity.create) {
                createEvent();
            }
            getDataFromApi();

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            iActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            iActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    InfoEventActivity.REQUEST_AUTHORIZATION);

        } catch (IOException e) {
            System.out.printf("The following error occurred: " +
                    e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        final String link = uri;
        Log.d("TAGcal" , link+"");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = new GregorianCalendar();
        try {

            if(link != null) {
                Date date = sdf.parse(link);
                cal.setTime(date);
                cal.add(Calendar.MONTH, 0);
                long time = cal.getTime().getTime();
                Uri.Builder builder =
                        CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                builder.appendPath(Long.toString(time));
                intent = new Intent(Intent.ACTION_VIEW, builder.build());

                if (iActivity.create) {
                    View rootView = iActivity.findViewById(R.id.linearLayout2);
                    Snackbar.make(rootView, R.string.calender_added, Snackbar.LENGTH_LONG).setAction(R.string.open_cal, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //                    Intent openBowser = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            //                    iActivity.startActivity(openBowser);
                            iActivity.startActivity(intent);
                        }
                    })
                            .setActionTextColor(Color.YELLOW)
                            .show();
                }

                if (checkSummary) {
                    iActivity.bt_add_calendar.setText(R.string.btn_added);
                    iActivity.bt_add_calendar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //                    Intent openBowser = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            //                    iActivity.startActivity(openBowser);
                            iActivity.startActivity(intent);
                        }
                    });
                }
            }

            iActivity.loadcalendar = true;
            if(iActivity.loadIMG) {
                iActivity.progressBar.setVisibility(View.GONE);
                iActivity.scrollView_info.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void createEvent() throws IOException {

        Event event = new Event()
                .setSummary(iActivity.event_detail.name)
                .setLocation(iActivity.event_detail.location)
                .setDescription(iActivity.event_detail.content + "\n#KKUEvent");

        DateTime startDateTime = new DateTime(iActivity.event_detail.dateTimeST);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime);
        start.setTimeZone(start.getTimeZone());
        event.setStart(start);

        DateTime endDateTime = new DateTime(iActivity.event_detail.dateTimeED);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime);
        end.setTimeZone(end.getTimeZone());
        event.setEnd(end);


        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(24*60),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        event = iActivity.mService.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());

    }

    private List<String> getDataFromApi() throws IOException {

        try {
            eventStrings = new ArrayList<String>();
            Events events = iActivity.mService.events().list("primary")
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            for (final Event event : items) {
                String getSummary = event.getSummary();
                EventDateTime eventDateTime = event.getStart();
                String getTAG = event.getDescription();
                if(getTAG!= null && getTAG.contains("#KKUEvent")) {
                    eventStrings.add(String.format("%s (%s)", event.getSummary(), getTAG));
                }
                if(getSummary!= null && getSummary.equals(iActivity.event_detail.name)){
                    uri = eventDateTime.getDateTime().toString();
                    uri = uri.substring(0,10);
                    checkSummary = true;
                    break;
                }
            }

            return eventStrings;
        }catch (Exception e){
            return eventStrings;
        }

    }



}