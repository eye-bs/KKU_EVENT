package com.sudjunham.boonyapon;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

public class InfoEventActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tv_title , tv_sponsor , tv_time, tv_location,tv_content;
    ImageView img_info , img_phone , img_web , img_backArrow;
    String phoneOnClick ;
    String webOnclick = "";
    ProgressBar progressBar;
    ScrollView scrollView_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_event);

        final Intent intent = getIntent();
        Event_list event_detail = Parcels.unwrap(intent.getParcelableExtra("objEvent"));



        scrollView_info = findViewById(R.id.scrollView_info);
        tv_title = findViewById(R.id.tv_title_info);
        tv_sponsor = findViewById(R.id.tv_sponsor_info);
        tv_time = findViewById(R.id.tv_time_info);
        tv_location = findViewById(R.id.tv_location_info);
        tv_content = findViewById(R.id.tv_content_info);

        img_phone = findViewById(R.id.img_phone_info);
        img_info = findViewById(R.id.img_Info);
        img_web = findViewById(R.id.img_web_info);
        img_backArrow = findViewById(R.id.img_backArrow);
        progressBar = findViewById(R.id.progress_bar_info);

        progressBar.setVisibility(View.VISIBLE);
        scrollView_info.setVisibility(View.INVISIBLE);

        img_backArrow.setOnClickListener(this);

        if(!event_detail.getWebsite().equals("") && event_detail.getWebsite().contains("http")){
            webOnclick = event_detail.getWebsite();
            img_web.setOnClickListener(this);
        }
        else{
            img_web.setVisibility(View.INVISIBLE);
        }

        if(!event_detail.getPhonecontact().equals("")){
            phoneOnClick = event_detail.getPhonecontact();

        }
        else{
            img_phone.setVisibility(View.INVISIBLE);
        }

        tv_title.setText(event_detail.getName());
        String getBy = getString(R.string.by,event_detail.getSponsor());
        tv_sponsor.setText(getBy);
        tv_time.setText(event_detail.getDate());
        tv_location.setText(event_detail.getLocation());
        tv_content.setText(event_detail.getContent());


        img_phone.setOnClickListener(this);

       String imgURL = event_detail.getImglink();
       imgURL = imgURL.replaceAll("http","https");

        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size. x;
        Picasso.with(this).load(imgURL).resize(width,0).placeholder(R.drawable.rounded_button).error(R.drawable.rounded_button).into(img_info, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                scrollView_info.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

            }
        });
    }


    @Override
    public void onClick(View view)
    {
        final String PHONE_ONCLICKED = phoneOnClick;
        final String WEB_ONCLICKED = webOnclick;
        Intent intent1 = new Intent(Intent.ACTION_DIAL);
        Intent openBowser = new Intent(Intent.ACTION_VIEW,Uri.parse(WEB_ONCLICKED));
        Intent backMainActivity = new Intent(InfoEventActivity.this , MainActivity.class);
        intent1.setData(Uri.parse("tel:"+PHONE_ONCLICKED));



        switch (view.getId()) {
            case R.id.img_phone_info :
                startActivity(intent1);
                break;
            case R.id.img_web_info :
                startActivity(openBowser);
                break;
            case R.id.img_backArrow:
                startActivity(backMainActivity);
                break;

        }

    }

    private void Toast(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();}


}
