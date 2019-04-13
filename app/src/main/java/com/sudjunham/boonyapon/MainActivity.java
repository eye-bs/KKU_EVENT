package com.sudjunham.boonyapon;

import android.annotation.SuppressLint;
import android.app.Activity;
import java.time.LocalDate;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;


public class MainActivity extends AppCompatActivity implements RecyclerViewItemClickListener, View.OnClickListener {

    List<Event_list> event_List_Arr = new ArrayList<Event_list>();
    String[] titleSeach;
    ScrollView scrollView;
     RecyclerView recyclerView;
     RecyclerViewAdapter adapter;
     LinearLayoutManager manager;
    ImageView img_filter,img_user;
    Boolean checkedTAG = false,checkedLocation = false;
    LinearLayout seach_bar;
    TextView tv_result_filter;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "AndroidClarified";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView_main);
        img_filter = findViewById(R.id.img_filter);
        seach_bar = findViewById(R.id.seach_bar);
        img_user = findViewById(R.id.img_user);
        tv_result_filter = findViewById(R.id.tv_result_filter);
        tv_result_filter.setVisibility(View.INVISIBLE);
        seach_bar.setOnClickListener(this);
        img_user.setOnClickListener(this);
        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignature = new Intent(getApplicationContext(),FilterActivity.class);
                startActivityForResult(intentSignature,1000);
            }
        });

        scrollView.smoothScrollTo(0,0);
        recyclerView = findViewById(R.id.list_view1);
        recyclerView.setNestedScrollingEnabled(false);
        callEventAPI();

        manager = new LinearLayoutManager(this) ;
        recyclerView.setLayoutManager(manager);
        adapter = new RecyclerViewAdapter(MainActivity.this,event_List_Arr);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        setImgUser();
    }

    private void callEventAPI(){

        String url = "https://www.kku.ac.th/ikku/api/activities/services/topActivity.php";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("activities");
                            titleSeach = new String[jsonArray.length()];
                            for(int i = 0; i < jsonArray.length();i++){
                                JSONObject activity_event = jsonArray.getJSONObject(i);
                                Event_list event_list = new Event_list();

                                String pDateST = activity_event.getString("dateSt");
                                String pDateED = activity_event.getString("dateEd");
                                String pTimeST = activity_event.getString("timeSt");
                                String pTimeED = activity_event.getString("timeEd");

                                String phoneDEL = activity_event.getJSONObject("contact").getString("phone");
                                phoneDEL = phoneDEL.replaceAll(" " , "");
                                phoneDEL = phoneDEL.replaceAll("-" , "");
                                if(phoneDEL.length() > 10){
                                    phoneDEL = phoneDEL.substring(0,9);
                                }

                                String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                                LocalDate currentDate = LocalDate.parse( timeStamp , DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                LocalDate getDateEvent = LocalDate.parse( pDateST , DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                               // if(currentDate.isBefore(getDateEvent) || currentDate.equals(getDateEvent)){
                                    event_list.setName(activity_event.getString("title"));
                                    event_list.setDate(dateThai(pDateST) + pTime(pTimeST,pTimeED) );
                                    event_list.setLocation(activity_event.getString("place"));
                                    event_list.setContent(activity_event.getString("content"));
                                    event_list.setImglink(activity_event.getString("image"));
                                    event_list.setSponsor(activity_event.getString("sponsor"));
                                    event_list.setPhonecontact(phoneDEL);
                                    event_list.setWebsite(activity_event.getJSONObject("contact").getString("website"));
                                    event_list.setmonthForFilter(getDateEvent.getMonthValue());
                                    event_list.setDateTimeST(parseDateTime(pDateST,pTimeST));
                                    event_list.setDateTimeED(parseDateTime(pDateED,pTimeED));

                                    event_List_Arr.add(event_list);
                                    titleSeach[i]=activity_event.getString("title");
                                    adapter.notifyDataSetChanged();
                                //}
                            }

                        } catch (JSONException e) {

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setImgUser();

        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK) {
                List<Event_list> event_List_Arr_stack = new ArrayList<>();
                for(int i = 0 ; i < event_List_Arr.size() ; i++){
                    checkedTAG = false;
                    checkedLocation = false;
                    if(FilteHelper.getInstance().getMinMonth() > event_List_Arr.get(i).getmonthForFilter() || FilteHelper.getInstance().getMaxMonth() < event_List_Arr.get(i).getmonthForFilter()){ }
                    else{
                        if(event_List_Arr.get(i).getLocation().contains("จังหวัด") && FilteHelper.getInstance().isCb_outsude()){
                            checkedLocation = true;
                        }
                        else if(!event_List_Arr.get(i).getLocation().contains("จังหวัด") && FilteHelper.getInstance().isCb_ui()){
                            checkedLocation = true;
                        }
                        else if(!FilteHelper.getInstance().isCb_ui() && !FilteHelper.getInstance().isCb_outsude()) {
                            checkedLocation = true;
                        }
                        if(FilteHelper.getInstance().isCb_TAG1()&&event_List_Arr.get(i).getContent().contains("อบรม")){
                            checkedTAG = true;
                        }
                        else if(FilteHelper.getInstance().isCb_TAG1()&&event_List_Arr.get(i).getContent().contains("บรรยาย")){
                            checkedTAG = true;
                        }
                        else if(FilteHelper.getInstance().isCb_TAG2()&&event_List_Arr.get(i).getContent().contains("ค่าย")){

                            checkedTAG = true;
                        }
                        else if(FilteHelper.getInstance().isCb_TAG3()&&event_List_Arr.get(i).getContent().contains("Start up")){
                            checkedTAG = true;
                        }
                        else if(FilteHelper.getInstance().isCb_TAG4()&&event_List_Arr.get(i).getContent().contains("มาราธอน")){
                            checkedTAG = true;
                        }
                        else if(!FilteHelper.getInstance().isCb_TAG4()&&!FilteHelper.getInstance().isCb_TAG3()&&!FilteHelper.getInstance().isCb_TAG2()&&!FilteHelper.getInstance().isCb_TAG1()){
                            checkedTAG = true;
                        }
                        if(checkedTAG && checkedLocation){
                            event_List_Arr_stack.add(event_List_Arr.get(i));
                        }

                    }
                }
                if(event_List_Arr_stack.size()== 0){
                    tv_result_filter.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else{
                    tv_result_filter.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                adapter = new RecyclerViewAdapter(MainActivity.this,event_List_Arr_stack);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(this);
            }
        }
    }

    public static String dateThai(String strDate)
    {
        String Months[] = {
                "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน",
                "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม",
                "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        int year=0,month=0,day=0;
        try {
            Date date = df.parse(strDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DATE);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return String.format("%s %s %s", day,Months[month],year);
    }

    public String parseDateTime(String date , String time)throws ParseException{
        DateFormat parserTime = new SimpleDateFormat("a. HH.mm");
        Date timeP = parserTime.parse(time);
        SimpleDateFormat formatter = new SimpleDateFormat("HH.mm");
        String formattedTime = formatter.format(timeP);
       return date + " " + formattedTime;

    }

    public static String pTime(String strtime , String timeED) throws ParseException {

            DateFormat parser = new SimpleDateFormat("a. HH.mm");
            Date dateST = parser.parse(strtime);
            Date dateEd = parser.parse(timeED);
            SimpleDateFormat formatter = new SimpleDateFormat("HH.mm");
            String formattedDateST = formatter.format(dateST);
            String formattedDateED = formatter.format(dateEd);
            return String.format(" เวลา %s - %s น.", formattedDateST,formattedDateED );

    }

    private void Toast(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();}


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(MainActivity.this , InfoEventActivity.class);
        Parcelable parcelable = Parcels.wrap(adapter.getItem(position));
        intent.putExtra("objEvent",parcelable);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seach_bar:
                Intent intent = new Intent(MainActivity.this, SeachActivity.class);
                Parcelable parcelable = Parcels.wrap(adapter.getEvent_lists());
                intent.putExtra("listEvent", parcelable);
                startActivity(intent);
                break;
            case R.id.img_user:

                GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
                if (googleSignInAccount != null) {
                    Intent userPage = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(userPage);
                } else {
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
                    googleSignInClient = GoogleSignIn.getClient(this, gso);

                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 101);
                }
        }
    }

    private void setImgUser() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            Picasso.with(this)
                    .load(googleSignInAccount.getPhotoUrl().toString())
                    .into(img_user);
        } else {
            Picasso.with(this)
                    .load(R.drawable.userr)
                    .into(img_user);
        }
    }
}
