package com.sudjunham.boonyapon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = "TAGlocation";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int RESULT_LOCATION_CODE = 5000;
    private LocationRequest mLocationRequest;
    MarkerOptions options;
    LatLng latLng;
    Location location;
    EditText editText;
    ImageView img_search,img_cancel;
    Button bt_addLocation;
    String result_add;
    RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editText = findViewById(R.id.editText_location);
        img_search = findViewById(R.id.img_search_location);
        img_cancel = findViewById(R.id.img_cancel_location);
        bt_addLocation = findViewById(R.id.bt_addlocation);
        relativeLayout = findViewById(R.id.map_activity);

        img_cancel.setVisibility(View.INVISIBLE);

        mGoogleApiClient = new
                GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000) // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        img_cancel.setOnClickListener(this);
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                relativeLayout.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String getEdittext = editText.getText().toString();
                if(!getEdittext.equals("")){
                    img_cancel.setVisibility(View.VISIBLE);
                }
                else {
                    img_cancel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                handleNewLocation(latLng.latitude,latLng.longitude);
            }
        });

        img_search.setOnClickListener(this);
        bt_addLocation.setOnClickListener(this);
        editText.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    searchByName();
                }
                return false;
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }catch (Exception e){

        }
        if (location == null) {
            handleNewLocation(16.4388806,102.8227072);
        } else {
            Log.d(TAG,"found location");
            handleNewLocation(location.getLatitude() , location.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
// Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " +
                    connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //handleNewLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void handleNewLocation(double curLat , double curLng){
        latLng = new LatLng(curLat, curLng);
        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> list = null;
            list = geocoder.getFromLocation(curLat,curLng,1);
            Address address = list.get(0);
            result_add = address.getAddressLine(0);
            options = new MarkerOptions()
                    .position(latLng).title(result_add);
            //1editText.setText("");
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.75f));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchByName(){
        String getText = editText.getText().toString() + "ขอนแก่น";
        Geocoder geocoder2 = new Geocoder(MapsActivity.this, Locale.getDefault());
        try{
            List<Address> list = geocoder2.getFromLocationName(getText,1);
            if(list.isEmpty()){
                Log.d(TAG,"Location not found");
            }
            else {
                Address address = list.get(0);
                handleNewLocation(address.getLatitude() , address.getLongitude());
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cancel_location:
                editText.setText("");
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                InputMethodManager inputMethodManager =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        relativeLayout.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);

                break;
            case R.id.img_search_location:
                searchByName();
                break;
            case R.id.bt_addlocation:

                Intent intentFilter = new Intent();
                if(editText.getText().toString().equals("")){
                    intentFilter.putExtra("locationName" , result_add);
                }else{
                    intentFilter.putExtra("locationName" , editText.getText().toString() + " ขอนแก่น");
                }

                setResult(RESULT_LOCATION_CODE,intentFilter);
                finish();


        }
    }
}
