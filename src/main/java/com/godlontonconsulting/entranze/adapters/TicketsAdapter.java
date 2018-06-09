package com.godlontonconsulting.entranze.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.activity.TicketDetailsActivity;
import com.godlontonconsulting.entranze.helper.PrefManager;

import com.godlontonconsulting.entranze.pojos.Purchases;

import com.godlontonconsulting.entranze.pojos.TicketResponse;
import com.godlontonconsulting.entranze.service.ServiceLocator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.ViewHolder> implements Filterable {

    private List<Purchases> mItems;
    private Activity activity;
    private List<Purchases> mFilteredList;

    private String id;

    public TicketsAdapter(Activity activity) {
        super();
        this.activity = activity;
        mItems = new ArrayList<Purchases>();
    }

    public void addData(List<Purchases> events) {
        mItems.addAll(events);
        //pItems.addAll(eventNames);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.payments_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "E MMMM dd,yyyy hh:mm a";
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
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final Purchases events = mItems.get(i);
        String date = events.getPurchaseDate();
        String input = parseDateToddMMyyyy(date);
        viewHolder.paiddate.setText("Purchase date: "+ input);
        viewHolder.cost.setText("R " + events.getPriceCents());
        viewHolder.cView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TicketDetailsActivity.class);
                intent.putExtra("EVENT_ID", events.getEntranzeId().toString());
                intent.putExtra("PURCHASE_ID", events.getId().toString());
                intent.putExtra("EVENT_TICKETID", events.getId().toString());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
            }
        });
        viewHolder.cView.setVisibility(View.INVISIBLE);
        id=events.getEntranzeId().toString();
        PrefManager pref = new PrefManager(activity);
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        Call<TicketResponse> call = ServiceLocator.INSTANCE.getMyApiService().getEvent(id);
        call.enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        viewHolder.title.setText(response.body().getData().getTitle());
                        viewHolder.cView.setVisibility(View.VISIBLE);
                    } else {
                       /// showError();
                    }
                }else {
                   // showError();
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                //showError();
            }
        });
    }

    public void updateList(List<Purchases> list){
        mItems = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView cost;
        public TextView paiddate;
        public CardView cView;
        public LinearLayout ticket;
        final public ImageView eventPic;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            paiddate = (TextView) itemView.findViewById(R.id.paidon);
            cost = (TextView) itemView.findViewById(R.id.price);
            ticket= (LinearLayout) itemView.findViewById(R.id.ticket);
            cView = (CardView) itemView.findViewById(R.id.cardView);
            eventPic = (ImageView) itemView.findViewById(R.id.eventpic);
        }
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mItems;
                } else {
                    ArrayList<Purchases> filteredList = new ArrayList<>();
                    for (Purchases event : mItems) {
//                        if (event.getTitle().toLowerCase().contains(charString) || event.getDescription().toLowerCase().contains(charString) || event.getGoogleMapLocation().toString().toLowerCase().contains(charString)) {
//
//                            filteredList.add(event);
//                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Purchases>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}