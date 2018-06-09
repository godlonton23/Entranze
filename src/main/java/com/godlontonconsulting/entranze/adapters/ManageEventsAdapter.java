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
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.activity.ManageYourEventDetailsActivity;
import com.godlontonconsulting.entranze.pojos.Entranze;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ManageEventsAdapter extends RecyclerView.Adapter<ManageEventsAdapter.ViewHolder> implements Filterable {

    private List<Entranze> mItems;
    private Activity activity;

    private List<Entranze> mFilteredList;

    public ManageEventsAdapter(Activity activity) {
        super();
        this.activity = activity;
        mItems = new ArrayList<Entranze>();
    }

    public void addData(Entranze event) {
        mItems.add(event);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.manage_events_view, viewGroup, false);
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
        viewHolder.cost.setText("R "+events.getPriceInCents().toString());

        String date = events.getStartTime();
        String input = parseDateToddMMyyyy(date);

        String[] dateparts = input.split("-");
        String day = dateparts[0];
        String monthString = dateparts[1];

        viewHolder.start.setText(day);
        viewHolder.month.setText(monthString);

        viewHolder.cView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ManageYourEventDetailsActivity.class);
                intent.putExtra("EVENT_TITLE", events.getTitle());
                intent.putExtra("EVENT_START", events.getStartTime());
                intent.putExtra("EVENT_END", events.getEndTime());
                intent.putExtra("EVENT_DETAILS", events.getDescription());
                intent.putExtra("EVENT_ID", events.getId().toString());
                intent.putExtra("EVENT_LAT", events.getGpsCordinates().getLatitude().toString());
                intent.putExtra("EVENT_LONG", events.getGpsCordinates().getLongitude().toString());
                intent.putExtra("EVENT_VENUE", events.getVenueName());
                intent.putExtra("EVENT_ISGATEKEEPER", events.getAmGateKeeper().toString());
                //if (events.getDiscountAlgorithmType()==null){
                    intent.putExtra("EVENT_CURRENCY", "R");
                //}else {
                    //intent.putExtra("EVENT_CURRENCY", events.getDiscountAlgorithmType());
                //}
                intent.putExtra("EVENT_COST", events.getPriceInCents().toString());
                intent.putExtra("EVENT_AVAILABLE", events.getAvailableSeats().toString());
                //activity.finish();
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView start;
        public TextView month;
        public TextView cost;
        public CardView cView;

        final public ImageView eventPic;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            start = (TextView) itemView.findViewById(R.id.start);
            month = (TextView) itemView.findViewById(R.id.txtMonth);
            cView = (CardView) itemView.findViewById(R.id.cardView);
            cost = (TextView) itemView.findViewById(R.id.cost);
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
                    ArrayList<Entranze> filteredList = new ArrayList<>();
                    for (Entranze event : mItems) {

                        if (event.getTitle().toLowerCase().contains(charString) || event.getDescription().toLowerCase().contains(charString) || event.getVenueName().toString().toLowerCase().contains(charString)) {

                            filteredList.add(event);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Entranze>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}