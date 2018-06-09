package com.godlontonconsulting.entranze.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.godlontonconsulting.entranze.R;

import com.godlontonconsulting.entranze.adapters.ManageEventsAdapter;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.Event;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.utils.UtilFunction;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Created by Energy on 2017/06/10.
 */

public class ManageEventsActivity extends AppCompatActivity {


    private Toolbar toolbar;
    SweetAlertDialog pDialog;
    RecyclerView mRecyclerView;
    PrefManager pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Manage My Events");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pref = new PrefManager(getApplicationContext());

        String token = pref.getToken();
        String authorization = "Bearer "+token;
        getEvents(authorization);

    }
    //
    private void getEvents(String authorization){
        showProgress();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ManageEventsAdapter mCardAdapter = new ManageEventsAdapter(this);
        mRecyclerView.setAdapter(mCardAdapter);
        mCardAdapter.clear();
        //
        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<Event> call = service.getEventList(authorization);
        call.enqueue(new Callback<Event>() {

            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.body()!=null) {
                    if (response.body().getSuccess()) {
                        for (int x = 0; x < response.body().getData().size(); x++) {
                            if (response.body().getData().get(x).getOwner() == Float.valueOf(pref.getMobileNumber())) {
                                mCardAdapter.addData(response.body().getData().get(x));
                            }
                        }
                        //
                        hideProgress();
                    } else {
                        hideProgress();
                        new SweetAlertDialog(ManageEventsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText(response.body().getMessage().toString())
                                .show();
                    }
                }else {
                    hideProgress();
                    new SweetAlertDialog(ManageEventsActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("Something went wrong when connecting. Please try again.")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                hideProgress();
                new SweetAlertDialog(ManageEventsActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops")
                        .setContentText("Something went wrong when connecting. Please try again.")
                        .show();
            }
        });
    }
    //
    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if(UpdateEventActivity.updatedAdded){
            UpdateEventActivity.updatedAdded=false;
            String token = pref.getToken();
            String authorization = "Bearer "+token;
            getEvents(authorization);
        }
    }
    //
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showProgress() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void hideProgress() {
        pDialog.hide();
    }

}
