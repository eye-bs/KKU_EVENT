package com.sudjunham.boonyapon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import static com.sudjunham.boonyapon.Constants.IMAGE_HEAD_INTENT;
import static com.sudjunham.boonyapon.VisionCloud.progressDialog;

public class CreateEvent extends AppCompatActivity {

    ImageView addImage, datepicker, locationpicker, eventdetail;
    EditText dateBox, locateBox, cloud_response_data, event_name;
    Button saveBtn, cancelBtn;
    Uri uri;
    CoordinatorLayout coordinatorLayout;
    VisionCloud visionCloud;
    Spinner spinner_credit, spinner_faculty;
    String getCredit, getEventFaculty;
    int yearFinal, monthFinal, dayFinal;
    String hourFinal, minuteFinal;
    String inputDate;
    DoubleDateAndTimePickerDialog.Builder doubleBuilder;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        addImage = findViewById(R.id.addImage);
        event_name = findViewById(R.id.eventName);
        dateBox = findViewById(R.id.dateTime);
        locateBox = findViewById(R.id.create_location);
        datepicker = findViewById(R.id.img_datetime);
        locationpicker = findViewById(R.id.img_location);
        eventdetail = findViewById(R.id.img_event_detail);
        cloud_response_data = findViewById(R.id.event_detail);
        spinner_credit = findViewById(R.id.activity_credit);
        spinner_faculty = findViewById(R.id.register_faculty);
        saveBtn = findViewById(R.id.btn_save_myevent);
        cancelBtn = findViewById(R.id.btn_cancel_myevent);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        spinner();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datetimepicker();
            }
        });
        eventdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(CreateEvent.this);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                required();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEvent.this,MyEventActivity.class);
                startActivity(intent);
            }
        });

        visionCloud = new VisionCloud(this,progressDialog,coordinatorLayout,cloud_response_data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("TAGorigin", requestCode + "\n" + resultCode + "\n" + data + "\n");
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    Bitmap bitmap = resizeBitmap(
                            MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri));
                    visionCloud.callCloudVision(bitmap);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
            else if (requestCode == IMAGE_HEAD_INTENT && resultCode == RESULT_OK && data != null) {
                uri = data.getData();
                    Bitmap bitmap = resizeBitmap(
                            MediaStore.Images.Media.getBitmap(getContentResolver(), uri));

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x - 40;
                    addImage.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    addImage.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    Picasso.with(this).load(uri).resize(width, 0).placeholder(R.drawable.rounded_button).error(R.drawable.rounded_button).into(addImage);
            }
            else if (requestCode == 200) {
                Log.i("TAGpdf", "Uri: " + data.getData());
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.fetchfail, Toast.LENGTH_LONG)
                    .show();
        }
    }


    public Bitmap resizeBitmap(Bitmap bitmap) {
        int maxDimension = 1024;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }

        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_HEAD_INTENT);
    }

    private void spinner() {
        final String[] creditActivity = getResources().getStringArray(R.array.credit);
        ArrayAdapter adaptercredit = ArrayAdapter.createFromResource(this, R.array.credit, R.layout.create_spinner);
        spinner_credit.setAdapter(adaptercredit);

        spinner_credit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCredit = creditActivity[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final String[] event_faculty = getResources().getStringArray(R.array.faculty);
        ArrayAdapter adapter_event_faculty = ArrayAdapter.createFromResource(this, R.array.faculty, R.layout.create_spinner);
        spinner_faculty.setAdapter(adapter_event_faculty);

        spinner_faculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getEventFaculty = event_faculty[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void datetimepicker() {
                final Date now = new Date();
                final Calendar calendarMin = Calendar.getInstance();
                final Calendar calendarMax = Calendar.getInstance();

                calendarMin.setTime(now); // Set min now
                calendarMax.setTime(new Date(now.getTime() + TimeUnit.DAYS.toMillis(150))); // Set max now + 150 days

                final Date minDate = calendarMin.getTime();
                final Date maxDate = calendarMax.getTime();

                doubleBuilder = new DoubleDateAndTimePickerDialog.Builder(CreateEvent.this)
                        .backgroundColor(Color.WHITE)
                        .mainColor(getResources().getColor(R.color.main_header))
                        .titleTextColor(Color.WHITE)
                        .minutesStep(15)
                        .mustBeOnFuture()
                        .minDateRange(minDate)
                        .maxDateRange(maxDate)
                        .secondDateAfterFirst(true)
                        .defaultDate(now)
                        .tab0Date(now)
                        .tab1Date(new Date(now.getTime() + TimeUnit.HOURS.toMillis(1)))
                        .title("Date/Time")
                        .tab0Text("Depart")
                        .tab1Text("Return")
                        .listener(new DoubleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(List<Date> dates) {
                                String parseDate = null;
                                String[] arrDate = new String[2];
                                String[] arrTime = new String[2];
                                SimpleDateFormat TimeFormat = new SimpleDateFormat("HH.mm", Locale.getDefault());
                                SimpleDateFormat DateFormatCP = new SimpleDateFormat("d MMM", Locale.getDefault());

                                for (int i = 0 ; i < dates.size() ; i++){
                                    arrDate[i] = DateFormatCP.format(dates.get(i));
                                    arrTime[i] = TimeFormat.format(dates.get(i));
                                }

                                if(arrDate[0].equals(arrDate[1])){
                                    parseDate = String.format("%s เวลา %s - %s น.", arrDate[1],arrTime[0],arrTime[1]);
                                }
                                else{
                                    parseDate = String.format("%s %s น. - %s %s น.", arrDate[0],arrTime[0],arrDate[1],arrTime[1]);
                                }
                                dateBox.setText(parseDate);
                            }
                        });
                doubleBuilder.display();
            }

    private  void required() {
        if( TextUtils.isEmpty (event_name.getText())){
            event_name.setError( "กรุณากรอกข้อมูลให้ครบถ้วน" );
        } else if ( TextUtils.isEmpty (dateBox.getText())){
            dateBox.setError( "กรุณากรอกข้อมูลให้ครบถ้วน" );
        } else if ( TextUtils.isEmpty (locateBox.getText())){
            locateBox.setError( "กรุณากรอกข้อมูลให้ครบถ้วน" );
        } else if ( TextUtils.isEmpty (cloud_response_data.getText())){
            cloud_response_data.setError( "กรุณากรอกข้อมูลให้ครบถ้วน" );
        }

        if ( !TextUtils.isEmpty (dateBox.getText())) {
            dateBox.setError(null);
        }
    }

}
