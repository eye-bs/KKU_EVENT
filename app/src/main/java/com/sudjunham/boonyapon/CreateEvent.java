package com.sudjunham.boonyapon;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import static android.view.View.VISIBLE;
import static com.sudjunham.boonyapon.Constants.IMAGE_HEAD_INTENT;
import static com.sudjunham.boonyapon.VisionCloud.progressDialog;

public class CreateEvent extends AppCompatActivity {

    ImageView addImage, datepicker, locationpicker, eventdetail;
    EditText dateBox, locateBox, cloud_response_data, event_name, phoneNum, website;
    Button saveBtn, cancelBtn;
    Uri uri;
    CoordinatorLayout coordinatorLayout;
    VisionCloud visionCloud;
    ProgressBar progressBar;
    ScrollView scrollView;
    Spinner spinner_credit, spinner_faculty;
    String getCredit, getEventFaculty;
    String picturePath, email, eventImagePath, eventName, eventPhone, eventDate, eventLocation, eventCredit
            , eventFaculty, eventWebsite, eventDetail, userID, urlString;
    DoubleDateAndTimePickerDialog.Builder doubleBuilder;

    final Calendar myCalendar = Calendar.getInstance();
    private StorageReference mstorageRef;
    DatabaseReference myRef;
    FirebaseDatabase database;
    FirebaseUser user;
    Uri downloadUrl;

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
        phoneNum = findViewById(R.id.phoneNumber);
        website = findViewById(R.id.website);
        progressBar = findViewById(R.id.progress_bar_create_event);
        scrollView = findViewById(R.id.main_app);

        spinner();

        progressBar.setVisibility(View.INVISIBLE);
        email = getIntent().getExtras().getString("activities");
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
                Intent intent = new Intent(CreateEvent.this, UserActivity.class);
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

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    verifyStoragePermissions(CreateEvent.this);

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
                        .title(getString(R.string.datetime))
                        .tab0Text(getString(R.string.startDate))
                        .tab1Text(getString(R.string.EndDate))
                        .listener(new DoubleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(List<Date> dates) {
                                String parseDate = null;
                                String[] arrDate = new String[2];
                                String[] arrTime = new String[2];
                                SimpleDateFormat TimeFormat = new SimpleDateFormat("HH.mm", Locale.getDefault());
                                SimpleDateFormat DateFormatCP = new SimpleDateFormat("d MMM", new Locale("th", "TH"));

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
            event_name.setError(getString(R.string.checkFill));
        } else if ( TextUtils.isEmpty (dateBox.getText())){
            dateBox.setError(getString(R.string.checkFill));
        } else if ( TextUtils.isEmpty (locateBox.getText())){
            locateBox.setError(getString(R.string.checkFill));
        } else if ( TextUtils.isEmpty (cloud_response_data.getText())){
            cloud_response_data.setError(getString(R.string.checkFill));
        } else if ( TextUtils.isEmpty(picturePath)) {
            Toast.makeText(CreateEvent.this, getString(R.string.checkFillImage), Toast.LENGTH_LONG).show();
        }

        if ( !TextUtils.isEmpty (dateBox.getText())) {
            dateBox.setError(null);
        }

        if ( !TextUtils.isEmpty (dateBox.getText()) && !TextUtils.isEmpty (event_name.getText()) &&
                !TextUtils.isEmpty (locateBox.getText()) && !TextUtils.isEmpty (cloud_response_data.getText())
                && !TextUtils.isEmpty( picturePath)) {
            uploadDatatoFirebase();
        }
    }

    protected void uploadDatatoFirebase() {
        eventImagePath = picturePath;
        eventName = event_name.getText().toString();
        eventDate = dateBox.getText().toString();
        eventLocation = locateBox.getText().toString();
        eventCredit = spinner_credit.getSelectedItem().toString();
        eventFaculty = spinner_faculty.getSelectedItem().toString();
        eventPhone = phoneNum.getText().toString();
        eventWebsite = website.getText().toString();
        eventDetail = cloud_response_data.getText().toString();

        userID = email.replace('.','+');

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("activities");
        mstorageRef = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        uploadFile(eventImagePath);
    }

    private void uploadFile(String path) {
        File file = new File(path);
        Uri fileUri = Uri.fromFile(file);

        final String toFIlePath = fileUri.getLastPathSegment();

        StorageReference riversRef = mstorageRef.child("KKUEvent/" + email + "/" + toFIlePath);

        UploadTask uploadTask = riversRef.putFile(fileUri);
        //Log.d("Arisara", "file is " + file.toString());

        progressBar.setVisibility(VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        progressBar.setVisibility(View.GONE);
                        downloadUrl = taskSnapshot.getUploadSessionUri();
                        urlString = downloadUrl.toString();
                        writeNewUser(eventName, eventDate, eventLocation, eventCredit, eventFaculty, eventPhone,
                                eventWebsite, eventDetail, urlString, email);
                        Log.i("Download url is ", urlString);
                        Toast.makeText(CreateEvent.this, getString(R.string.createActicity),
                                Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent(CreateEvent.this, UserActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(CreateEvent.this,
                                "Fail", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void writeNewUser(String name, String date, String location, String credit, String faculty,
                              String phone, String website, String detail, String url, String email) {
        //String key = myRef.child("create-event").push().getKey();
        User user = new User(name, date, location, credit, faculty, phone,
                website, detail, url, email);
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("" , userValues );
        myRef.updateChildren(childUpdates);
    }

    @IgnoreExtraProperties
    public static class User {
        public String name;
        public String date;
        public String location;
        public String credit;
        public String faculty;
        public String phone;
        public String website;
        public String detail;
        public String url;
        public String email;

        public  User() {

        }

        public User(String name, String date, String location, String credit, String faculty, String phone, String website,
                    String detail, String url, String email) {
            this.name = name;
            this.date = date;
            this.location = location;
            this.credit = credit;
            this.faculty = faculty;
            this.phone = phone;
            this.website = website;
            this.detail = detail;
            this.url = url;
            this.email = email;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("title", name);
            result.put("datetime", date);
            result.put("place", location);
            result.put("credit", credit);
            result.put("faculty", faculty);
            result.put("phone", phone);
            result.put("url", website);
            result.put("content", detail);
            result.put("image", url);
            result.put("email", email);
            return result;
        }
    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
