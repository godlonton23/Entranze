package com.godlontonconsulting.entranze.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.activity.EventDetailsActivity;
import com.godlontonconsulting.entranze.activity.GetFollowersActivity;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.Entranze;

import com.godlontonconsulting.entranze.pojos.Fav;
import com.godlontonconsulting.entranze.pojos.FollowDTO;
import com.godlontonconsulting.entranze.pojos.GetFollowers;
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


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>  {

    private List<Entranze> mItems;
    private Activity activity;
    private List<Entranze> mFilteredList;

    private boolean fav=false;

    private static RecyclerView horizontalList;

    public EventAdapter(Activity activity) {
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.events_view, viewGroup, false);
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

        final PrefManager pref = new PrefManager(activity);
        final String tokenRefresh = pref.getToken();

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
                intent.putExtra("EVENT_START", events.getStartTime());
                intent.putExtra("EVENT_END", events.getEndTime());
//                if (events.getEstimatedDiscountInCents()==0){
//                    intent.putExtra("EVENT_CURRENCY", "R");
//                }else if (events.getEstimatedDiscountInCents()==1){
//                    intent.putExtra("EVENT_CURRENCY", "$");
//                }else if (events.getEstimatedDiscountInCents()==2){
//                    intent.putExtra("EVENT_CURRENCY", "R");
//                }else if (events.getEstimatedDiscountInCents()==3){
//                    intent.putExtra("EVENT_CURRENCY", "£");
//                }
                intent.putExtra("EVENT_CURRENCY", "R");
                intent.putExtra("EVENT_DETAILS", events.getDescription());
                intent.putExtra("EVENT_ID", events.getId().toString());
                intent.putExtra("EVENT_LAT", events.getGpsCordinates().getLatitude().toString());
                intent.putExtra("EVENT_LONG", events.getGpsCordinates().getLongitude().toString());
                intent.putExtra("EVENT_VENUE", events.getVenueName());
                intent.putExtra("EVENT_ISGATEKEEPER", events.getAmGateKeeper().toString());
                intent.putExtra("EVENT_COST", events.getPriceInCents().toString());
                intent.putExtra("EVENT_FOLLOW", events.getFollowersCount().toString());
                intent.putExtra("EVENT_ISFOLLOW", events.getAmFollower().toString());
                intent.putExtra("EVENT_AVAILABLE", events.getAvailableSeats().toString());
                intent.putExtra("EVENT_MOBILE", Long.toString(events.getOwner()));
                intent.putExtra("EVENT_PRIVATE", events.getAPrivate().toString());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
            }
        });
        //

        if (events.getFavourite()) {
            viewHolder.fav.setFavorite(true, false);
        } else {
            viewHolder.fav.setFavorite(false, false);
        }

        viewHolder.fav.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        //if(fav){
                            if (favorite){
                                String tokenRefresh = pref.getToken();
                                ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
                                Fav fav= new Fav();
                                fav.setId(events.getId());
                                Call<FollowDTO> call = ServiceLocator.INSTANCE.getMyApiService().postFav(fav);
                                call.enqueue(new Callback<FollowDTO>() {
                                    @Override
                                    public void onResponse(Call<FollowDTO> call, Response<FollowDTO> response) {
                                        if (response.body() != null) {
                                            if (response.body().getSuccess()) {
//                                                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
//                                                        .setTitleText("Awesome!")
//                                                        .setContentText("Event successfully added to favs.")
//                                                        .show();
                                            } else {
//                                                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                                                        .setTitleText("Error")
//                                                        .setContentText(response.body().getMessage())
//                                                        .show();
                                                viewHolder.fav.setFavorite(false, false);
                                            }
                                        }else {
//                                            new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                                                    .setTitleText("Error")
//                                                    .setContentText("Error adding fav. Please try again.")
//                                                    .show();
                                            viewHolder.fav.setFavorite(false, false);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<FollowDTO> call, Throwable t) {
//                                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                                                .setTitleText("Error")
//                                                .setContentText("Error adding fav. Please try again.")
//                                                .show();
                                        viewHolder.fav.setFavorite(false, false);
                                    }
                                });
                            } else {
                                String tokenRefresh = pref.getToken();
                                ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
                                Fav fav= new Fav();
                                fav.setId(events.getId());
                                Call<FollowDTO> call = ServiceLocator.INSTANCE.getMyApiService().unpostFav(fav);
                                call.enqueue(new Callback<FollowDTO>() {
                                    @Override
                                    public void onResponse(Call<FollowDTO> call, Response<FollowDTO> response) {
                                        if (response.body() != null) {
                                            if (response.body().getSuccess()) {
//                                                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
//                                                        .setTitleText("Awesome!")
//                                                        .setContentText("Event removed from your favs.")
//                                                        .show();
                                            } else {
//                                                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                                                        .setTitleText("Error")
//                                                        .setContentText(response.body().getMessage())
//                                                        .show();
                                                viewHolder.fav.setFavorite(true, false);
                                            }
                                        }else {
//                                            new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                                                    .setTitleText("Error")
//                                                    .setContentText("Error removing fav. Please try again.")
//                                                    .show();
                                            viewHolder.fav.setFavorite(true, false);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<FollowDTO> call, Throwable t) {
//                                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                                                .setTitleText("Error")
//                                                .setContentText("Error removing fav. Please try again.")
//                                                .show();
                                        viewHolder.fav.setFavorite(true, false);
                                    }
                                });
                        }
                    }
                });
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
        public MaterialFavoriteButton fav;
        public CardView cView;
        final public ImageView eventPic;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            start = (TextView) itemView.findViewById(R.id.start);
            month = (TextView) itemView.findViewById(R.id.txtMonth);
            cView = (CardView) itemView.findViewById(R.id.cardView);
            fav = (MaterialFavoriteButton) itemView.findViewById(R.id.tog);
            venue = (TextView) itemView.findViewById(R.id.location);
            eventPic = (ImageView) itemView.findViewById(R.id.eventpic);
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        fav=false;
        mItems.clear();
        if (charText.length() == 0) {
            mItems.addAll(mFilteredList);
        } else {
            for (Entranze wp : mFilteredList) {
                if (wp.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getVenueName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mItems.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}