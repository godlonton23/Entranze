package com.godlontonconsulting.entranze.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.aviadmini.quickimagepick.PickCallback;
import com.aviadmini.quickimagepick.PickSource;
import com.aviadmini.quickimagepick.QiPick;
import com.aviadmini.quickimagepick.UriUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.AddEntranzeResponse;
import com.godlontonconsulting.entranze.pojos.Entranze;
import com.godlontonconsulting.entranze.pojos.GPS;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static java.lang.Integer.parseInt;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Energy on 2017/05/26.
 */

public class AddEventActivity extends AppCompatActivity implements View.OnClickListener,LocationListener,AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddEventActivity" ;
    EditText eventName;
    EditText eventCost,eventDescription;
    Button btnAddEvent;
    private static int PLACE_PICKER_REQUEST = 1;
    Button btnDatePicker, btnTimePicker, btnDatePicker2, btnTimePicker2,btnGmaps;
    TextView eventLocation,eventSeating;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private ImageView imgAdd;
    private Toolbar toolbar;
    private FloatingActionButton fAddImage;
    private boolean isImage;
    //a Uri object to store file path//
    public static boolean eventAdded=false;
    private double latCords;
    private double longCords;
    private String venuePlace,chosenCurrency;
    private boolean privacy=false;
    SweetAlertDialog pDialog;
    PrefManager pref;
    File file,compressedImageFile ;
    Uri imageURI;
    String filePath;
    private static final String QIP_DIR_NAME = "Entranze";

    // Wigets - GUI
    Spinner spCountries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Event");
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pref = new PrefManager(getApplicationContext());
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        eventName = (EditText) findViewById(R.id.event_title);
        eventSeating = (EditText) findViewById(R.id.event_seats);
        eventName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eventName.setText("");
            }
        });
        //
        // Spinner element //
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        chosenCurrency="USD";
        //
        spCountries = (Spinner) findViewById(R.id.spCountries);
        // Country Item Selected Listener
        spCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();

                if (item.equalsIgnoreCase("USD")){
                    chosenCurrency="$";
                }else if (item.equalsIgnoreCase("ZAR")){
                    chosenCurrency="R";
                }else if (item.equalsIgnoreCase("GBP")){
                    chosenCurrency="£";
                }
                // Showing selected spinner item
               // Toast.makeText(getApplicationContext(),"Selected Country : " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        // Spinner click listener //
        spinner.setOnItemSelectedListener(this);
        // Spinner Drop down elements //
        List<String> categories = new ArrayList<String>();
        categories.add("Public event");
        categories.add("Private event");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        //
        int maxLength = 25;
        eventName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        eventCost = (EditText) findViewById(R.id.event_cost);
        eventCost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eventCost.setText("");
            }
        });
        eventDescription = (EditText) findViewById(R.id.event_description);
        eventDescription.setHorizontallyScrolling(false);
        eventDescription.setMaxLines(Integer.MAX_VALUE);
        imgAdd = (ImageView) findViewById(R.id.imgAdd);
        eventDescription.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eventDescription.setText("");
            }
        });

        int maxLengthCost = 4;
        eventCost.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthCost)});
        btnAddEvent = (Button) findViewById(R.id.addevent_button);
        btnAddEvent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateForm();
            }
        });
        //
        btnGmaps = (Button) findViewById(R.id.gmaps);
        btnGmaps.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isImage=false;

                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {

                }

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {

                }

                if(!gps_enabled && !network_enabled) {
                    new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error")
                            .setContentText("Location services are not enabled. Please check and enable your location services.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(myIntent);
                                }
                            })
                            .show();
                } else {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(AddEventActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //
        btnDatePicker=(Button) findViewById(R.id.event_startdate);
        btnTimePicker=(Button) findViewById(R.id.event_start_time);
        String currentDate = DateFormat.getDateInstance().format(new Date());
        String currentTime = DateFormat.getTimeInstance().format(new Date());

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);

        btnDatePicker.setText(todayString);
        btnTimePicker.setText(currentTime);
        btnTimePicker=(Button) findViewById(R.id.event_start_time);
        btnDatePicker2=(Button) findViewById(R.id.end_date);
        btnTimePicker2=(Button) findViewById(R.id.end_time);
        btnDatePicker2.setText(todayString);
        btnTimePicker2.setText(currentTime);
        fAddImage=(FloatingActionButton) findViewById(R.id.fab);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnDatePicker2.setOnClickListener(this);
        btnTimePicker2.setOnClickListener(this);
        eventLocation=(TextView) findViewById(R.id.event_location);
        fAddImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
   //
    private void selectImage() {
        isImage=true;
        QiPick.in(this).fromGallery();

    }
    //
    private final PickCallback mCallback = new PickCallback() {

        @Override
        public void onImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final Uri pImageUri) {
            // Do something with Uri, for example load image into an ImageView
            imageURI=pImageUri;
            Glide.with(getApplicationContext())
                    .load(pImageUri)
                    .apply(new RequestOptions().fitCenter())
                    .into(imgAdd);

            final String extension = UriUtils.getFileExtension(AddEventActivity.this, pImageUri);
            Log.i(TAG, "Picked: " + pImageUri.toString() + "\nMIME type: " + UriUtils.getMimeType(AddEventActivity.this,
                    pImageUri) + "\nFile extension: " + extension + "\nRequest type: " + pRequestType);
            filePath=pImageUri.toString();

            try {

                final String ext = extension == null ? "" : "." + extension;
                final File outDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), QIP_DIR_NAME);
                Random rand = new Random();

                int  n = rand.nextInt(500000) + 1;

                file = new File(outDir, "entranze" + String.valueOf(n)+ ext);
                outDir.mkdirs();
                UriUtils.saveContentToFile(AddEventActivity.this, pImageUri, file);

            } catch (final IOException e) {
                Toast.makeText(AddEventActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        public void onMultipleImagesPicked(int i, @NonNull List<Uri> list) {

        }

        @Override
        public void onError(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final String pErrorString) {
            Log.e(TAG, "Err: " + pErrorString);
        }

        @Override
        public void onCancel(@NonNull final PickSource pPickSource, final int pRequestType) {
            Log.d(TAG, "Cancel: " + pPickSource.name());
        }

    };

    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!isImage) {
            if(resultCode != RESULT_CANCELED){
                if (requestCode == PLACE_PICKER_REQUEST) {
                    if (resultCode == Activity.RESULT_OK) {
                        Place place = PlacePicker.getPlace(data, AddEventActivity.this);
                        double lat = place.getLatLng().latitude;
                        double lng = place.getLatLng().longitude;
                        latCords= place.getLatLng().latitude;
                        longCords= place.getLatLng().longitude;
                        //
                        String[] parts = place.getAddress().toString().split(",");

                        String part1 = parts[0]; // 004
                        String part2 = parts[1]; // 034556
                        //String part3 = parts[2]; // 004

                        btnGmaps.setText(part1+", "+part2);
                        venuePlace=part1+", "+part2;
                        latCords= place.getLatLng().latitude;
                        longCords= place.getLatLng().longitude;
//                        Geocoder geocoder;
//                        List<Address> addresses;
//                        geocoder = new Geocoder(this, Locale.getDefault());
//                        try {
//                            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                            String city = addresses.get(0).getLocality();
//                            String state = addresses.get(0).getAdminArea();
//                            String country = addresses.get(0).getCountryName();
//                            String postalCode = addresses.get(0).getPostalCode();
//                            String knownName = addresses.get(0).getFeatureName();
//                            btnGmaps.setText(address + ", " + city);
//                            venuePlace=address + ", " + city;
//                        } catch (IOException e) {
//
//                        }
                    }
                } else {
                    btnGmaps.setText("Event venue");
                    new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Missing fields")
                            .setContentText( "Please select a venue.")
                            .show();
                }
            } else {
                btnGmaps.setText("Event venue");
                new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Missing fields")
                        .setContentText( "Please select a venue.")
                        .show();
            }
        }else {
            QiPick.handleActivityResult(getApplicationContext(), requestCode, resultCode, data, this.mCallback);
        }
    }
    /**
     * Validating user details form
     */
    private void validateForm() {
        String title="";
        int priceInCents=0;
        int seatingAvailable=0;
        String eventAbout="";
        //
        if (isValidText(eventName.getText().toString())) {
            title=eventName.getText().toString();
        } else {
            new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Missing fields")
                    .setContentText("Please enter valid event title.")
                    .show();
        }
        //
        if (isValidText(eventCost.getText().toString())){
            priceInCents=parseInt(eventCost.getText().toString());
        } else {
            new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Missing fields")
                    .setContentText("Please enter a price.")
                    .show();
        }

        if (isValidText(eventSeating.getText().toString())){
            seatingAvailable=parseInt(eventSeating.getText().toString());
        } else {
            new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Missing fields")
                    .setContentText("Please enter seating number.")
                    .show();
        }

        if (isValidText(eventDescription.getText().toString())){
            eventAbout=eventDescription.getText().toString();
        } else {
            new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Missing fields")
                    .setContentText("Please enter a description.")
                    .show();
        }

        Boolean gmaps=false;
        if (btnGmaps.getText().toString().equalsIgnoreCase("Event venue")){
            new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Missing fields")
                    .setContentText("Please select a venue.")
                    .show();
            gmaps=false;
        } else {
            gmaps=true;
        }

        if (filePath==null){
            new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Missing image")
                    .setContentText("Please select an image before continuing.")
                    .show();
        }

        if (isValidText(eventSeating.getText().toString().trim()) && isValidText(eventName.getText().toString().trim()) && isValidText(eventCost.getText().toString().trim()) && isValidText(eventDescription.getText().toString().trim()) && gmaps && filePath!=null){
            showProgress();

            String startTime=btnDatePicker.getText().toString()+" "+btnTimePicker.getText().toString();
            String endTime=btnDatePicker2.getText().toString()+" "+btnTimePicker2.getText().toString();
            int discount=0;
            String discountAlgType="NoDiscount";
            PrefManager pref = new PrefManager(getApplicationContext());
            long owner=Long.parseLong(pref.getMobile());
            Entranze event = new Entranze();
            GPS gpscords = new GPS();
            gpscords.setLatitude(latCords);
            gpscords.setLongitude(longCords);
            event.setAPrivate(privacy);
            event.setAvailableSeats(seatingAvailable);
            event.setRegisteredSeats(seatingAvailable);
            event.setOwner(owner);
            //event.setDiscountAlgorithmType(chosenCurrency);
            event.setDescription(eventAbout);
            event.setTitle(title);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setGpsCordinates(gpscords);
            event.setVenueName(venuePlace);
            event.setPriceInCents(priceInCents);
            event.setDiscountAlgorithmType(discountAlgType);
            event.setEstimatedDiscountInCents(0);

            String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

            Call<AddEntranzeResponse> call = ServiceLocator.INSTANCE.getMyApiService().addEventDetails(event);
            call.enqueue(new Callback<AddEntranzeResponse>() {

                @Override
                public void onResponse(Call<AddEntranzeResponse> call, Response<AddEntranzeResponse> response) {
                    if (response.body()!=null) {
                        if (response.body().getSuccess()) {
                            hideProgress();
                            uploadImageToServer(response.body().getData().getId().toString());
                        } else {
                            new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(response.body().getMessage())
                                    .show();
                            hideProgress();
                        }
                    }else {
                        new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Error")
                                .setContentText("Cannot add event due to a connection issue. Please try again.")
                                .show();
                        hideProgress();
                    }
                }

                @Override
                public void onFailure(Call<AddEntranzeResponse> call, Throwable t) {
                    new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error")
                            .setContentText("Cannot add event due to a connection issue. Please try again.")
                            .show();
                    hideProgress();
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        if (position==0){
            privacy=false;
        } else {
            privacy=true;
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    //this method will upload the file
    private void uploadImageToServer(String eventId) {
        //if there is a file to upload
        if (imageURI != null) {
            //displaying a progress dialog while upload is going on
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
            pDialog.setTitleText("Uploading image...");
            pDialog.setCancelable(false);
            pDialog.show();
            //

            try {
                compressedImageFile = new Compressor(AddEventActivity.this).compressToFile(file);

            } catch (final IOException e) {

            }
            //
            String token = pref.getToken();
            final String authorization = "Bearer "+token;
            //
            Ion.with(this)
                    .load("https://entranze.bluespine.co.za/api/entranzes/"+eventId+"/avatar.file")
                    .uploadProgressHandler(new ProgressCallback() {
                        @Override
                        public void onProgress(long uploaded, long total) {
                            pDialog.setTitleText("Uploaded " + ((int) uploaded) + "%...");
                        }
                    })
                    .setTimeout(60 * 60 * 1000)
                     .setHeader("Authorization", authorization)
                    .setMultipartParameter("Authorization", authorization)
                    .setMultipartContentType("multipart/form-data")
                    .setMultipartFile("image", "image/jpeg", compressedImageFile)
                    .setMultipartFile("upload", "image/jpeg",compressedImageFile)
                    .asJsonObject()
                    // run a callback on completion
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            pDialog.hide();
                            if (e != null) {
                                new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Error")
                                        .setContentText("Error uploading image. Please try again.")
                                        .show();
                                return;
                            }
                            new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Awesome!")
                                .setContentText("Event successfully added.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        setResult(AddEventActivity.RESULT_OK);
                                        eventAdded = true;
                                        finish();
                                    }
                                })
                                .show();
                        }
                    });
        }
    }

    public Boolean isValidText(String text){
        if(text.isEmpty() || text.length() == 0 || text.equals("") || text == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            int month = monthOfYear + 1;
                            String formattedMonth = "" + month;
                            String formattedDayOfMonth = "" + dayOfMonth;

                            if(month < 10){

                                formattedMonth = "0" + month;
                            }
                            if(dayOfMonth < 10){

                                formattedDayOfMonth = "0" + dayOfMonth;
                            }

                            btnDatePicker.setText(year + "-" + (formattedMonth) + "-" +formattedDayOfMonth);
                            btnDatePicker2.setText(year + "-" + (formattedMonth) + "-" +formattedDayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String hourString;
                            if (hourOfDay < 10)
                                hourString = "0" + hourOfDay;
                            else
                                hourString = "" +hourOfDay;

                            String minuteSting;
                            if (minute < 10)
                                minuteSting = "0" + minute;
                            else
                                minuteSting = "" +minute;

                            btnTimePicker.setText(hourString + ":" + minuteSting+":00");


                            }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        //
        if (v == btnDatePicker2) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            int month = monthOfYear + 1;
                            String formattedMonth = "" + month;
                            String formattedDayOfMonth = "" + dayOfMonth;

                            if(month < 10){

                                formattedMonth = "0" + month;
                            }
                            if(dayOfMonth < 10){

                                formattedDayOfMonth = "0" + dayOfMonth;
                            }
                            btnDatePicker2.setText(year+ "-" + ( formattedMonth) + "-" +formattedDayOfMonth);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
        if (v == btnTimePicker2) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String hourString;
                            if (hourOfDay < 10)
                                hourString = "0" + hourOfDay;
                            else
                                hourString = "" +hourOfDay;

                            String minuteSting;
                            if (minute < 10)
                                minuteSting = "0" + minute;
                            else
                                minuteSting = "" +minute;

                            btnTimePicker2.setText(hourString + ":" + minuteSting+":00");
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    //
    // Check location //
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    public void showProgress() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
        pDialog.setTitleText("Adding event...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void hideProgress() {
        pDialog.hide();
    }
}
