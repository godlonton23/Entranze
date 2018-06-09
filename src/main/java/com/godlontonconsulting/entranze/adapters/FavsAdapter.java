package com.godlontonconsulting.entranze.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.activity.EventDetailsActivity;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.Entranze;
import com.godlontonconsulting.entranze.pojos.Fav;
import com.godlontonconsulting.entranze.pojos.FollowDTO;
import com.godlontonconsulting.entranze.service.ServiceLocator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FavsAdapter extends RecyclerView.Adapter<FavsAdapter.ViewHolder>  {

    private List<Entranze> mItems;
    private Activity activity;
    private List<Entranze> mFilteredList;

    public FavsAdapter(Activity activity) {
        super();
        this.activity = activity;
        mItems = new ArrayList<Entranze>();
        mFilteredList= new ArrayList<Entranze>();
    }
    public void addData(List<Entranze> events) {
        mItems.addAll(events);
        mFilteredList.addAll(events);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fav_events_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Entranze events = mItems.get(i);
        viewHolder.title.setText(events.getTitle());

        String date = events.getStartTime();
        String input = parseDateToddMMyyyy(date);

        String[] dateparts = input.split("-");
        String day = dateparts[0];
        String monthString = dateparts[1];

        viewHolder.start.setText(day);
        viewHolder.month.setText(monthString);
        viewHolder.venue.setText(events.getVenueName());
        viewHolder.cView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EventDetailsActivity.class);
                intent.putExtra("EVENT_TITLE", events.getTitle());
                //if (events.getDiscountAlgorithmType()==null){
                    intent.putExtra("EVENT_CURRENCY", "R");
//                }else {
//                    intent.putExtra("EVENT_CURRENCY", events.getDiscountAlgorithmType());
//                }
                intent.putExtra("EVENT_START", events.getStartTime());
                intent.putExtra("EVENT_END", events.getEndTime());
                intent.putExtra("EVENT_DETAILS", events.getDescription());
                intent.putExtra("EVENT_ID", events.getId().toString());
                intent.putExtra("EVENT_LAT", events.getGpsCordinates().getLatitude().toString());
                intent.putExtra("EVENT_LONG", events.getGpsCordinates().getLongitude().toString());
                intent.putExtra("EVENT_VENUE", events.getVenueName());
                intent.putExtra("EVENT_ISGATEKEEPER", events.getAmGateKeeper().toString());
                intent.putExtra("EVENT_COST", "R " + events.getPriceInCents());
                intent.putExtra("EVENT_FOLLOW", events.getFollowersCount().toString());
                intent.putExtra("EVENT_AVAILABLE", events.getAvailableSeats().toString());
                intent.putExtra("EVENT_MOBILE", Long.toString(events.getOwner()));
                intent.putExtra("EVENT_PRIVATE", events.getAPrivate().toString());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
            }
        });
        //
        final PrefManager pref = new PrefManager(activity);
        final String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        String imageUri = "https://entranze.bluespine.co.za/api/entranzes/"+events.getId().toString()+"/avatar.file";
        ServiceLocator.INSTANCE.getPicasso(activity).load(imageUri).fit().centerCrop().into(viewHolder.eventPic);
    }


    public void updateList(List<Entranze> list){
        mItems = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView start;
        public TextView month;
        public TextView venue;
        public CardView cView;
        final public ImageView eventPic;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            start = (TextView) itemView.findViewById(R.id.start);
            month = (TextView) itemView.findViewById(R.id.txtMonth);
            cView = (CardView) itemView.findViewById(R.id.cardView);
            venue = (TextView) itemView.findViewById(R.id.location);
            eventPic = (ImageView) itemView.findViewById(R.id.eventpic);

        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mItems.clear();
        if (charText.length() == 0) {
            mItems.addAll(mFilteredList);
        } else {
            for (Entranze wp : mFilteredList) {
                if (wp.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mItems.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


}