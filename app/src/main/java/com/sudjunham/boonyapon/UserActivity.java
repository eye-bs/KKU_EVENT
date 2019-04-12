package com.sudjunham.boonyapon;

import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UserActivity extends AppCompatActivity {
    ImageView img_calendar;
    private GoogleSignInClient googleSignInClient;
    private TextView profileName, profileEmail;
    private ImageView profileImage,backArrow;
    private TextView signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        profileName = findViewById(R.id.tv_userName);
        profileEmail = findViewById(R.id.tv_Email);
        profileImage = findViewById(R.id.profile_image);
        signOut = findViewById(R.id.btn_signout);
        backArrow = findViewById(R.id.img_backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainPage = new Intent(UserActivity.this, MainActivity.class);
                startActivity(mainPage);
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
}