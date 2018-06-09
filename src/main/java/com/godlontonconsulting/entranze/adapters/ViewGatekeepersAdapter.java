package com.godlontonconsulting.entranze.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.pojos.ViewGateKeepers;
import java.util.ArrayList;
import java.util.List;


public class ViewGatekeepersAdapter extends RecyclerView.Adapter<ViewGatekeepersAdapter.ViewHolder> {

    private List<ViewGateKeepers> mItems;

    private Activity activity;


    public ViewGatekeepersAdapter(Activity activity) {
        super();
        this.activity = activity;
        mItems = new ArrayList<ViewGateKeepers>();
    }

    public void addData(List<ViewGateKeepers> event) {
        mItems.addAll(event);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewgatekeepers_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final ViewGateKeepers events = mItems.get(i);
        viewHolder.mobile.setText(String.valueOf(events.getMsisdn()));
    }

    public void updateList(List<ViewGateKeepers> list){
        mItems = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mobile;

        public ViewHolder(View itemView) {
            super(itemView);
            mobile = (TextView) itemView.findViewById(R.id.mobile);
        }
    }
}