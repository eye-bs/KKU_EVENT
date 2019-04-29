package com.sudjunham.boonyapon;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;


import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SeachActivity extends AppCompatActivity implements RecyclerViewItemClickListener, View.OnClickListener {

    private long mLastClickTime = 0;
    private SearchViewAdapter adapter;
    private List<String> exampleList;
    List<Event_list> event_List_Search = new ArrayList<Event_list>();
    EditText editText;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    ImageView img_search,img_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach);

        editText = findViewById(R.id.editText);
        img_cancel = findViewById(R.id.img_cancel);
        img_search = findViewById(R.id.img_search);

        img_cancel.setOnClickListener(this);
        img_search.setOnClickListener(this);

        editText.setEnabled(true);
        editText.requestFocus();

        final Intent intent = getIntent();
        event_List_Search = Parcels.unwrap(intent.getParcelableExtra("listEvent"));
        fillExampleList();
        setUpRecyclerView();
        recyclerView.setVisibility(View.INVISIBLE);
        img_cancel.setVisibility(View.INVISIBLE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String getEdittext = editText.getText().toString();
                if(!getEdittext.equals("")){
                    recyclerView.setVisibility(View.VISIBLE);
                    img_cancel.setVisibility(View.VISIBLE);

                    adapter.getFilter().filter(getEdittext);}
                else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    img_cancel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode==KeyEvent.KEYCODE_ENTER)
        {
            // Just ignore the [Enter] key
            return true;
        }
        // Handle all other keys in the default way
        return super.onKeyDown(keyCode, event);
    }


    private void fillExampleList() {
        exampleList = new ArrayList<>();
        for(int i = 0 ; i < event_List_Search.size(); i++){
            exampleList.add(event_List_Search.get(i).name);
        }

    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new SearchViewAdapter(exampleList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("Check","in position " + adapter.getItem(position));
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        for(int i = 0 ; i < event_List_Search.size(); i++){
            if(adapter.getItem(position).equals(event_List_Search.get(i).name)){
                Intent intent = new Intent(SeachActivity.this , InfoEventActivity.class);
                Parcelable parcelable = Parcels.wrap(event_List_Search.get(i));
                intent.putExtra("objEvent",parcelable);
                startActivity(intent);
            }
        }



    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_search:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                break;
            case R.id.img_cancel:
                editText.setText("");
                break;
        }
    }
}
