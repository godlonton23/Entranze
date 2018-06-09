package com.godlontonconsulting.entranze.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.activity.ManageEventsActivity;
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


public class MyEventsFragment extends Fragment {

    RecyclerView mRecyclerView;
    TextView refreshTxt;
    SweetAlertDialog pDialog;
    PrefManager pref;

    public View onCreateView(LayoutInflater inflater, ViewGroup vg,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liked, vg, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favs_view);
        refreshTxt = (TextView) view.findViewById(R.id.refreshTxt);
        refreshTxt.setVisibility(View.INVISIBLE);
        refreshTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEvents();
            }
        });
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        getEvents();
        setRetainInstance(true);
        //
        return view;
    }

    private void getEvents(){
        showProgress();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ManageEventsAdapter mCardAdapter = new ManageEventsAdapter(getActivity());
        mRecyclerView.setAdapter(mCardAdapter);
        mCardAdapter.clear();
        final PrefManager pref = new PrefManager(getActivity());
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

        Call<Event> call = ServiceLocator.INSTANCE.getMyApiService().getEventList();
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
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText(response.body().getMessage().toString())
                                .show();
                    }
                }else {
                    hideProgress();
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("Something went wrong when connecting. Please try again.")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                hideProgress();
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops")
                        .setContentText("Something went wrong when connecting. Please try again.")
                        .show();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
           // getFavEvents();
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
                .setTitleText("No connection...")
                .setContentText("Could not connect to server.")
                .show();
    }

    public void hideProgress() {
        pDialog.hide();
    }
}


