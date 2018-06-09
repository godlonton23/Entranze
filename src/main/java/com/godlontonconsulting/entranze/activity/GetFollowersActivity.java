package com.godlontonconsulting.entranze.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.adapters.FollowersAdapter;
import com.godlontonconsulting.entranze.adapters.ManageEventsAdapter;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.Event;
import com.godlontonconsulting.entranze.pojos.GetFollowers;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.godlontonconsulting.entranze.utils.EntranzeApp;
import com.godlontonconsulting.entranze.utils.NoInternetDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Energy on 2017/06/10.
 */

public class GetFollowersActivity extends AppCompatActivity {


    private Toolbar toolbar;

    SweetAlertDialog pDialog;

    RecyclerView mRecyclerView;
    PrefManager pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_followers);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Followers");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getFollowers();
    }
    //
    private void getFollowers() {
        if (EntranzeApp.hasNetworkConnection(this)){

            showProgress();
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            final FollowersAdapter mCardAdapter = new FollowersAdapter(this);
            mRecyclerView.setAdapter(mCardAdapter);
            mCardAdapter.clear();

            PrefManager pref = new PrefManager(this);
            String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

            Call<GetFollowers> call = ServiceLocator.INSTANCE.getMyApiService().getFollowers();
            call.enqueue(new Callback<GetFollowers>() {

                @Override
                public void onResponse(Call<GetFollowers> call, Response<GetFollowers> response) {
                    if (response.body() != null) {
                        if (response.body().getSuccess()) {
                            mCardAdapter.addData(response.body().getData());
                            hideProgress();
                        } else {

                        }
                    }else {
                        hideProgress();
                        new SweetAlertDialog(GetFollowersActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText(response.body().getMessage().toString())
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<GetFollowers> call, Throwable t) {
                    hideProgress();
                    new SweetAlertDialog(GetFollowersActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("Something went wrong when connecting. Please try again.")
                            .show();
                }
            });
        }
        else {
            NoInternetDialog dialog = new NoInternetDialog(this);
            dialog.show();
        }
    }
    //

    //
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
