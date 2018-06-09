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
import com.godlontonconsulting.entranze.adapters.FavsAdapter;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.Event;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LikedFragment extends Fragment {

    RecyclerView mRecyclerView;
    TextView refreshTxt;
    FavsAdapter mCardAdapter;
    SweetAlertDialog pDialog;

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
                getFavEvents();
            }
        });
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        getFavEvents();
        setRetainInstance(true);
        //
        return view;
    }

    private void getFavEvents(){
            showProgress();
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCardAdapter = new FavsAdapter(getActivity());
            mRecyclerView.setAdapter(mCardAdapter);
            mCardAdapter.clear();
            PrefManager pref = new PrefManager(getActivity());
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
//            getFavEvents();
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


