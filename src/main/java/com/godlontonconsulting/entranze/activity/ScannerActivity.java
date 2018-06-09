package com.godlontonconsulting.entranze.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.AuthGateToken;
import com.godlontonconsulting.entranze.pojos.AuthorizeTokenResponse;
import com.godlontonconsulting.entranze.pojos.GateTokenResponse;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.google.zxing.Result;
import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.base.BaseScannerActivity;

import net.glxn.qrgen.android.QRCode;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    private static final String FLASH_STATE = "FLASH_STATE";

    private ZXingScannerView mScannerView;
    private boolean mFlash;

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scanner);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Scan Ticket");

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
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

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
    }

    @Override
    public void handleResult(Result rawResult) {
        //Toast.makeText(ScannerActivity.this, "Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        authorizeToken(rawResult.getText());
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(ScannerActivity.this);
//            }
//        }, 2000);
    }

    private void authorizeToken(String ticketId){

        PrefManager pref = new PrefManager(getApplicationContext());

        String token = pref.getToken();
        String entranzeId=getIntent().getStringExtra("EVENT_ID");
        AuthGateToken authToken = new AuthGateToken();
        authToken.setToken(ticketId);

        String authorization = "Bearer "+token;

        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<AuthorizeTokenResponse> call = service.scanTicket(authorization,entranzeId,authToken);
        call.enqueue(new Callback<AuthorizeTokenResponse>() {

            @Override
            public void onResponse(Call<AuthorizeTokenResponse> call, Response<AuthorizeTokenResponse> response) {

                if (response.body()!=null) {
                    if (response.body().getSuccess()) {
                        new SweetAlertDialog(ScannerActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Thank you.")
                                    .setContentText("Ticket successfully validated.")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                            finish();
                                    }
                                }).show();
                    } else {
                        new SweetAlertDialog(ScannerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Sorry")
                                .setContentText("Invalid/used ticket.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        finish();
                                    }})
                                .show();
                    }
                }else{
                    new SweetAlertDialog(ScannerActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("Something went wrong when connecting. Please try again.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    finish();
                                }})
                            .show();
                }
            }

            @Override
            public void onFailure(Call<AuthorizeTokenResponse> call, Throwable t) {
                new SweetAlertDialog(ScannerActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                finish();
                            }})
                        .setContentText("Something went wrong when connecting. Please try again.")
                        .show();
            }
        });
    }

    public void toggleFlash(View v) {
        mFlash = !mFlash;
        mScannerView.setFlash(mFlash);
    }
    //
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void finish() {
        super.finish();
    }
}