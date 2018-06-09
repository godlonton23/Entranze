package com.godlontonconsulting.entranze.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.godlontonconsulting.entranze.R;

public class ContactUsFragment extends Fragment {

    public static final String CONTEXT = "[HomeFragment]";
    ContactUsFragmentListerner frag_listener;
    RelativeLayout my_layout;

    public static Fragment getInstance() {
        Fragment f = new ContactUsFragment();
        f.setRetainInstance(true);
        return f;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        my_layout = (RelativeLayout) inflater.inflate(R.layout.contact_us_fragment, container, false);
        LinearLayout callUsLink = (LinearLayout) my_layout.findViewById(R.id.call_layout);

        callUsLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // start call
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + "0861555263"));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException e) {
                    Log.e("failed to call support", "Call failed", e);
                }
            }
        });

        return my_layout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Ensure that the activity implemented the fragment's listener interface
        try {
            frag_listener = (ContactUsFragmentListerner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ContactUsFragmentListerner");
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }


    public interface ContactUsFragmentListerner {
        public void ContactUsFragmentListernerClicked();
    }

}
