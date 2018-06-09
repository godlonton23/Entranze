package com.godlontonconsulting.entranze.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.activity.FollowersEventsActivity;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.CustomersDTO;
import com.godlontonconsulting.entranze.pojos.Entries;

import com.godlontonconsulting.entranze.service.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>  {

    private List<Entries> mItems;
    private Activity activity;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private List<Entries> mFilteredList;
    String phoneNo;
    String message;

    public ContactsAdapter(Activity activity) {
        super();
        this.activity = activity;
        mItems = new ArrayList<Entries>();
    }

    public void addData( List<Entries> entry) {
        mItems.addAll(entry);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Entries entry = mItems.get(i);
        //final String mobile=Long.toString(events.getMsisdn());
        PrefManager pref = new PrefManager(activity);
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

        viewHolder.cname.setText(entry.getName());
        viewHolder.contact.setText(entry.getPhone());
//        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
//        String imageUri = "https://accounts.bluespine.co.za/api/customers/"+mobile+"/avatar.file";
//        ServiceLocator.INSTANCE.getPicasso(activity).load(imageUri).resize(60, 60).centerCrop().placeholder(R.drawable.ic_no_profile_pic).into(viewHolder.profPic);
//
        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage(entry.getPhone());
            }
        });
    }

    protected void sendSMSMessage(String phone) {
        phoneNo = phone;
        message = "Join Entranze - the all-in-one events based app. 'https://play.google.com/store/apps/details?id=com.godlontonconsulting.entranze'";
        //
        Uri uri = Uri.parse("smsto:" + phoneNo);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", message);
        activity.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView cname;
        public TextView contact;
        public CircleImageView profPic;

        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            cname = (TextView) itemView.findViewById(R.id.follower);
            profPic = (CircleImageView ) itemView.findViewById(R.id.imgProfile);
            contact=(TextView) itemView.findViewById(R.id.txtNumber);
            card= (CardView) itemView.findViewById(R.id.cardView);
        }
    }

}