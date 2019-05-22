package com.sudjunham.boonyapon;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MyEventActivity extends AppCompatActivity implements RecyclerViewItemClickListener, ValueEventListener {

    Button bt_add_event;
    String email;
    RecyclerView recyclerView;
    RecyclerViewAdapterUser adapter;
    LinearLayoutManager manager;
    UserCreateEvent user;
    List<String> create_event_list;
    List<Event_list> event_kku = new ArrayList<>();
    List<Event_list> createEvent = new ArrayList<>();
    GoogleSignInAccount googleSignInAccount;
    DatabaseReference myRef, userRef, checkRef;
    FirebaseDatabase database;
    String eventTitle, urlVal;
    ArrayList<String> teat1 = new ArrayList<>();
    ListView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);
        recyclerView = findViewById(R.id.list_view_create_event);
        list_view = findViewById(R.id.list_view);

        email = getIntent().getExtras().getString("userEmail");
        //event_kku = Event_kku.getInstance().getEventLists();

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //Event_all.getInstance().setEventLists(event_kku);
        urlVal = "https://us-central1-kku-even.cloudfunctions.net/listKKU";
        new RetrieveFeedTaskEvent().execute();

        manager = new LinearLayoutManager(this) ;
        recyclerView.setLayoutManager(manager);
        setAdapterFunc(createEvent);

        userRef = myRef.child("create-event").child("users").child(googleSignInAccount.getId()).child("title");
        userRef.addListenerForSingleValueEvent(this);

        // swipe to go back
        Slidr.attach(this);


        bt_add_event = findViewById(R.id.bt_add_event);

        bt_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyEventActivity.this , CreateEvent.class );
                intent.putExtra("userEmail", email);
                startActivity(intent);
            }
        });

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyEventActivity.this , InfoEventActivity.class);
                Parcelable parcelable = Parcels.wrap(createEvent.get(position));
                intent.putExtra("objEvent",parcelable);
                startActivity(intent);
            }
        });
    }

    public class RetrieveFeedTaskEvent extends AsyncTask<Void, Void, String> {
        // display progressbar while loading
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @SuppressLint("WrongThread")
        protected String doInBackground(Void... urls) {

            try {
                URL urlAddr;
                urlAddr = new URL(urlVal);
                HttpURLConnection urlConnection = (HttpURLConnection) urlAddr.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected void onPostExecute(String response) {
            super.onPreExecute();

            try {
                if(response == null) {
                    final Dialog dialog = new Dialog(MyEventActivity.this);
                    dialog.setContentView(R.layout.customdialog);
                    dialog.setCancelable(false);

                    Button button1 = dialog.findViewById(R.id.button_dialog);
                    button1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.cancel();
                            finish();
                            System.exit(0);
                        }
                    });
                    dialog.show();
                }else {

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray jsonArray = object.getJSONArray("activities");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject activity_event = jsonArray.getJSONObject(i);
                            Event_list event_list = new Event_list();

                            String pDateST = activity_event.getString("dateSt");
                            String pDateED = activity_event.getString("dateEd");
                            String pTimeST = activity_event.getString("timeSt");
                            String pTimeED = activity_event.getString("timeEd");

                            String timeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                            LocalDate currentDate = LocalDate.parse(timeStamp, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            LocalDate getDateEvent = LocalDate.parse(pDateST, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            event_list.setName(activity_event.getString("title").replaceAll("&quot;", "\""));
                            event_list.setDate((pDateST.equals(pDateED))
                                    ? (dateThai(pDateST, null, pTimeST, pTimeED))
                                    : (dateThai(pDateST, pDateED, pTimeST, pTimeED)));
                            event_list.setLocation(activity_event.getString("place"));
                            event_list.setContent(activity_event.getString("content").replaceAll("&quot;", "\""));
                            event_list.setImglink(activity_event.getString("image"));
                            event_list.setmonthForFilter(getDateEvent.getMonthValue());
                            event_list.setDateTimeST(parseDateTime(pDateST, pTimeST));
                            event_list.setDateTimeED(parseDateTime(pDateED, pTimeED));
                            String phoneDEL = activity_event.getString("phone");
                            phoneDEL = phoneDEL.replaceAll(" ", "");
                            phoneDEL = phoneDEL.replaceAll("-", "");
                            if (phoneDEL.length() > 10) {
                                phoneDEL = phoneDEL.substring(0, 9);
                            }
                            event_list.setPhonecontact(phoneDEL);
                            event_list.setWebsite(activity_event.getString("website"));
                            event_list.setSponsor(activity_event.getString("sponsor"));

                            event_kku.add(event_list);
                            adapter.notifyDataSetChanged();
                        }
                        Event_kku.getInstance().setEventLists(event_kku);
                }
                setAdapterFunc(createEvent);
                readevent();
            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

    }

    public void setAdapterFunc(List<Event_list> list){
        adapter = new RecyclerViewAdapterUser(MyEventActivity.this,list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private void readevent(){
        user = UserCreateEvent.getInstance();
        if(user != null && eventTitle != null) {
            create_event_list = Arrays.asList(eventTitle.split(","));
            for (int i = 0; i < event_kku.size(); i++) {
                for (int k = 0; k < create_event_list.size(); k++) {
                    if (create_event_list.get(k).equals(event_kku.get(i).name)) {
                        createEvent.add(event_kku.get(i));
                        teat1.add(event_kku.get(i).name);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            ArrayAdapter<String> arr = new ArrayAdapter<>(this,R.layout.listview,R.id.textView,teat1);
            list_view.setAdapter(arr);
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(MyEventActivity.this , InfoEventActivity.class);
        Parcelable parcelable = Parcels.wrap(adapter.getItem(position));
        intent.putExtra("objEvent",parcelable);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String title = dataSnapshot.getValue(String.class);
        eventTitle = title;
        if (title != null) {
         //   readevent();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    public static String dateThai(String strDate,String endDate,String strtime , String timeED)throws ParseException
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat DateFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
        SimpleDateFormat DateFormatWYear = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

        Date dateST = df.parse(strDate);

        DateFormat parser = new SimpleDateFormat("a. HH.mm",Locale.ENGLISH);
        SimpleDateFormat formatter = new SimpleDateFormat("HH.mm",Locale.ENGLISH);
        Date dtimeST = parser.parse(strtime);
        Date dtimeEd = parser.parse(timeED);
        strtime = formatter.format(dtimeST);
        timeED = formatter.format(dtimeEd);

        //same date
        if(endDate == null) {
            strDate = DateFormatWYear.format(dateST);
            return String.format("%s เวลา %s - %s น.", strDate,strtime,timeED);
        }
        //muti date
        else{
            Date dateED = df.parse(endDate);
            strDate = DateFormat.format(dateST);
            endDate = DateFormat.format(dateED);

            return String.format("%s %s น. - %s %s น.", strDate, strtime,endDate,timeED);
        }
    }

    public String parseDateTime(String date , String time)throws ParseException{
        DateFormat parserTime = new SimpleDateFormat("a. HH.mm", Locale.ENGLISH);
        Date timeP = parserTime.parse(time);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = formatter.format(timeP);
        return date + "T" + formattedTime;

    }
}
