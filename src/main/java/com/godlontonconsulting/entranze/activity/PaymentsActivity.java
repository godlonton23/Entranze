package com.godlontonconsulting.entranze.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.RadioButton;
import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.helper.PrefManager;

public class PaymentsActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private PrefManager pref;

    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Payment Options");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       // radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);

        //CardView androidpay = (CardView) findViewById(R.id.apay);
        CardView paypal = (CardView) findViewById(R.id.ppal);
        CardView ccard = (CardView) findViewById(R.id.ccard);
        CardView cash = (CardView) findViewById(R.id.cash);

//        androidpay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                radioButton1.setChecked(true);
//                radioButton2.setChecked(false);
//                radioButton3.setChecked(false);
//                radioButton4.setChecked(false);
//            }
//        });

        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton2.setChecked(true);
               // radioButton1.setChecked(false);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
            }
        });

        ccard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton3.setChecked(true);
                radioButton2.setChecked(false);
                //radioButton1.setChecked(false);
                radioButton4.setChecked(false);
            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton4.setChecked(true);
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
               // radioButton1.setChecked(false);
            }
        });
    }
    //
    //
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }
}
