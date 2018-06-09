package com.godlontonconsulting.entranze.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.CustomersDTO;
import com.godlontonconsulting.entranze.pojos.User;
import com.godlontonconsulting.entranze.service.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FollowersHorizontalAdapter extends RecyclerView.Adapter<FollowersHorizontalAdapter.ViewHolder>  {

    private List<User> mItems;
    private Activity activity;

    private List<User> mFilteredList;

    public FollowersHorizontalAdapter(Activity activity) {
        super();
        this.activity = activity;
        mItems = new ArrayList<User>();
    }

    public void addData( List<User> event) {
        mItems.addAll(event);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontalfavs_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final User events = mItems.get(i);

        final String mobile=Long.toString(events.getMsisdn());


        PrefManager pref = new PrefManager(activity);
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

        Call<CustomersDTO> call = ServiceLocator.INSTANCE.getMyApiService().getFollower(events.getMsisdn());
        call.enqueue(new Callback<CustomersDTO>() {

            @Override
            public void onResponse(Call<CustomersDTO> call, Response<CustomersDTO> response) {
                if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        viewHolder.follower.setText(response.body().getData().getFirstName());
                        //hideProgress();
                    } else {
//                        hideProgress();
//                        showError();
                        String error="Error";
                    }
                }else {
//                    hideProgress();
//                    showError();
                    String error="Error";
                }
            }

            @Override
            public void onFailure(Call<CustomersDTO> call, Throwable t) {
//                hideProgress();
//                showError();
                String error="Error";
            }
        });


        //
        //final PrefManager pref = new PrefManager(activity);
        //final String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        String imageUri = "https://accounts.bluespine.co.za/api/customers/"+mobile+"/avatar.file";
        ServiceLocator.INSTANCE.getPicasso(activity).load(imageUri).resize(60, 60).centerCrop().placeholder(R.drawable.ic_no_profile_pic).into(viewHolder.profPic);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView follower;
        public CircleImageView profPic;

        public ViewHolder(View itemView) {
            super(itemView);
            follower = (TextView) itemView.findViewById(R.id.folluser);
            profPic = (CircleImageView ) itemView.findViewById(R.id.imgProfile);
        }
    }

}