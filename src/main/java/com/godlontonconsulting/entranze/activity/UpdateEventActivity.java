package com.godlontonconsulting.entranze.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import com.godlontonconsulting.entranze.app.Config;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.AddEntranzeResponse;
import com.godlontonconsulting.entranze.pojos.AddEvents;
import com.godlontonconsulting.entranze.pojos.GPS;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static java.lang.Integer.parseInt;


/**
 * Created by Energy on 2017/05/26.
 */

public class UpdateEventActivity extends AppCompatActivity implements View.OnClickListener,LocationListener,AdapterView.OnItemSelectedListener {

    EditText eventName;
    EditText eventDescription;
    Button btnAddEvent;
    private static int PLACE_PICKER_REQUEST = 1;
    Button btnDatePicker, btnTimePicker, btnDatePicker2, btnTimePicker2,btnGmaps;
    TextView eventLocation,eventSeating,eventCost;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private CharSequence[] options;
    private File profile_pic;
    private boolean isImageset = false;
    private String updatedImage;
    private ImageView imgAdd;
    private Toolbar toolbar;
    private FloatingActionButton fAddImage;
    private boolean isImage;
    public static boolean updatedAdded=false;
    private double latCords;
    private double longCords;
    private String venuePlace;
    private boolean privacy=false;
    SweetAlertDialog pDialog;
    PrefManager pref;
    File file,compressedImageFile ;
    Uri imageURI;
    String filePath;
    private static final String QIP_DIR_NAME = "Entranze";
    private static final String TAG = "UpdateEventActivity" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_event);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Event");


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

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
        // Spinner element //
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
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
        eventName.setText(getIntent().getStringExtra("EVENT_TITLE"));
        eventCost = (TextView) findViewById(R.id.event_cost);
        eventCost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eventCost.setText("");
            }
        });
        eventCost.setText("R " + getIntent().getStringExtra("EVENT_COST"));
        eventSeating.setText(getIntent().getStringExtra("EVENT_AVAILABLE"));

        latCords= Double.parseDouble(getIntent().getStringExtra("EVENT_LAT"));
        longCords= Double.parseDouble(getIntent().getStringExtra("EVENT_LONG"));
        eventDescription = (EditText) findViewById(R.id.event_description);
        eventDescription.setHorizontallyScrolling(false);
        eventDescription.setMaxLines(Integer.MAX_VALUE);
        imgAdd = (ImageView) findViewById(R.id.imgAdd);
        pref = new PrefManager(getApplicationContext());
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        String imageUri = "https://entranze.bluespine.co.za/api/entranzes/"+getIntent().getStringExtra("EVENT_ID")+"/avatar.file";
        ServiceLocator.INSTANCE.getPicasso(this).load(imageUri).fit().centerCrop().into(imgAdd);

        eventDescription.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eventDescription.setText("");
            }
        });
        eventDescription.setText(getIntent().getStringExtra("EVENT_DETAILS"));
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
        btnGmaps.setText(getIntent().getStringExtra("EVENT_VENUE"));
        btnGmaps.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //isImage=false;

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
                    // notify user.
                    AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateEventActivity.this);
                    dialog.setMessage("Locations services not enabled. Please check and enable your enable location services.");
                    dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                            //get gps //
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                        }
                    });
                    dialog.show();
                } else {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(UpdateEventActivity.this), PLACE_PICKER_REQUEST);
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

        String dateStart = getIntent().getStringExtra("EVENT_START");

        String[] dateparts = dateStart.split(" ");
        String date = dateparts[0];
        String time = dateparts[1];

        String dateEnd = getIntent().getStringExtra("EVENT_END");

        String[] dateparts2 = dateEnd.split(" ");
        String date2 = dateparts2[0];
        String time2 = dateparts2[1];

        btnDatePicker.setText(date);
        btnTimePicker.setText(time);

        btnTimePicker=(Button) findViewById(R.id.event_start_time);
        btnDatePicker2=(Button) findViewById(R.id.end_date);
        btnTimePicker2=(Button) findViewById(R.id.end_time);
        btnDatePicker2.setText(date2);
        btnTimePicker2.setText(time2);
        fAddImage=(FloatingActionButton) findViewById(R.id.fab);
        //
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnDatePicker2.setOnClickListener(this);
        btnTimePicker2.setOnClickListener(this);
        eventLocation=(TextView) findViewById(R.id.event_location);
        venuePlace=btnGmaps.getText().toString();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void selectImage() {
        isImage=true;
        QiPick.in(this).fromGallery();
    }
    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!isImage) {
            if(resultCode != RESULT_CANCELED){
                if (requestCode == PLACE_PICKER_REQUEST) {
                    if (resultCode == Activity.RESULT_OK) {
                        Place place = PlacePicker.getPlace(data, UpdateEventActivity.this);

                        double lat = place.getLatLng().latitude;
                        double lng = place.getLatLng().longitude;

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
//
//                        try {
//                            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                            String city = addresses.get(0).getLocality();
//                            String state = addresses.get(0).getAdminArea();
//                            String country = addresses.get(0).getCountryName();
//                            String postalCode = addresses.get(0).getPostalCode();
//                            String knownName = addresses.get(0).getFeatureName();
//                            //btnGmaps.setText(address + ", " + city);
//                            //venuePlace=address + ", " + city;
//                            btnGmaps.setText(address);
//                            venuePlace=address;
//                        } catch (IOException e) {
//
//                        }
                    }
                } else {
                    btnGmaps.setText("Event venue");
                    new SweetAlertDialog(UpdateEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Missing fields")
                            .setContentText( "Please select a venue.")
                            .show();
                }
            } else {
                btnGmaps.setText("Event venue");
                new SweetAlertDialog(UpdateEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Missing fields")
                        .setContentText( "Please select a venue.")
                        .show();
            }
        }else {
            QiPick.handleActivityResult(getApplicationContext(), requestCode, resultCode, data, this.mCallback);
        }
    }
    //
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

            final String extension = UriUtils.getFileExtension(UpdateEventActivity.this, pImageUri);
            Log.i(TAG, "Picked: " + pImageUri.toString() + "\nMIME type: " + UriUtils.getMimeType(UpdateEventActivity.this,
                    pImageUri) + "\nFile extension: " + extension + "\nRequest type: " + pRequestType);
            filePath=pImageUri.toString();
            try {
                final String ext = extension == null ? "" : "." + extension;
                final File outDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), QIP_DIR_NAME);
                Random rand = new Random();
                int  n = rand.nextInt(5000) + 1;
                file = new File(outDir, "qip_temp" + ext);
                //compressedImageFile = new Compressor(UpdateEventActivity.this).compressToFile(file);
                //noinspection ResultOfMethodCallIgnored
                outDir.mkdirs();
                // DO NOT do this on main thread. This is only for reference
                UriUtils.saveContentToFile(UpdateEventActivity.this, pImageUri, file);

            } catch (final IOException e) {
                Toast.makeText(UpdateEventActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }

        }



        @Override
        public void onMultipleImagesPicked(int i, @NonNull List<Uri> list) {

        }

        @Override
        public void onError(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final String pErrorString) {
            //Log.e(TAG, "Err: " + pErrorString);
        }

        @Override
        public void onCancel(@NonNull final PickSource pPickSource, final int pRequestType) {
           // Log.d(TAG, "Cancel: " + pPickSource.name());
        }

    };

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
                compressedImageFile = new Compressor(UpdateEventActivity.this).compressToFile(file);

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
                            // Displays the progress bar for the first time.
                            //mNotifyManager.notify(notificationId, mBuilder.build());
                            // mBuilder.setProgress((int) total, (int) uploaded, false);
                            // progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
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
                                new SweetAlertDialog(UpdateEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Error")
                                        .setContentText("Error uploading image. Please try again.")
                                        .show();
                                return;
                            }
                            new SweetAlertDialog(UpdateEventActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Awesome!")
                                    .setContentText("Event successfully updated.")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            setResult(UpdateEventActivity.RESULT_OK);
                                            updatedAdded = true;
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    });
        }
    }
    //

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
    /**
     * Validating user details form
     */
    private void validateForm() {

        showProgress();
        String title=eventName.getText().toString();
        int priceInCents=100;
        String eventAbout=eventDescription.getText().toString();
        int seatingAvailable=parseInt(eventSeating.getText().toString());
        int regSeats=parseInt(eventSeating.getText().toString());
        String startTime=btnDatePicker.getText().toString()+" "+btnTimePicker.getText().toString();
        String endTime=btnDatePicker2.getText().toString()+" "+btnTimePicker2.getText().toString();
        int discount=0;
        String discountAlgType="NoDiscount";
        PrefManager pref = new PrefManager(getApplicationContext());
        long owner=Long.parseLong(pref.getMobile());

        AddEvents event = new AddEvents();
        GPS gpscords = new GPS();
        gpscords.setLatitude(latCords);
        gpscords.setLongitude(longCords);
        event.setAPrivate(privacy);
        //event.setAvailableSeats(seatingAvailable);
        event.setRegisteredSeats(seatingAvailable);
        event.setOwner(owner);
        event.setDescription(eventAbout);
        event.setTitle(title);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setGpsCordinates(gpscords);
        event.setVenueName(venuePlace);
        event.setPriceInCents(priceInCents);
        event.setDiscountAlgorithmType(discountAlgType);
        event.setEstimatedDiscountInCents(0);

        String token = pref.getToken();
        final String authorization = "Bearer "+token;
        //
        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<AddEntranzeResponse> call = service.updateEventDetails(getIntent().getStringExtra("EVENT_ID"),authorization,event);
        call.enqueue(new Callback<AddEntranzeResponse>() {

            @Override
            public void onResponse(Call<AddEntranzeResponse> call, Response<AddEntranzeResponse> response) {
                if (response.body()!=null) {
                    if (response.body().getSuccess()) {
                        if (isImage) {
                            uploadImageToServer(getIntent().getStringExtra("EVENT_ID"));
                        } else {
                            new SweetAlertDialog(UpdateEventActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Awesome!")
                                    .setContentText("Event successfully updated.")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            setResult(UpdateEventActivity.RESULT_OK);
                                            updatedAdded = true;
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                        hideProgress();
                    } else {
                        new SweetAlertDialog(UpdateEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Error")
                                .setContentText(response.body().getMessage())
                                .show();
                        hideProgress();
                        if (response.body().getMessage().equalsIgnoreCase("You need to capture an Entranze avatar before updating further!")){
                            if (isImage) {
                                uploadImageToServer(getIntent().getStringExtra("EVENT_ID"));
                            }
                        }
                    }
                }else {
                    new SweetAlertDialog(UpdateEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error")
                            .setContentText("Cannot add event due to a connection issue. Please try again.")
                            .show();
                    hideProgress();
                }
            }

            @Override
            public void onFailure(Call<AddEntranzeResponse> call, Throwable t) {
                new SweetAlertDialog(UpdateEventActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Error")
                        .setContentText("Cannot add event due to a connection issue. Please try again.")
                        .show();
                hideProgress();
            }
        });
    }


    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {
            // Get Current Date //
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
        pDialog.setTitleText("Updating...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void hideProgress() {
        pDialog.hide();
    }
}
