package com.sudjunham.boonyapon;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.icu.text.IDNA;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.CalendarContract;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class InfoEventActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_title, tv_sponsor, tv_time, tv_location, tv_content;
    ImageView img_info, img_phone, img_web,bt_like,img_pin_info;
    String phoneOnClick;
    boolean create = false , loadIMG = false , loadcalendar = false;
    String webOnclick = "";
    ProgressBar progressBar;
    ScrollView scrollView_info;
    Button bt_add_calendar;
    Event_list event_detail;

    com.google.api.services.calendar.Calendar mService;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    GoogleAccountCredential credentialCaledndar;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_AUTHORIZATION = 1001;
    GoogleSignInAccount googleSignInAccount;
    DatabaseReference myRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_event);

        // swipe to go back
        Slidr.attach(this);

        final Intent intent = getIntent();
        event_detail = Parcels.unwrap(intent.getParcelableExtra("objEvent"));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        bt_like = findViewById(R.id.bt_like);

        scrollView_info = findViewById(R.id.scrollView_info);
        tv_title = findViewById(R.id.tv_title_info);
        tv_sponsor = findViewById(R.id.tv_sponsor_info);
        tv_time = findViewById(R.id.tv_time_info);
        tv_location = findViewById(R.id.tv_location_info);
        tv_content = findViewById(R.id.tv_content_info);
        bt_add_calendar = findViewById(R.id.bt_add_calendar);

        img_phone = findViewById(R.id.img_phone_info);
        img_pin_info = findViewById(R.id.img_pin_info);
        img_info = findViewById(R.id.img_Info);
        img_web = findViewById(R.id.img_web_info);
        progressBar = findViewById(R.id.progress_bar_info);

        progressBar.setVisibility(View.VISIBLE);
        scrollView_info.setVisibility(View.INVISIBLE);

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        tv_location.setOnClickListener(this);
        bt_add_calendar.setOnClickListener(this);
        img_pin_info.setOnClickListener(this);

        if (googleSignInAccount != null) {
        credentialCaledndar = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(googleSignInAccount.getEmail());

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credentialCaledndar)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();

            create = false;
            new ApiAsyncTask(InfoEventActivity.this).execute();

            bt_like.setOnClickListener(this);

        } else {
            bt_like.setVisibility(View.INVISIBLE);
        }

        if (!event_detail.getWebsite().equals("") && event_detail.getWebsite().contains("http")) {
            webOnclick = event_detail.getWebsite();
            img_web.setOnClickListener(this);
        } else {
            img_web.setVisibility(View.INVISIBLE);
        }

        if (!event_detail.getPhonecontact().equals("")) {
            phoneOnClick = event_detail.getPhonecontact();

        } else {
            img_phone.setVisibility(View.INVISIBLE);
        }

        tv_title.setText(event_detail.getName());
        String getBy = getString(R.string.by, event_detail.getSponsor());
        tv_sponsor.setText(getBy);
        tv_time.setText(event_detail.getDate());
        tv_location.setText(event_detail.getLocation());
        tv_content.setText( event_detail.getContent());

        img_phone.setOnClickListener(this);

        String imgURL = event_detail.getImglink();
        imgURL = imgURL.replaceAll("http", "https");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Picasso.with(this).load(imgURL).resize(width, 0).placeholder(R.drawable.rounded_button).error(R.drawable.rounded_button).into(img_info, new Callback() {
            @Override
            public void onSuccess() {
                loadIMG = true;
                if(loadcalendar){
                    progressBar.setVisibility(View.GONE);
                    scrollView_info.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {
                img_info.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                scrollView_info.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUTHORIZATION){
            create = true;
            new ApiAsyncTask(InfoEventActivity.this).execute();
        }

    }

    @Override
    public void onClick(View view) {
        final String PHONE_ONCLICKED = phoneOnClick;
        final String WEB_ONCLICKED = webOnclick;
        Intent intent1 = new Intent(Intent.ACTION_DIAL);
        Intent openBowser = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_ONCLICKED));
        intent1.setData(Uri.parse("tel:" + PHONE_ONCLICKED));

        switch (view.getId()) {
            case R.id.img_phone_info:
                startActivity(intent1);
                break;
            case R.id.img_web_info:
                startActivity(openBowser);
                break;
            case R.id.bt_add_calendar:
                if(googleSignInAccount != null) {
                    create = true;
                    new ApiAsyncTask(InfoEventActivity.this).execute();
                    break;
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setMessage("คุณต้องการเข้าสู่ระบบหรือไม่?");
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent gotoLogin = new Intent(InfoEventActivity.this,LoginActivity.class);
                            startActivity(gotoLogin);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Do something
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                break;

            case R.id.img_pin_info:
                Geocoder geocoder = new Geocoder(InfoEventActivity.this, Locale.getDefault());
                try{
                    List<Address> list = geocoder.getFromLocationName(tv_location.getText().toString(),1);
                    if(list.isEmpty()){
                        View rootView = findViewById(R.id.linearLayout2);
                        Snackbar.make(rootView, "ไม่สามารถระบุตำแหน่งได้", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        Address address = list.get(0);

                        Uri gmmIntentUri = Uri.parse("geo:" + address.getLatitude() + "," + address.getLongitude() + "?q=" + Uri.encode(tv_location.getText().toString()));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tv_location_info:
                Geocoder geocoder2 = new Geocoder(InfoEventActivity.this, Locale.getDefault());
                try{
                    List<Address> list = geocoder2.getFromLocationName(tv_location.getText().toString(),1);
                    if(list.isEmpty()){
                        View rootView = findViewById(R.id.linearLayout2);
                        Snackbar.make(rootView, "ไม่สามารถระบุตำแหน่งได้", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        Address address = list.get(0);

                        Uri gmmIntentUri = Uri.parse("geo:" + address.getLatitude() + "," + address.getLongitude() + "?q=" + Uri.encode(tv_location.getText().toString()));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.bt_like:

        }

    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        InfoEventActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    public void writeNewUser(String userId , String name , String email){
        String key = myRef.child("user").push().getKey();
        User user = new User(name ,email);
        Map<String,Object> userValue = user.toMap();
        Map<String,Object> childUpdate = new HashMap<>();
        childUpdate.put("/users/" + userId,userValue);
        myRef.updateChildren(childUpdate);
    }

}
