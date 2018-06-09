package com.godlontonconsulting.entranze.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.adapters.FavsAdapter;
import com.godlontonconsulting.entranze.adapters.ManageEventsAdapter;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.Event;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.service.ServiceLocator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Energy on 2017/06/10.
 */

public class FavEventsActivity extends AppCompatActivity {


    private Toolbar toolbar;
    SweetAlertDialog pDialog;
    RecyclerView mRecyclerView;
    PrefManager pref;
    FavsAdapter mCardAdapter;
    TextView refreshTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_events);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Favourite Events");

        mRecyclerView = (RecyclerView) findViewById(R.id.favs_view);
        refreshTxt = (TextView)findViewById(R.id.refreshTxt);
        refreshTxt.setVisibility(View.INVISIBLE);
        refreshTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFavEvents();
            }
        });
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getFavEvents();

    }
    //
    private void getFavEvents(){
        showProgress();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCardAdapter = new FavsAdapter(this);
        mRecyclerView.setAdapter(mCardAdapter);
        mCardAdapter.clear();
        PrefManager pref = new PrefManager(this);
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

        Call<Event> call = ServiceLocator.INSTANCE.getMyApiService().getFavs();
        call.enqueue(new Callback<Event>() {

            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        mCardAdapter.addData(response.body().getData());
                        hideProgress();
                    } else {
                        hideProgress();
//                            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
//                                    .setTitleText("No Favourites")
//                                    .setContentText("No favourites selected yet.")
//                                    .show();
                    }
                }else {
                    hideProgress();
//                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText("No Favourites")
//                                .setContentText("No favourites selected yet.")
//                                .show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                hideProgress();
                showError();
                //refreshTxt.setVisibility(View.VISIBLE);
            }
        });

    }
    //
    @Override
    public void onResume() {
        super.onResume();
           // getFavEvents();
    }

    public void showProgress() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void showError(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No connection...")
                .setContentText("Could not connect to server.")
                .show();
    }
    //
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void hideProgress() {
        pDialog.hide();
    }

}
