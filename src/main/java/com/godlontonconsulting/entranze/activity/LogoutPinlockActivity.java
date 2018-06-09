package com.godlontonconsulting.entranze.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.app.Config;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Energy on 2017/05/02.
 */

public class LogoutPinlockActivity extends AppCompatActivity {
    private PrefManager pref;
    private TextView etName;
    private CircularImageView imgProfile;
    private File profile_pic;

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the Above View
        setContentView(R.layout.activity_logoutpin);
        //getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        etName = (TextView) findViewById(R.id.namefrag);
        //btnLogin = (Button) findViewById(R.id.login);

        imgProfile = (CircularImageView) findViewById(R.id.imgProfile);
        imgProfile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        //mPinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
        //mPinLockView.enableLayoutShuffling();

        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
        //

        pref = new PrefManager(LogoutPinlockActivity.this.getApplicationContext());
        HashMap<String, String> profile = pref.getUserDetails();
        etName.setText(profile.get("name"));
        //
        bindValues();
        //
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            if (pref.getLoginPin().equalsIgnoreCase(pin)){
                pref.logInReg();
                Intent intent = new Intent(LogoutPinlockActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }else {
                new SweetAlertDialog(LogoutPinlockActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Incorrect Pin")
                        .setContentText("Please enter valid pin details to login.")
                        .show();
            }
        }

        @Override
        public void onEmpty() {
           // Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
           // Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };


    private void bindValues() {
        SharedPreferences prefs = LogoutPinlockActivity.this.getSharedPreferences(Config.USER_PROFILE, 0);
        String restoredPic = prefs.getString("profilepic", "");
        imgProfile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //if (!loginData.getData().getImageProfile().isEmpty()) {
        if (!restoredPic.isEmpty()) {
            profile_pic = new File(restoredPic);
            Picasso.with(getApplicationContext()).load(profile_pic).placeholder(R.drawable.ic_no_profile_pic).into(imgProfile);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {

       // super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void finish() {
        super.finish();
    }
}
