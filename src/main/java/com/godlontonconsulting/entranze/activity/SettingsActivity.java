package com.godlontonconsulting.entranze.activity;



import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.helper.PrefManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingsActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Switch push = (Switch) findViewById(R.id.push);
        CardView setpin = (CardView) findViewById(R.id.setpin);
        //
        setpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPin();
            }
        });

        TextView textView =(TextView)findViewById(R.id.rateus);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://play.google.com/store/apps/details?id=com.godlontonconsulting.entranze'> Rate us </a>";
        textView.setText(Html.fromHtml(text));

        pref = new PrefManager(this);
        if (pref.getPush()) {
            push.setChecked(true);
        }else {
            push.setChecked(false);
        }
        //
        push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    pref.turnPushOn();
                }else {
                    pref.turnPushOff();
                }
            }
        });
    }
    //
    private void SetPin(){

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.custominputsetpin, null);

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
                                if (userInput.getText().toString().trim().length() < 4) {
                                    new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Please enter a pin that is 4 digits.")
                                            .show();
                                }else {
                                    new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Awesome!")
                                            .setContentText("New personal PIN set.")
                                            .show();
                                    pref.setLoginPin(userInput.getText().toString().trim());
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
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }
}
