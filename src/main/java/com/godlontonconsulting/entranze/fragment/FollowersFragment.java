package com.godlontonconsulting.entranze.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.adapters.FollowersAdapter;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.GetFollowers;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.godlontonconsulting.entranze.utils.EntranzeApp;
import com.godlontonconsulting.entranze.utils.NoInternetDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FollowersFragment extends Fragment {

    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SweetAlertDialog pDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup vg,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers, vg, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getFollowers();
            }
        });
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        getFollowers();
        setRetainInstance(true);
        //
        return view;
    }

    private void getFollowers() {
        if (EntranzeApp.hasNetworkConnection(getActivity())){

            showProgress();
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            final FollowersAdapter mCardAdapter = new FollowersAdapter(getActivity());
            mRecyclerView.setAdapter(mCardAdapter);
            mCardAdapter.clear();

            PrefManager pref = new PrefManager(getActivity());
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
                        showError();
//                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText("Oops")
//                                .setContentText("Something went wrong when connecting to followers. Please try again.")
//                                .show();
                    }
                }

                @Override
                public void onFailure(Call<GetFollowers> call, Throwable t) {
                    hideProgress();
                    showError();
//                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
//                            .setTitleText("Oops")
//                            .setContentText("Something went wrong when connecting. Please try again.")
//                            .show();
                }
            });
        }
        else {
            showErrorNoInternet();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
           // getFollowers();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void showProgress() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void showError(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops!")
                .setContentText("We're having difficulty connecting to the server. Check your connection or try again later.")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        getFollowers();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void showErrorNoInternet(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No internet connection...")
                .setContentText("Please check your internet connection and try again.")
                .show();
    }

    public void hideProgress() {
        pDialog.hide();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}


