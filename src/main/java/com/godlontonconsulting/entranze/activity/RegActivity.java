package com.godlontonconsulting.entranze.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.GetContacts;
import com.godlontonconsulting.entranze.pojos.GetFollowers;
import com.godlontonconsulting.entranze.pojos.InstallDtoResponse;
import com.godlontonconsulting.entranze.pojos.InstallUser;

import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.godlontonconsulting.entranze.utils.EntranzeApp;
import com.godlontonconsulting.entranze.utils.NoInternetDialog;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = RegActivity.class.getSimpleName();

    public static AppCompatButton btnRequestSms;
    private EditText inputName, inputMobile;
    private PrefManager pref;

    String name = "";
    String surname = "";
    public static String mobile = "";
    public final static int PERM_REQUEST_CODE_DRAW_OVERLAYS = 1234;

    CountryCodePicker ccp;
    String countryCode="27";
    SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        int PERMISSION_ALL = 1;

        String[] PERMISSIONS = {Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        inputName = (EditText) findViewById(R.id.inputName);
        inputMobile = (EditText) findViewById(R.id.inputMobile);
        btnRequestSms = (AppCompatButton) findViewById(R.id.btn_login);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode= ccp.getDefaultCountryCode();
            }
        });

        TextView textView =(TextView)findViewById(R.id.tcs);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.entranze-app.com/termsandconditions.pdf'> By signing up you agree to Entranze's term of service, privacy policy and community guidlines. </a>";
        textView.setText(Html.fromHtml(text));

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        pref = new PrefManager(getApplicationContext());
        btnRequestSms.setOnClickListener(this);
        //
        inputName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputName.setText("");
            }
        });
        inputMobile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputMobile.setText("");
            }
        });
        int maxLength = 10;
        inputMobile.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        pref = new PrefManager(this);
        if (pref.isLoggedIn()) {
            if (pref.getLoginPin()==null) {
                Intent intent = new Intent(RegActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }else {
                if (pref.isLogReg()){
                    Intent intent = new Intent(RegActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(RegActivity.this, LogoutPinlockActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
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

    public void permissionToDrawOverlays() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
            }
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                validateForm();
//                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Hang tight!")
//                        .setContentText("We're busy rebuilding a new fresh backend service.")
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                btnRequestSms.setEnabled(true);
//                                sDialog.dismissWithAnimation();
//                            }
//                        })
//                        .show();

                break;
        }
    }
    /**
     * Validating user details form
     */
    private void validateForm() {
        name = inputName.getText().toString().trim();
       // surname = inputSurname.getText().toString().trim();
        surname = "Empty";
        countryCode= ccp.getDefaultCountryCode();
        mobile = countryCode+ mobileNumberFormat(inputMobile.getText().toString().trim());
        // validating empty name and email
        if (name.length() == 0) {
            new SweetAlertDialog(RegActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Missing fields")
                    .setContentText("Please enter your details")
                    .show();
            return;
        }
        // validating mobile number
        // it should be of 10 digits length
        if (isValidPhoneNumber(inputMobile.getText().toString().trim())) {
            // request for sms
          // progressBar.setVisibility(View.VISIBLE);
            registerUser();
        } else {
            new SweetAlertDialog(RegActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Error")
                    .setContentText("Please enter valid mobile number")
                    .show();
        }
    }

    public static void finishRegistration(){

    }

    private void registerUser(){
        if (EntranzeApp.hasNetworkConnection(this)) {
            btnRequestSms.setEnabled(false);
            final ProgressDialog progressDialog = new ProgressDialog(RegActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Registering you on our network...");
            progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            onLoginSuccess();
                            // onLoginFailed();
                           // progressDialog.dismiss();
                        }
                    }, 25000);
        //
            // requesting for sms
            InstallUser user = new  InstallUser("ZAF",Long.valueOf(mobile).longValue(), name, surname);
            MyApiService service = MyApiService.retrofit.create(MyApiService.class);
            Call<InstallDtoResponse> call = service.registerUser(user);
            call.enqueue(new Callback<InstallDtoResponse>() {

                @Override
                public void onResponse(Call<InstallDtoResponse> call, Response<InstallDtoResponse> response) {

                    if (response.isSuccessful()) {
                        progressDialog.setMessage("Sending an SMS and auto-registering you on our network...");
                       //progressBar.setVisibility(View.INVISIBLE);
                        // saving the mobile number in shared preferences
                        pref.setMobileNumber(mobile);
                        pref.createActivateUser(name, surname,mobile);
                        //
                        //finish();
                    } else {
                        ErrorResponse();
                    }
                }

                @Override
                public void onFailure(Call<InstallDtoResponse> call, Throwable t) {
                    ErrorResponse();
                }
        });
        }else {
            NoInternetDialog dialog = new NoInternetDialog(this);
            dialog.show();
        }
    }

    public void ErrorResponse(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops!")
                .setContentText("We're having difficulty connecting to the server. Check your connection or try again later.")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        btnRequestSms.setEnabled(true);
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private String mobileNumberFormat(String number){
        number = number.startsWith("0") ? number.substring(1) : number;
        return number;
    }

    public void onLoginSuccess() {
        btnRequestSms.setEnabled(true);
        //finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
       // }
        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!Settings.canDrawOverlays(this)) {
                    // ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
                }
            }
        }
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


