package com.godlontonconsulting.entranze.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.DefaultItemAnimator;
import com.godlontonconsulting.entranze.R;

import com.godlontonconsulting.entranze.activity.TicketDetailsActivity;
import com.godlontonconsulting.entranze.adapters.TicketsAdapter;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.EntranzePurchases;
import com.godlontonconsulting.entranze.pojos.Purchases;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.godlontonconsulting.entranze.utils.EntranzeApp;


import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class TicketsFragment extends Fragment {

    SweetAlertDialog pDialog;
    RecyclerView mRecyclerViews;
    PrefManager pref;
    SwipeRefreshLayout mSwipeRefreshLayout;
    //
    ArrayList<Purchases> idPayedList;

    public View onCreateView(LayoutInflater inflater, ViewGroup vg,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets, vg, false);

        mRecyclerViews = (RecyclerView) view.findViewById(R.id.recycler_view);
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        mSwipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getTickets();
            }
        });
        pref = new PrefManager(getActivity().getApplicationContext());

        getTickets();
        return view;
    }

    private void getTickets(){
        if (EntranzeApp.hasNetworkConnection(getActivity())) {
            //
            mRecyclerViews.setHasFixedSize(true);
            mRecyclerViews.setLayoutManager(new LinearLayoutManager(getActivity()));
            final TicketsAdapter mCardAdapter = new TicketsAdapter(getActivity());
            mRecyclerViews.setAdapter(mCardAdapter);
            //
            mCardAdapter.clear();
            //showProgress();
            //
            PrefManager pref = new PrefManager(getActivity());
            String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

            Call<EntranzePurchases> call = ServiceLocator.INSTANCE.getMyApiService().getPurchases();
            call.enqueue(new Callback<EntranzePurchases>() {

                @Override
                public void onResponse(Call<EntranzePurchases> call, Response<EntranzePurchases> response) {
                    if (response.body() != null) {
                        if (response.body().getSuccess()) {
                            mCardAdapter.addData(response.body().getData());
                            // Offline Tickets //
                            PrefManager pref = new PrefManager(getActivity());
                            String str =  pref.getTickets();
                            Type type = new TypeToken<ArrayList<Purchases>>() { }.getType();
                            //
                            if (str!=null) {
                                idPayedList = new Gson().fromJson(str, type);
                                Purchases dataObj = new  Purchases();
                                idPayedList.add(dataObj);
                                String dataTicketStr = new Gson().toJson(idPayedList);
                                pref.createTicketList(dataTicketStr);
                            }
                            // End Offline tickets //
                            //
                            //hideProgress();
                        } else {
                           // hideProgress();
                            showError();
                        }
                    }else {
                        //hideProgress();
                        getOffLineTickets();
                        showError();
                    }
                }
                @Override
                public void onFailure (Call < EntranzePurchases > call, Throwable t){
                    //hideProgress();
                    getOffLineTickets();
                    showError();
                }

            });
        }
        else {
            //hideProgress();
            getOffLineTickets();
            showErrorNoInternet();
        }
    }

    private void getOffLineTickets() {
        String str = pref.getTickets();
        Type type = new TypeToken<ArrayList<Purchases>>() {
        }.getType();
        ArrayList<Purchases> idPayedList = new Gson().fromJson(str, type);

        if (idPayedList != null) {
            if (idPayedList.size() <= 0) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                               .setTitleText("Oops!")
                        .setContentText("No tickets bought.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                getTickets();
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }

            DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
            mRecyclerViews.setHasFixedSize(true);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerViews.setLayoutManager(mLinearLayoutManager);
            mRecyclerViews.setItemAnimator(itemAnimator);
            TicketsAdapter adapter;
            adapter = new TicketsAdapter(getActivity());
            adapter.addData(idPayedList);
            mRecyclerViews.setAdapter(adapter);
        }else {
            //Toast.makeText(getActivity().getApplicationContext(), "No tickets.", Toast.LENGTH_LONG).show();
        }
    }


    private void showError(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops!")
                .setContentText("We're having difficulty connecting to the server. Check your connection or try again later.")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        //getTickets();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
           getTickets();
        }
    }


    private void showErrorNoInternet(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No internet connection.")
                .setContentText("Please check your internet connection and try again.")
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

//                if (mAdapter != null) mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == TicketDetailsActivity.RESULT_OK){
            getTickets();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        //if(TicketDetailsActivity.ticketTransfered){
          //  TicketDetailsActivity.ticketTransfered=false;
            getTickets();
       // }
    }

    public void showProgress() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void hideProgress() {
        mSwipeRefreshLayout.setRefreshing(false);
        pDialog.hide();
    }
}


