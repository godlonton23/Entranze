package com.godlontonconsulting.entranze.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.AssignGateKeepers;
import com.godlontonconsulting.entranze.pojos.AssignGateKeepersResponseDTO;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.service.ServiceLocator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageYourEventDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextView txtStart, txtEnd,txtPrice,txtDetails, txtVenue,txtTitle,txtAvailable;;
    Button btnAssignGateKeeper,btnUpdate,btnCheckGatekeeper;
    ImageView eventImages;
    PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnAssignGateKeeper = (Button) findViewById(R.id.btnAssignGateKeeper);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        txtAvailable = (TextView) findViewById(R.id.available);
        txtAvailable.setText("Available seats: "+getIntent().getStringExtra("EVENT_AVAILABLE"));

        btnAssignGateKeeper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AssignGatekeepers();
            }
        });

        btnCheckGatekeeper= (Button) findViewById(R.id.btnCheckGateKeeper);
        btnCheckGatekeeper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageYourEventDetailsActivity.this, ViewGatekeepersActivity.class);
                //final PaymentsParcelableData ticket = pItems.get(i);
                intent.putExtra("EVENT_TITLE", getIntent().getStringExtra("EVENT_TITLE"));
                intent.putExtra("EVENT_ID", getIntent().getStringExtra("EVENT_ID"));
                startActivity(intent);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageYourEventDetailsActivity.this, UpdateEventActivity.class);
                intent.putExtra("EVENT_TITLE",  getIntent().getStringExtra("EVENT_TITLE"));
                intent.putExtra("EVENT_START",  getIntent().getStringExtra("EVENT_START"));
                intent.putExtra("EVENT_END",  getIntent().getStringExtra("EVENT_END"));
                intent.putExtra("EVENT_DETAILS",  getIntent().getStringExtra("EVENT_DETAILS"));
                intent.putExtra("EVENT_ID",  getIntent().getStringExtra("EVENT_ID"));
                intent.putExtra("EVENT_LAT",  getIntent().getStringExtra("EVENT_LAT"));
                intent.putExtra("EVENT_LONG",  getIntent().getStringExtra("EVENT_LONG"));
                intent.putExtra("EVENT_VENUE",  getIntent().getStringExtra("EVENT_VENUE"));
                intent.putExtra("EVENT_ISGATEKEEPER",  getIntent().getStringExtra("EVENT_ISGATEKEEPER"));
                intent.putExtra("EVENT_COST", getIntent().getStringExtra("EVENT_COST").toString());
                intent.putExtra("EVENT_AVAILABLE",  getIntent().getStringExtra("EVENT_AVAILABLE"));

                startActivity(intent);
                finish();
            }
        });

        txtStart= (TextView) findViewById(R.id.starts);
        txtEnd = (TextView) findViewById(R.id.ends);
        txtPrice = (TextView) findViewById(R.id.price);
        txtTitle= (TextView) findViewById(R.id.event_title);

        txtDetails = (TextView) findViewById(R.id.description);
        txtVenue=(TextView) findViewById(R.id.locationvenue);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Manage My Event");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //
        txtTitle.setText(getIntent().getStringExtra("EVENT_TITLE"));
        //
        String date = getIntent().getStringExtra("EVENT_START");
        String input = parseDateToddMMyyyy(date);
        String date2 = getIntent().getStringExtra("EVENT_END");
        String input2 = parseDateToddMMyyyy(date2);
        //
        txtStart.setText(input);
        txtEnd.setText(input2);

        if (getIntent().getStringExtra("EVENT_COST").equalsIgnoreCase("R 0")){
            txtPrice.setText("FREE");
        }else{
            txtPrice.setText("R " + getIntent().getStringExtra("EVENT_COST"));
        }

        txtDetails.setText(getIntent().getStringExtra("EVENT_DETAILS"));
        txtVenue.setText(getIntent().getStringExtra("EVENT_VENUE"));

        eventImages=(ImageView) findViewById(R.id.eventimage);
        eventImages.setScaleType(ImageView.ScaleType.FIT_XY);
        pref = new PrefManager(getApplicationContext());
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        String imageUri = "https://entranze.bluespine.co.za/api/entranzes/"+getIntent().getStringExtra("EVENT_ID")+"/avatar.file";
        ServiceLocator.INSTANCE.getPicasso(this).load(imageUri).fit().centerCrop().into(eventImages);

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
    private void AssignGatekeepers(){

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.custominputdialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                //result.setText(userInput.getText());
                                if (isValidPhoneNumber(userInput.getText().toString().trim())) {
                                    // request for sms
                                    // progressBar.setVisibility(View.VISIBLE);
                                    assignGateKeeper("27"+mobileNumberFormat(userInput.getText().toString().trim()));
                                } else {
                                    new SweetAlertDialog(ManageYourEventDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Please enter valid phone number.")
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
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    //
    private void assignGateKeeper (String mobile){
        PrefManager pref = new PrefManager(getApplicationContext());
        String token = pref.getToken();
        String authorization = "Bearer "+token;
        AssignGateKeepers gatekeeper = new AssignGateKeepers(Long.parseLong(mobile));

        List<AssignGateKeepers> gatekeepers = new ArrayList<AssignGateKeepers>();
        gatekeepers.add(gatekeeper);
        //
        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<AssignGateKeepersResponseDTO> call = service.assignGateKeeper(getIntent().getStringExtra("EVENT_ID"),authorization,gatekeepers);
        call.enqueue(new Callback<AssignGateKeepersResponseDTO>() {

            @Override
            public void onResponse(Call<AssignGateKeepersResponseDTO> call, Response<AssignGateKeepersResponseDTO> response) {

                if (response.body()!=null) {
                    if (response.body().getSuccess()){
                        new SweetAlertDialog(ManageYourEventDetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Awesome!")
                                .setContentText("You successfully added a new gatekeeper.")
                                .show();
                    } else {
                        showError();
                    }
                }else {
                    showErrorNameRegistered();
                }
            }

            @Override
            public void onFailure(Call<AssignGateKeepersResponseDTO> call, Throwable t) {
                showError();
            }
        });
    }
    //
    private void showError(){
        new SweetAlertDialog(ManageYourEventDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No connection...")
                .setContentText("Could not connect to server.")
                .show();
    }
    //
    private void showErrorNameRegistered(){
        new SweetAlertDialog(ManageYourEventDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Error...")
                .setContentText("Gatekeeper already registered.")
                .show();
    }
    //

    //
    private String mobileNumberFormat(String number){
        number = number.startsWith("0") ? number.substring(1) : number;
        return number;
    }
    //
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
}
