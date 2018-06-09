package com.godlontonconsulting.entranze.fragment;
//

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.activity.AddEventActivity;
import com.godlontonconsulting.entranze.activity.EventDetailsActivity;
import com.godlontonconsulting.entranze.activity.HomeActivity;
import com.godlontonconsulting.entranze.activity.UpdateEventActivity;
import com.godlontonconsulting.entranze.adapters.EventAdapter;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.Event;
import com.godlontonconsulting.entranze.pojos.NewRefreshAccessToken;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.godlontonconsulting.entranze.utils.EntranzeApp;
import com.godlontonconsulting.entranze.utils.NoInternetDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    RecyclerView mRecyclerView;
    EventAdapter mCardAdapter;
    SweetAlertDialog pDialog;
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getEvents();
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        getRefresherToken();

        setHasOptionsMenu(true);
        setRetainInstance(true);
        return view;
    }

    private void showError(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops!")
                .setContentText("We're having difficulty connecting to the server. Check your connection or try again later.")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        getEvents();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void showErrorNoInternet(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No internet connection.")
                .setContentText("Please check your internet connection and try again.")
                .show();
    }

    private void getEvents(){
        if (EntranzeApp.hasNetworkConnection(getActivity())) {
            //showProgress();
            final PrefManager pref = new PrefManager(getActivity());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCardAdapter = new EventAdapter(getActivity());
            mRecyclerView.setAdapter(mCardAdapter);
            mCardAdapter.clear();
            String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

            Call<Event> call = ServiceLocator.INSTANCE.getMyApiService().getEventList();
            call.enqueue(new Callback<Event>() {

                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {
                    if (response.body() != null) {
                        if (response.body().getSuccess()) {
                            mCardAdapter.addData(response.body().getData());
                            // Stop refresh animation
                            mSwipeRefreshLayout.setRefreshing(false);
                            //hideProgress();
                        } else {
                            //hideProgress();
                            showError();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }else {
                        hideProgress();
                        showError();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {
                    hideProgress();
                    showError();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        else {
            showErrorNoInternet();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getRefresherToken(){
        final PrefManager pref = new PrefManager(getActivity());
        String tokenRefresh = pref.getRefreshToken();
        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<NewRefreshAccessToken> call = service.getNewAccessToken("refresh_token",tokenRefresh);
        call.enqueue(new Callback<NewRefreshAccessToken>() {
            @Override
            public void onResponse(Call<NewRefreshAccessToken> call, Response<NewRefreshAccessToken> response) {
                if (response.body() !=null){
                    pref.createAccessToken(response.body().getAccessToken());
                    pref.createRefreshToken(response.body().getRefreshToken());
                    getEvents();
                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error!")
                            .setContentText("We're having difficulty connecting to the server. Check your connection or try again later.")
                            .show();
                    String noToken="";
                    //Toast.makeText(getApplicationContext(), "Cannot access or connect to server at this time, please try again later.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NewRefreshAccessToken> call, Throwable t) {
                Log.e("Home Fragment", t.toString());
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
//            PrefManager pref = new PrefManager(getActivity().getApplicationContext());
//            getEvents(pref.getToken());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void searchList(String query) {
        if (mCardAdapter!=null)mCardAdapter.filter(query);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddEventActivity.RESULT_OK){
            getRefresherToken();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if(AddEventActivity.eventAdded){
            AddEventActivity.eventAdded=false;
            getRefresherToken();
        }

        if(EventDetailsActivity.followAdded){
            EventDetailsActivity.followAdded=false;
            getRefresherToken();
        }

        if(UpdateEventActivity.updatedAdded){
            UpdateEventActivity.updatedAdded=false;
            getRefresherToken();
        }
    }

    public void showProgress() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
        pDialog.setTitleText("Loading events...");
        pDialog.setCancelable(false);
        pDialog.show();
    }


    public void hideProgress() {
        pDialog.hide();
    }
}


