package com.godlontonconsulting.entranze.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aviadmini.quickimagepick.PickCallback;
import com.aviadmini.quickimagepick.PickSource;
import com.aviadmini.quickimagepick.QiPick;
import com.aviadmini.quickimagepick.UriUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.app.Config;
import com.godlontonconsulting.entranze.fragment.ContactsFragment;
import com.godlontonconsulting.entranze.fragment.FollowersFragment;
import com.godlontonconsulting.entranze.fragment.LikedFragment;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.GetFollowers;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.godlontonconsulting.entranze.utils.EntranzeApp;
import com.godlontonconsulting.entranze.utils.NoInternetDialog;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Energy on 2017/05/02.
 */

public class AccountActivity extends AppCompatActivity {
    private PrefManager pref;
    private TextView etName;
    private TextView etMobile;
    private TextView etFollow;
    private CircularImageView imgProfile;
    private File profile_pic;
    private boolean isImageset = false;
    private String updatedImage;
    private Toolbar toolbar;
    SweetAlertDialog pDialog;
    private static final String QIP_DIR_NAME = "EntranzeProfile";
    private static final String TAG = "AccountActivity" ;
    File file,compressedImageFile;
    Uri imageURI;
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    private FloatingActionButton floatingActionButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int followers=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the Above View
        setContentView(R.layout.activity_account);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        etName = (TextView) findViewById(R.id.namefrag);
        etMobile = (TextView) findViewById(R.id.mobilefrag);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });
        imgProfile= (CircularImageView) findViewById(R.id.imgProfile);
        //
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        LinearLayout linearLayout = (LinearLayout)tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.GRAY);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);
        //
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pref = new PrefManager(AccountActivity.this.getApplicationContext());
        HashMap<String, String> profile = pref.getUserDetails();
        etName.setText(profile.get("name"));
        etMobile.setText(profile.get("msisdn"));
        bindValues();
        //
        getFollowers();
        //
        //GetContactsIntoArrayList();
    }
    //
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new LikedFragment(), "Favs");
        adapter.addFrag(new FollowersFragment(), followers+System.getProperty("line.separator")+"Followers");
        adapter.addFrag(new ContactsFragment(), "Friends");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void bindValues() {
        SharedPreferences prefs = getSharedPreferences(Config.USER_PROFILE, 0);
        String restoredPic = prefs.getString("profilepic", "");
        imgProfile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //if (!loginData.getData().getImageProfile().isEmpty()) {
        if (!restoredPic.isEmpty()) {
            // progressBar.setVisibility(View.GONE);
            profile_pic = new File(restoredPic);
            Picasso.with(this).load(profile_pic).placeholder(R.drawable.ic_no_profile_pic).into(imgProfile);
            //Picasso.with(getActivity().getApplicationContext()).load(R.drawable.ic_events).placeholder(R.drawable.ic_no_profile_pic).into(imgProfile);
        } else {
            imgProfile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            final PrefManager pref = new PrefManager(this);
            final String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
            String imageUri = "https://accounts.bluespine.co.za/api/me/avatar.file";
            ServiceLocator.INSTANCE.getPicasso(this).load(imageUri).fit().centerCrop().placeholder(R.drawable.ic_no_profile_pic).into(imgProfile);
            //
        }
    }



    private void getFollowers() {
        if (EntranzeApp.hasNetworkConnection(this)){

            PrefManager pref = new PrefManager(this);
            String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

            Call<GetFollowers> call = ServiceLocator.INSTANCE.getMyApiService().getFollowers();
            call.enqueue(new Callback<GetFollowers>() {

                @Override
                public void onResponse(Call<GetFollowers> call, Response<GetFollowers> response) {
                    if (response.body() != null) {
                        if (response.body().getSuccess()) {
                            followers=response.body().getData().size();
                            setupViewPager(viewPager);
                           // etFollow.setText(Integer.toString(followers));
                        } else {
                           // etFollow.setText("");
                        }
                    }else {
                        //etFollow.setText("");
                    }
                }

                @Override
                public void onFailure(Call<GetFollowers> call, Throwable t) {
                   // etFollow.setText("");
                }
            });
        }
        else {
            NoInternetDialog dialog = new NoInternetDialog(this);
            dialog.show();
        }
    }



    private void selectImage() {
        QiPick.in(this).fromMultipleSources("Profile picture", PickSource.CAMERA, PickSource.GALLERY);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QiPick.handleActivityResult(getApplicationContext(), requestCode, resultCode, data, this.mCallback);
    }

    private final PickCallback mCallback = new PickCallback() {

        @Override
        public void onImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final Uri pImageUri) {
            // Do something with Uri, for example load image into an ImageView
            imageURI=pImageUri;
            Glide.with(getApplicationContext())
                    .load(pImageUri)
                    .apply(new RequestOptions().fitCenter())
                    .into(imgProfile);
            final String extension = UriUtils.getFileExtension(AccountActivity.this, pImageUri);
            Log.i(TAG, "Picked: " + pImageUri.toString() + "\nMIME type: " + UriUtils.getMimeType(AccountActivity.this,
                    pImageUri) + "\nFile extension: " + extension + "\nRequest type: " + pRequestType);
            //filePath=pImageUri.toString();
            try {

                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("hh-mm-ss");
                String formattedDate = df.format(c.getTime());

                final String ext = extension == null ? "" : "." + extension;
                final File outDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), QIP_DIR_NAME);
                Random rand = new Random();

                int  n = rand.nextInt(500000) + 1;

                file = new File(outDir, "entranze" + String.valueOf(n)+ ext);
                ///compressedImageFile = new Compressor(AccountActivity.this).compressToFile(file);
                outDir.mkdirs();
                UriUtils.saveContentToFile(AccountActivity.this, pImageUri, file);
                //
                updatedImage=file.getPath();
                //
                SharedPreferences settings = getSharedPreferences(Config.USER_PROFILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("profilepic",updatedImage);
                editor.commit();
                //
                uploadImageToServer();

            } catch (final IOException e) {
                Toast.makeText(AccountActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        public void onMultipleImagesPicked(int i, @NonNull List<Uri> list) {

        }

        @Override
        public void onError(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final String pErrorString) {
            Log.e(TAG, "Err: " + pErrorString);
        }

        @Override
        public void onCancel(@NonNull final PickSource pPickSource, final int pRequestType) {
            Log.d(TAG, "Cancel: " + pPickSource.name());
        }

    };

    //this method will upload the file
    private void uploadImageToServer() {
        // if there is a file to upload
        if (imageURI != null) {
            //displaying a progress dialog while upload is going on
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
            pDialog.setTitleText("Uploading your profile image...");
            pDialog.setCancelable(false);
            pDialog.show();
            //
            try {
                compressedImageFile = new Compressor(AccountActivity.this).compressToFile(file);

            } catch (final IOException e) {

            }
            //
            String token = pref.getToken();
            final String authorization = "Bearer "+token;
            //
            Ion.with(this)
                    .load("https://entranze.bluespine.co.za/api/me/avatar.file")
                    .uploadProgressHandler(new ProgressCallback() {
                        @Override
                        public void onProgress(long uploaded, long total) {
                            pDialog.setTitleText("Uploaded " + ((int) uploaded) + "%...");
                        }
                    })
                    .setTimeout(60 * 60 * 1000)
                    .setHeader("Authorization", authorization)
                    .setMultipartParameter("Authorization", authorization)
                    .setMultipartContentType("multipart/form-data")
                    .setMultipartFile("image", "image/jpeg", compressedImageFile)
                    .setMultipartFile("upload", "image/jpeg",compressedImageFile)
                    .asJsonObject()
                    // run a callback on completion
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            pDialog.hide();
                            if (e != null) {
                                new SweetAlertDialog(AccountActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Error")
                                        .setContentText("Error uploading your profile picture to the server. Please try again.")
                                        .show();
                                return;
                            }
                            new SweetAlertDialog(AccountActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Awesome!")
                                    .setContentText("New profile picture added.")
                                    .show();
                        }
                    });
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //bindValues();
    }


    @Override
    public void finish() {
        super.finish();
    }
}
