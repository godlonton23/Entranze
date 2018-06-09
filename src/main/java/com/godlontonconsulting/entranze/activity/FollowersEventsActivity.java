package com.godlontonconsulting.entranze.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.adapters.FavsAdapter;
import com.godlontonconsulting.entranze.adapters.FollProfileAdapter;
import com.godlontonconsulting.entranze.adapters.ManageEventsAdapter;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.Event;
import com.godlontonconsulting.entranze.pojos.FollowDTO;
import com.godlontonconsulting.entranze.pojos.FollowUser;
import com.godlontonconsulting.entranze.pojos.GetFollowers;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.godlontonconsulting.entranze.utils.EntranzeApp;
import com.godlontonconsulting.entranze.utils.NoInternetDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Energy on 2017/06/10.
 */

public class FollowersEventsActivity extends AppCompatActivity {


    private Toolbar toolbar;
    SweetAlertDialog pDialog;
    RecyclerView mRecyclerView;
    PrefManager pref;
    CircleImageView propic;
    TextView tvEvents,tvFollowers,tvFollowing,txtDisplay;
    private int followers=0;
    Button follBtn;
    public static boolean followAdded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_eventsprofile);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("FOLLOWERS_NAME")+"'s"+" Events");
        mRecyclerView = (RecyclerView) findViewById(R.id.favs_view);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        propic= (CircleImageView) findViewById(R.id.profile_photo);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvEvents= (TextView) findViewById(R.id.tvPosts);
        tvFollowers= (TextView) findViewById(R.id.tvFollowers);
        tvFollowing= (TextView) findViewById(R.id.tvFollowing);
        tvFollowers.setText(getIntent().getStringExtra("FOLLOWING"));
        txtDisplay= (TextView) findViewById(R.id.display_name);
        txtDisplay.setText(getIntent().getStringExtra("FOLLOWERS_NAME"));
        follBtn=(Button)findViewById(R.id.btnFollowUser);
        //
        final PrefManager pref = new PrefManager(this);
        final String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        String imageUri = "https://accounts.bluespine.co.za/api/customers/"+getIntent().getStringExtra("EVENT_MOBILE")+"/avatar.file";
        ServiceLocator.INSTANCE.getPicasso(this).load(imageUri).fit().centerCrop().placeholder(R.drawable.ic_no_profile_pic).into( propic);

        getFollwersEvents();
        getFollowers();
        checkFollowers();

    }
    //
    private void getFollwersEvents(){
        showProgress();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final FollProfileAdapter mCardAdapter = new FollProfileAdapter(this);
        mRecyclerView.setAdapter(mCardAdapter);
        mCardAdapter.clear();
        PrefManager pref = new PrefManager(this);
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

        Call<Event> call = ServiceLocator.INSTANCE.getMyApiService().getEventList();
        call.enqueue(new Callback<Event>() {

            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        int numEvents=0;
                        for (int x = 0; x < response.body().getData().size(); x++) {
                            if (response.body().getData().get(x).getOwner() == Float.valueOf(getIntent().getStringExtra("EVENT_MOBILE"))) {
                                numEvents++;
                                mCardAdapter.addData(response.body().getData().get(x));
                            }
                        }
                        tvEvents.setText(Integer.toString(numEvents));
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
    private void getFollowers() {
        if (EntranzeApp.hasNetworkConnection(this)){

            PrefManager pref = new PrefManager(this);
            String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

            Call<GetFollowers> call = ServiceLocator.INSTANCE.getMyApiService().getFollowers();
            call.enqueue(new Callback<GetFollowers>() {

                @Override
                public void onResponse(Call<GetFollowers> call, Response<GetFollowers> response) {
                    if (response.body() != null) {
                        if (response.body().getSuccess()) {
                            followers=response.body().getData().size();
                            tvFollowing.setText(Integer.toString(followers));
                        } else {
                            // etFollow.setText("");
                        }
                    }else {
                        //etFollow.setText("");
                    }
                }

                @Override
                public void onFailure(Call<GetFollowers> call, Throwable t) {
                    // etFollow.setText("");
                }
            });
        }
        else {
            NoInternetDialog dialog = new NoInternetDialog(this);
            dialog.show();
        }
    }
    //
    private void followUser() {
        if (EntranzeApp.hasNetworkConnection(this)){
            showProgress();
            PrefManager pref = new PrefManager(this);
            String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

            FollowUser user = new  FollowUser();
            user.setMsisdn(Long.valueOf(getIntent().getStringExtra("EVENT_MOBILE")));

            Call<FollowDTO> call = ServiceLocator.INSTANCE.getMyApiService().followUser(user);
            call.enqueue(new Callback<FollowDTO>() {

                @Override
                public void onResponse(Call<FollowDTO> call, Response<FollowDTO> response) {
                    if (response.body() != null) {
                        if (response.body().getSuccess()) {
                            new SweetAlertDialog(FollowersEventsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Awesome!")
                                    .setContentText("You are now following "+getIntent().getStringExtra("FOLLOWERS_NAME"))
                                    .show();
                            followAdded=true;
                            follBtn.setText("Unfollow");
                            follBtn.setBackground(getResources().getDrawable(R.drawable.shape_follow));
                            follBtn.setTextColor(Color.WHITE);
                            follBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    unfollowUser();
                                }
                            });
                            hideProgress();
                        } else {
                            hideProgress();
                            new SweetAlertDialog(FollowersEventsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(response.body().getMessage())
                                    .show();
                        }
                    }else {
                        hideProgress();
                        new SweetAlertDialog(FollowersEventsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Error")
                                .setContentText("Cannot follow user due to a connection issue. Please try again.")
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<FollowDTO> call, Throwable t) {
                    hideProgress();
                    new SweetAlertDialog(FollowersEventsActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error")
                            .setContentText("Cannot follow user due to a connection issue. Please try again.")
                            .show();
                }
            });
        }
        else {
            NoInternetDialog dialog = new NoInternetDialog(this);
            dialog.show();
        }
    }
    //    //
    private void unfollowUser() {
        if (EntranzeApp.hasNetworkConnection(this)){
            showProgress();
            PrefManager pref = new PrefManager(this);
            String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

            Call<FollowDTO> call = ServiceLocator.INSTANCE.getMyApiService().unfollowUser(Long.valueOf(getIntent().getStringExtra("EVENT_MOBILE")));
            call.enqueue(new Callback<FollowDTO>() {

                @Override
                public void onResponse(Call<FollowDTO> call, Response<FollowDTO> response) {
                    if (response.body() != null) {
                        if (response.body().getSuccess()) {
                            new SweetAlertDialog(FollowersEventsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Awesome!")
                                    .setContentText("You are no longer following "+getIntent().getStringExtra("FOLLOWERS_NAME"))
                                    .show();
                            followAdded=true;
                            follBtn.setText("Follow");
                            follBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    followUser();
                                }
                            });
                            follBtn.setBackground(getResources().getDrawable(R.drawable.grey_border));
                            follBtn.setTextColor(Color.BLACK);
                            hideProgress();
                        } else {
                            hideProgress();
                            new SweetAlertDialog(FollowersEventsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(response.body().getMessage())
                                    .show();
                        }
                    }else {
                        hideProgress();
                        new SweetAlertDialog(FollowersEventsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Error")
                                .setContentText("Cannot unfollow user due to a connection issue. Please try again.")
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<FollowDTO> call, Throwable t) {
                    hideProgress();
                    new SweetAlertDialog(FollowersEventsActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error")
                            .setContentText("Cannot follow user due to a connection issue. Please try again.")
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
    private void checkFollowers() {
        if (getIntent().getStringExtra("EVENT_ISFOLLOW").equalsIgnoreCase("true")) {
            follBtn.setText("Unfollow");
            follBtn.setBackground(getResources().getDrawable(R.drawable.shape_follow));
            follBtn.setTextColor(Color.WHITE);
            follBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unfollowUser();
                }
            });

        } else {
            follBtn.setText("Follow");
            follBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followUser();
                }
            });
            follBtn.setTextColor(Color.BLACK);
            follBtn.setBackground(getResources().getDrawable(R.drawable.grey_border));
        }
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
