package com.sudjunham.boonyapon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;

import java.util.Arrays;
import java.util.List;


public class MyEventActivity extends AppCompatActivity {

    Button bt_add_event;
    String email;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);
        email = getIntent().getExtras().getString("userEmail");

        // swipe to go back
        Slidr.attach(this);
        //readliked();

        bt_add_event = findViewById(R.id.bt_add_event);

        bt_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyEventActivity.this , CreateEvent.class );
                intent.putExtra("userEmail", email);
                startActivity(intent);
            }
        });
    }

    /*private void readliked(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("activities");
        String getTitleFirebase = database.name;
        if(user != null && getTitleFirebase != null) {
            likedList = Arrays.asList(getTitleFirebase.split(","));
            for (int i = 0; i < event_kku.size(); i++) {
                for (int k = 0; k < likedList.size(); k++) {
                    if (likedList.get(k).equals(event_kku.get(i).)) {
                        fevEvent.add(event_kku.get(i));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }*/
}
