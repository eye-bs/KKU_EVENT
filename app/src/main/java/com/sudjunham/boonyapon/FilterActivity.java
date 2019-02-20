package com.sudjunham.boonyapon;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import static com.sudjunham.boonyapon.FilteHelper.*;

public class FilterActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    String Months[] = {
            "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน",
            "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม",
            "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
    CheckBox checkBox_uni , checkBox_ununi,cb_c1,cb_c2,cb_c3,cb_c4,cb_c5,cb_T1,cb_T2,cb_T3,cb_T4;
    Button bt_filter_ok;
    Spinner spinner;
    String getFaculty;
    int minV , maxV;
    Intent intentFilter;
    ImageView img_backFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        final TextView tvMin = findViewById(R.id.tv_minSeek);
        final TextView tvMax = findViewById(R.id.tv_maxSeek);

        final CrystalRangeSeekbar rangeSeekbar = findViewById(R.id.rangeSeekbar);

        checkBox_uni = findViewById(R.id.checkBox_uni);
        checkBox_ununi = findViewById(R.id.checkBox_ununi);
        cb_c1 = findViewById(R.id.cb_c1);
        cb_c2 = findViewById(R.id.cb_c2);
        cb_c3 = findViewById(R.id.cb_c3);
        cb_c4 = findViewById(R.id.cb_c4);
        cb_c5 = findViewById(R.id.cb_c5);
        cb_T1 = findViewById(R.id.cb_T1);
        cb_T2 = findViewById(R.id.cb_T2);
        cb_T3 = findViewById(R.id.cb_T3);
        cb_T4 = findViewById(R.id.cb_T4);

        FilteHelper.getInstance().setCb_ui(false);
        FilteHelper.getInstance().setCb_outsude(false);


        bt_filter_ok = findViewById(R.id.bt_filter_ok);
        img_backFilter = findViewById(R.id.img_backFilter);
        spinner = findViewById(R.id.spinner_open);

        final String[] faculty = getResources().getStringArray(R.array.faculty);
        ArrayAdapter adapterfaculty = ArrayAdapter.createFromResource(this,R.array.faculty,R.layout.my_spinner);
        spinner.setAdapter(adapterfaculty);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getFaculty = faculty[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkBox_uni.setOnCheckedChangeListener(this);
        checkBox_ununi.setOnCheckedChangeListener(this);
        cb_c1.setOnCheckedChangeListener(this);
        cb_c2.setOnCheckedChangeListener(this);
        cb_c3.setOnCheckedChangeListener(this);
        cb_c4.setOnCheckedChangeListener(this);
        cb_c5.setOnCheckedChangeListener(this);
        cb_T1.setOnCheckedChangeListener(this);
        cb_T2.setOnCheckedChangeListener(this);
        cb_T3.setOnCheckedChangeListener(this);
        cb_T4.setOnCheckedChangeListener(this);

        img_backFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        bt_filter_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FilteHelper.getInstance().setFaculty(getFaculty);
                FilteHelper.getInstance().setMinMonth(minV);
                FilteHelper.getInstance().setMaxMonth(maxV);

                intentFilter = new Intent();
                setResult(RESULT_OK,intentFilter);
                finish();

            }
        });

        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                minV = Integer.parseInt(String.valueOf(minValue));
                maxV = Integer.parseInt(String.valueOf(maxValue));
                tvMin.setText(Months[minV]);
                tvMax.setText(Months[maxV]);
            }
        });

        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Drawable da_checked = (Drawable)getResources().getDrawable(R.drawable.rounded_checked_box);
        Drawable da_unchecked = (Drawable)getResources().getDrawable(R.drawable.ounded_uncheck_box);

        switch (buttonView.getId()){
            case R.id.checkBox_uni:
                if(isChecked) {
                    checkBox_uni.setBackground(da_checked);
                    checkBox_uni.setTextColor(Color.WHITE);
                   FilteHelper.getInstance().setCb_ui(true);
                }
                else {
                    checkBox_uni.setBackground(da_unchecked);
                    checkBox_uni.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_ui(false);
                }
                break;
            case R.id.checkBox_ununi:
                if(isChecked) {
                    checkBox_ununi.setBackground(da_checked);
                    checkBox_ununi.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_outsude(true);
                }
                else {
                    checkBox_ununi.setBackground(da_unchecked);
                    checkBox_ununi.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_outsude(false);
                }
                break;

            case R.id.cb_c1:
                if(isChecked) {
                    cb_c1.setBackground(da_checked);
                    cb_c1.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_credit1(true);
                }
                else {
                    cb_c1.setBackground(da_unchecked);
                    cb_c1.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_credit1(false);
                }
                break;

            case R.id.cb_c2:
                if(isChecked) {
                    cb_c2.setBackground(da_checked);
                    cb_c2.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_credit2(true);
                }
                else {
                    cb_c2.setBackground(da_unchecked);
                    cb_c2.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_credit2(false);
                }
                break;

            case R.id.cb_c3:
                if(isChecked) {
                    cb_c3.setBackground(da_checked);
                    cb_c3.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_credit3(true);
                }
                else {
                    cb_c3.setBackground(da_unchecked);
                    cb_c3.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_credit3(false);
                }
                break;

            case R.id.cb_c4:
                if(isChecked) {
                    cb_c4.setBackground(da_checked);
                    cb_c4.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_credit4(true);
                }
                else {
                    cb_c4.setBackground(da_unchecked);
                    cb_c4.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_credit4(false);}
                break;

            case R.id.cb_c5:
                if(isChecked) {
                    cb_c5.setBackground(da_checked);
                    cb_c5.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_credit5(true);
                }
                else {
                    cb_c5.setBackground(da_unchecked);
                    cb_c5.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_credit5(false);
                }
                break;

            case R.id.cb_T1:
                if(isChecked) {
                    cb_T1.setBackground(da_checked);
                    cb_T1.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_TAG1(true);
                }
                else {
                    cb_T1.setBackground(da_unchecked);
                    cb_T1.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_TAG1(false);
                }
                break;

            case R.id.cb_T2:
                if(isChecked) {
                    cb_T2.setBackground(da_checked);
                    cb_T2.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_TAG2(true);
                }
                else {
                    cb_T2.setBackground(da_unchecked);
                    cb_T2.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_TAG2(false);
                }
                break;

            case R.id.cb_T3:
                if(isChecked) {
                    cb_T3.setBackground(da_checked);
                    cb_T3.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_TAG3(true);
                }
                else {
                    cb_T3.setBackground(da_unchecked);
                    cb_T3.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_TAG3(false);
                }
                break;

            case R.id.cb_T4:
                if(isChecked) {
                    cb_T4.setBackground(da_checked);
                    cb_T4.setTextColor(Color.WHITE);
                    FilteHelper.getInstance().setCb_TAG4(true);
                }
                else {
                    cb_T4.setBackground(da_unchecked);
                    cb_T4.setTextColor(Color.BLACK);
                    FilteHelper.getInstance().setCb_TAG4(false);
                }
                break;
        }
    }

    private void Toast(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
