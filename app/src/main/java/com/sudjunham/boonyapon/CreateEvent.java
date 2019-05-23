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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    int i = 0;
    private final static int REQUEST_LOCATION_CODE = 6000;
    CoordinatorLayout coordinatorLayout;
    VisionCloud visionCloud;
    ProgressBar progressBar;
    ScrollView scrollView;
    Spinner spinner_credit, spinner_faculty;
    String getCredit, getEventFaculty;
    String picturePath, email, eventImagePath, eventName, eventPhone, eventDate, eventLocation, eventCredit
            , eventFaculty, eventWebsite, eventDetail, urlString, dateSt, dateEd, timeSt, timeEd, displayName;
    DoubleDateAndTimePickerDialog.Builder doubleBuilder;
    GoogleSignInAccount googleSignInAccount;
    UserCreateEvent userEvent;
    User user2;
    List<String> createList;

    final Calendar myCalendar = Calendar.getInstance();
    private StorageReference mstorageRef;
    DatabaseReference myRef, userRef;
    FirebaseDatabase database;
    FirebaseUser user;
    Uri downloadUrl;
    long maxid = 0;

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
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("create-event/activities");
        userRef = database.getReference("create-event/users");
        mstorageRef = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        spinner();

        progressBar.setVisibility(View.INVISIBLE);
        email = getIntent().getExtras().getString("userEmail");
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        displayName = googleSignInAccount.getDisplayName();
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        dateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datetimepicker();
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

        dateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datetimepicker();
            }
        });

        locationpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEvent.this , MapsActivity.class);
                startActivityForResult(intent,REQUEST_LOCATION_CODE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                required();
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
                finish();
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    maxid = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        visionCloud = new VisionCloud(this,progressDialog,coordinatorLayout,cloud_response_data);
        readliked();
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
            else if(requestCode == REQUEST_LOCATION_CODE && resultCode == 5000){
               String locationName =  data.getStringExtra("locationName");
               locateBox.setText(locationName);
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
                Date dateformatt;

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
                            public void onDateSelected(List<Date> dates){
                                String parseDate = null;
                                String[] arrDate = new String[2];
                                String[] arrTime = new String[2];
                                String[] arrDateJSON = new String[2];
                                String[] arrTimeJSON = new String[2];
                                SimpleDateFormat TimeFormat = new SimpleDateFormat("HH.mm", Locale.getDefault());
                                SimpleDateFormat DateFormatCP = new SimpleDateFormat("d MMM", Locale.getDefault());
                                SimpleDateFormat DateConvert = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                SimpleDateFormat TimeConvert = new SimpleDateFormat("a. HH.mm", Locale.ENGLISH);

                                for (int i = 0 ; i < dates.size() ; i++){
                                    arrDate[i] = DateFormatCP.format(dates.get(i));
                                    arrTime[i] = TimeFormat.format(dates.get(i));
                                    arrDateJSON[i] = DateConvert.format(dates.get(i));
                                    arrTimeJSON[i] = TimeConvert.format(dates.get(i));
                                }

                                if(arrDate[0].equals(arrDate[1])){
                                    parseDate = String.format("%s เวลา %s - %s น.", arrDate[1],arrTime[0],arrTime[1]);
                                    dateSt = arrDateJSON[1];
                                    dateEd = arrDateJSON[1];
                                }
                                else{
                                    parseDate = String.format("%s %s น. - %s %s น.", arrDate[0],arrTime[0],arrDate[1],arrTime[1]);
                                    dateSt = arrDateJSON[0];
                                    dateEd = arrDateJSON[1];
                                }
                                dateBox.setText(parseDate);
                                timeSt = arrTimeJSON[0];
                                timeEd = arrTimeJSON[1];
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

        uploadFile(eventImagePath);
    }

    private void uploadFile(String path) {
        File file = new File(path);
        Uri fileUri = Uri.fromFile(file);

        final String toFIlePath = fileUri.getLastPathSegment();

        final StorageReference riversRef = mstorageRef.child("KKUEvent/" + email + "/" + toFIlePath);

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
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                writeNewUser(eventName, displayName, dateSt, dateEd, timeSt, timeEd, eventLocation, eventCredit, eventFaculty, eventPhone,
                                        eventWebsite, eventDetail, url, email);
                            }
                        });

                        String createThisEvent = eventName + ",";
                        if(createList != null) {
                            for (int i = 0; i < createList.size(); i++) {
                                createThisEvent += createList.get(i)+ ",";
                            }
                        }
                        writeUserEvent(googleSignInAccount.getId(), createThisEvent, googleSignInAccount.getEmail());
                        Toast.makeText(CreateEvent.this, getString(R.string.createActicity),
                                Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent(CreateEvent.this, MainActivity.class);
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

    private void writeNewUser(String name, String displayName, String dateSt, String dateEd, String timeSt, String timeEd,
                              String location, String credit, String faculty,
                              String phone, String website, String detail, String url, String email) {

        FirebaseUserActivity user = new FirebaseUserActivity(name, displayName, dateSt, dateEd, timeSt, timeEd, location, credit,
                faculty, phone, website, detail, url, email);
        Map<String, Object> userValues = user.toMap();
        myRef.child(String.valueOf(maxid+i)).setValue(userValues);
        i++;
    }

    private void writeUserEvent(String userId , String title, String email) {
        UserCreateEvent user = new UserCreateEvent(title, email);
        Map<String,Object> userValue = user.toMap();
        Map<String,Object> childUpdate = new HashMap<>();
        childUpdate.put(userId,userValue);
        userRef.updateChildren(childUpdate);
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

    private void readliked(){

        Query userID = userRef.child(googleSignInAccount.getId());
        userID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user2 = dataSnapshot.getValue(User.class);
                if(user2 != null){
                    User.getInstance().setEmail(user2.email);
                    User.getInstance().setTitle(user2.title);
                    String getTitleFirebase = user2.title;
                    createList = Arrays.asList(getTitleFirebase.split(","));
                    User.getInstance().setLikedList(createList);
                    Log.d("eyeeye" , createList.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
