package com.godlontonconsulting.entranze.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.AssignGateKeepersResponseDTO;
import com.godlontonconsulting.entranze.pojos.GateTokenResponse;
import com.godlontonconsulting.entranze.pojos.TicketResponse;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.godlontonconsulting.entranze.utils.EntranzeApp;
import com.godlontonconsulting.entranze.utils.NoInternetDialog;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestActivityBehavior;
import com.uber.sdk.android.rides.RideRequestButton;

import net.glxn.qrgen.android.QRCode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextView txtStart, txtEnd,txtTitle,txtLocation,txtPrice;
    Button btnPurchase;
    RideRequestButton rideRequestButton;
    double latUber;
    double lonUber;
    String qrToken,title,start,end,description;
    ImageView eventImage;
    LinearLayout ticket;
    SweetAlertDialog pDialogs;
    public static boolean ticketTransfered=false;
    PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_event_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnPurchase = (Button) findViewById(R.id.btnPurchase);
        txtTitle= (TextView) findViewById(R.id.event_title);
        txtPrice= (TextView) findViewById(R.id.event_cost);
        txtStart= (TextView) findViewById(R.id.activity_event_details_starts);
        txtEnd = (TextView) findViewById(R.id.activity_event_details_ends);
        txtLocation = (TextView) findViewById(R.id.venue);
        ticket= (LinearLayout) findViewById(R.id.ticket);
        ticket.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pDialogs = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String lat =  String.valueOf(latUber);
                    String lon =  String.valueOf(lonUber);
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
            }
        });
        //
        rideRequestButton=(RideRequestButton) findViewById(R.id.rideUber);
        eventImage=(ImageView) findViewById(R.id.eventimage);

        pref = new PrefManager(getApplicationContext());
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        String imageUri = "https://entranze.bluespine.co.za/api/entranzes/"+getIntent().getStringExtra("EVENT_ID")+"/avatar.file";
        ServiceLocator.INSTANCE.getPicasso(this).load(imageUri).fit().centerCrop().into(eventImage);

        Activity activity = this; // If you're in a fragment you must get the containing Activity!
        int requestCode = 1234;
        rideRequestButton.setRequestBehavior(new RideRequestActivityBehavior(activity, requestCode));
        // Optional, default behavior is to use current location for pickup
        RideParameters rideParams = new RideParameters.Builder()
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                .setDropoffLocation(latUber, lonUber, "Cape Town", "South Africa")
                .build();
        rideRequestButton.setRideParameters(rideParams);
        //
        generateQRCodeFromToken(getIntent().getStringExtra("EVENT_TICKETID"));
        getEvent();
    }
    //
    private void generateQRCodeFromToken(String ticketId){
        PrefManager pref = new PrefManager(getApplicationContext());
        String token = pref.getToken();
        String authorization = "Bearer "+token;
        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<GateTokenResponse> call = service.qrCodeFromToken(ticketId,authorization);
        call.enqueue(new Callback<GateTokenResponse>() {
            @Override
            public void onResponse(Call<GateTokenResponse> call, Response<GateTokenResponse> response) {
                if (response.body().getSuccess()){
                    qrToken=response.body().getData().getToken();
                    //
                    Bitmap myBitmap = QRCode.from(qrToken).withSize(200, 200).bitmap();
                    ImageView myImage = (ImageView) findViewById(R.id.qrCode);
                    myImage.setImageBitmap(myBitmap);
                } else {
                   showError();
                }
            }

            @Override
            public void onFailure(Call<GateTokenResponse> call, Throwable t) {
                showError();
            }
        });
    }
    //
    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "E MMMM dd,yyyy hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    //
    private void TransferATicket(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.custominputdialogtransfer, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (isValidPhoneNumber(userInput.getText().toString().trim())) {
                                    transferTicket("27"+mobileNumberFormat(userInput.getText().toString().trim()));
                                } else {
                                    new SweetAlertDialog(TicketDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Please enter valid mobile number")
                                            .show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{9,10}$";
        return mobile.matches(regEx);
    }
    //
    private String mobileNumberFormat(String number){
        number = number.startsWith("0") ? number.substring(1) : number;
        return number;
    }
    //
    private void transferTicket (String mobile){
        PrefManager pref = new PrefManager(getApplicationContext());
        String token = pref.getToken();
        String authorization = "Bearer "+token;
        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<AssignGateKeepersResponseDTO> call = service.transferTicket(getIntent().getStringExtra("PURCHASE_ID"),mobile,authorization);
        call.enqueue(new Callback<AssignGateKeepersResponseDTO>() {
            @Override
            public void onResponse(Call<AssignGateKeepersResponseDTO> call, Response<AssignGateKeepersResponseDTO> response) {
                if (response.body().getSuccess()){
                    new SweetAlertDialog(TicketDetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Awesome!")
                            .setContentText("Ticket successfully transfered.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    finish();
                                }
                            })
                            .show();
                } else {
                    new SweetAlertDialog(TicketDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error")
                            .setContentText(response.body().getMessage())
                            .show();
                }
            }
            @Override
            public void onFailure(Call<AssignGateKeepersResponseDTO> call, Throwable t) {
//                new SweetAlertDialog(TicketDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Error")
//                        .setContentText("Cannot access or connect to server at this time, please try again later.")
//                        .show();
                //
                new SweetAlertDialog(TicketDetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Awesome!")
                        .setContentText("Ticket successfully transfered.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                ticketTransfered=true;
                                finish();
                            }
                        })
                         .show();
            }
        });
    }
    //
    private void getEvent(){
        if (EntranzeApp.hasNetworkConnection(this)) {
            //showProgress();
            PrefManager pref = new PrefManager(this);
            String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
            Call<TicketResponse> call = ServiceLocator.INSTANCE.getMyApiService().getEvent(getIntent().getStringExtra("EVENT_ID"));
            call.enqueue(new Callback<TicketResponse>() {
                @Override
                public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getSuccess()) {
                            String date = response.body().getData().getStartTime();
                            String input = parseDateToddMMyyyy(date);
                            String date2 =response.body().getData().getEndTime();
                            String input2 = parseDateToddMMyyyy(date2);
                            txtStart.setText(input);
                            txtEnd.setText(input2);
                            txtPrice.setText("R "+response.body().getData().getPriceInCents().toString());
                            txtTitle.setText(response.body().getData().getTitle());
                            txtLocation.setText(response.body().getData().getVenueName());
                            //
                            latUber=response.body().getData().getGpsCordinates().getLatitude();
                            lonUber=response.body().getData().getGpsCordinates().getLongitude();
                            ticket.setVisibility(View.VISIBLE);
                            //
                            title=response.body().getData().getTitle();
                            start=response.body().getData().getStartTime();
                            end=response.body().getData().getEndTime();
                            description=response.body().getData().getDescription();
                            //
                           // hideProgress();
                        } else {
                            showError();
                           // hideProgress();
                        }
                    }else {
                        showError();
                        //hideProgress();
                    }
                }

                @Override
                public void onFailure(Call<TicketResponse> call, Throwable t) {

                    showError();
                }
            });
        }
        else {
            showErrorNoInternet();
        }
    }
    //
    public void showProgress() {
        pDialogs.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
        pDialogs.setTitleText("Loading...");
        pDialogs.setCancelable(false);
        pDialogs.show();
    }
    //
    private void showErrorNoInternet(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No internet connection.")
                .setContentText("Please check your internet connection and try again.")
                .show();
    }
    //
    private void showError(){
        new SweetAlertDialog(TicketDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No connection...")
                .setContentText("Could not connect to server.")
                .show();
    }
    //
    public void hideProgress() {
        pDialogs.hide();
    }
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_transfer:
                TransferATicket();
                return true;
            case R.id.action_map:
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latUber + "," +lonUber);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                return true;
            case R.id.action_add_to_calendar:
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, title);
                intent.putExtra(CalendarContract.Events.DESCRIPTION, description);
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,end);
                startActivity(intent);
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ticket_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //
    @Override
    public void onBackPressed() {
        ticketTransfered=true;
        super.onBackPressed();
        finish();
    }
}
