package com.sudjunham.boonyapon;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.parceler.Parcels;

public class UserActivity extends AppCompatActivity implements RecyclerViewItemClickListener, RadioGroup.OnCheckedChangeListener {
    ImageView img_calendar,signOut;
    private GoogleSignInClient googleSignInClient;
    private TextView profileName, profileEmail;
    private ImageView profileImage;
    private TextView myEvent;
    TextView tv_num_join;
    RecyclerView recyclerView;
    RecyclerViewAdapterUser adapter;
    LinearLayoutManager manager;
    List<Event_list> event_kku = new ArrayList<>();
    List<Event_list> upComing = new ArrayList<>();
    RadioGroup rd_user;
   ScrollView scrollView;
    ProgressBar progressBar;


    com.google.api.services.calendar.Calendar mService;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    GoogleAccountCredential credentialCaledndar;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // swipe to go back
        Slidr.attach(this);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        profileName = findViewById(R.id.tv_userName);
        profileEmail = findViewById(R.id.tv_Email);
        profileImage = findViewById(R.id.profile_image);
        signOut = findViewById(R.id.btn_signout);
        myEvent = findViewById(R.id.my_event);
        tv_num_join = findViewById(R.id.tv_num_join);
        rd_user = findViewById(R.id.rg_user);
        scrollView = findViewById(R.id.scrollView_user);
        progressBar = findViewById(R.id.progress_bar_user);

        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        event_kku = Event_all.getInstance().getEventLists();

        rd_user.setOnCheckedChangeListener(this);

        credentialCaledndar = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(googleSignInAccount.getEmail());

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credentialCaledndar)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();

        new ApiAsyncTaskForUser(UserActivity.this).execute();

        recyclerView = findViewById(R.id.list_view_user);
        recyclerView.setNestedScrollingEnabled(false);
        manager = new LinearLayoutManager(this) ;
        recyclerView.setLayoutManager(manager);
        adapter = new RecyclerViewAdapterUser(UserActivity.this,upComing);

        myEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this,MyEventActivity.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        FirebaseAuth.getInstance().signOut();
                    }
                });
            }
        });

        Picasso.with(this)
                .load(googleSignInAccount.getPhotoUrl().toString())
                .into(profileImage);
        profileName.setText(googleSignInAccount.getDisplayName());
        profileEmail.setText(googleSignInAccount.getEmail());

        img_calendar = findViewById(R.id.img_calendar);
        img_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = new GregorianCalendar();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, 0);
                long time = cal.getTime().getTime();
                Uri.Builder builder =
                        CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                builder.appendPath(Long.toString(time));
                Intent intent =
                        new Intent(Intent.ACTION_VIEW, builder.build());
                startActivity(intent);
            }
        });
    }

    public void setAdapterFunc(List<Event_list> list){
        adapter = new RecyclerViewAdapterUser(UserActivity.this,list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(UserActivity.this , InfoEventActivity.class);
        Parcelable parcelable = Parcels.wrap(adapter.getItem(position));
        intent.putExtra("objEvent",parcelable);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_user_fev:
                recyclerView.setVisibility(View.GONE);
                break;
            case R.id.rb_user_upcome:
                setAdapterFunc(upComing);
                break;
        }
    }
}