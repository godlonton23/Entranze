package com.godlontonconsulting.entranze.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.activity.TicketDetailsActivity;
import com.godlontonconsulting.entranze.adapters.ContactsAdapter;

import com.godlontonconsulting.entranze.helper.PrefManager;

import com.godlontonconsulting.entranze.pojos.Entries;
import com.godlontonconsulting.entranze.pojos.GetContacts;
import com.godlontonconsulting.entranze.service.ServiceLocator;


import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ContactsFragment extends Fragment {

    SweetAlertDialog pDialog;
    RecyclerView mRecyclerViews;
    PrefManager pref;

    ArrayList<Entries> Contacts ;
    Cursor cursor ;
    String namecont, phonenumber ;

    public View onCreateView(LayoutInflater inflater, ViewGroup vg,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactlist, vg, false);
        Contacts = new ArrayList<Entries>();
        mRecyclerViews = (RecyclerView) view.findViewById(R.id.contacts_view);
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        GetContactsIntoArrayList();
        return view;
    }

    public void GetContactsIntoArrayList(){
        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        while (cursor.moveToNext()) {
            namecont = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Entries entry=new Entries();
            entry.setName(namecont);
            entry.setPhone(phonenumber);
            Contacts.add(entry);
        }
        cursor.close();
        //postContacts();
        addContacts();
    }

    private void addContacts() {

        mRecyclerViews.setHasFixedSize(true);
        mRecyclerViews.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ContactsAdapter mCardAdapter = new ContactsAdapter(getActivity());
        mRecyclerViews.setAdapter(mCardAdapter);
        mCardAdapter.clear();
        mCardAdapter.addData(Contacts);
    }

    private void postContacts() {

        PrefManager pref = new PrefManager(getActivity());
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

        Call<GetContacts> call = ServiceLocator.INSTANCE.getMyApiService().postContacts(Contacts);
        call.enqueue(new Callback<GetContacts>() {

            @Override
            public void onResponse(Call<GetContacts> call, Response<GetContacts> response) {
                if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        String success=response.body().getMessage().toString();
                    } else {
                        String fail=response.body().getMessage().toString();
                    }
                }else {
                    String fail="failed";
                }
            }

            @Override
            public void onFailure(Call<GetContacts> call, Throwable t) {
                String fail="failed";
            }
        });

    }


    private void showError(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No connection...")
                .setContentText("Could not connect to server.")
                .show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
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
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == TicketDetailsActivity.RESULT_OK){

        }
    }

    @Override
    public void onResume() {
        super.onResume();
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


