package com.godlontonconsulting.entranze.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.adapters.ManageEventsAdapter;
import com.godlontonconsulting.entranze.adapters.ViewGatekeepersAdapter;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.Event;
import com.godlontonconsulting.entranze.pojos.GateKeepers;
import com.godlontonconsulting.entranze.pojos.ViewGateKeepers;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.utils.UtilFunction;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Energy on 2017/06/10.
 */

public class ViewGatekeepersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar mProgressBar;

    private TextView txtTitle;
    RecyclerView mRecyclerView;
    PrefManager pref;

    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gatekeepers);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        txtTitle = (TextView) findViewById(R.id.txtTitle);

        txtTitle.setText(getIntent().getStringExtra("EVENT_TITLE"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("View Your Gatekeepers");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pref = new PrefManager(getApplicationContext());

        String token = pref.getToken();
        String authorization = "Bearer "+token;
        getGatekeepers(getIntent().getStringExtra("EVENT_ID"),authorization);
    }
    //
    private void getGatekeepers(String id,String authorization){
           showProgress();
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            final ViewGatekeepersAdapter mCardAdapter = new ViewGatekeepersAdapter(this);
            mRecyclerView.setAdapter(mCardAdapter);
            mCardAdapter.clear();
            //
            MyApiService service = MyApiService.retrofit.create(MyApiService.class);
            Call<List<ViewGateKeepers>> call = service.getGateKeepers(id,authorization);
            call.enqueue(new Callback<List<ViewGateKeepers>>() {

                @Override
                public void onResponse(Call<List<ViewGateKeepers>> call, Response<List<ViewGateKeepers>> response) {
                    if (response.body()!=null){
                        hideProgress();
                        if(response.body().size()<=0) {
                            showErrorNoGatekeepers();
                        } else {
                            mCardAdapter.addData(response.body());
                        }
                    } else {
                        hideProgress();
                        showErrorNoGatekeepers();
                    }
                }

                @Override
                public void onFailure(Call<List<ViewGateKeepers>> call, Throwable t) {
                    hideProgress();
                    showError();
                }
            });
    }
    //
    private void showError(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No connection...")
                .setContentText("Could not connect to server.")
                .show();
    }
    //
    private void showErrorNoGatekeepers(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No assigned gatekeepers")
                .setContentText("Please assign gatekeepers.")
                .show();
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
