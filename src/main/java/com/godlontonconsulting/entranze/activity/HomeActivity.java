package com.godlontonconsulting.entranze.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.app.Config;
import com.godlontonconsulting.entranze.fragment.FollowingFragment;
import com.godlontonconsulting.entranze.fragment.HomeFragment;
import com.godlontonconsulting.entranze.fragment.TicketsFragment;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.DeviceUser;
import com.godlontonconsulting.entranze.pojos.FollowDTO;
import com.godlontonconsulting.entranze.pojos.Me;
import com.godlontonconsulting.entranze.pojos.NewRefreshAccessToken;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.service.ServiceLocator;
import com.godlontonconsulting.entranze.utils.EntranzeApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rom4ek.arcnavigationview.ArcNavigationView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CircularImageView profilePic;
    private CircularImageView profilePicMenu;
    private TextView nameMenu;
    private File profile_pic;
    private boolean accountProfile =false;
    private FragmentManager fragmentManager;
    private MaterialSearchView searchView;
    private String searchText="";
    private DrawerLayout drawer;
    private ArcNavigationView naview;
    private PrefManager pref;
    private SweetAlertDialog pDialog;

    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String token = intent.getStringExtra("token");
            if(token != null)
            {
                sendRegistrationToServer(token);
            }
        }
    };
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //MultiDex.install(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        naview=(ArcNavigationView) findViewById(R.id.nav_view_right);
        registerUser();
        // Register and initialization with Uber //
        SessionConfiguration config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("WrsRvMI47ApYMnVcX08x_Q90TjjUqYGi")
                // required for enhanced button features
                .setServerToken("owV8QN6RcENGfX20oE1RuJEqxpMraneRBw2nJiqZ")
                //
                .setRedirectUri("https://login.uber.com/oauth/v2/authorize?client_id=WrsRvMI47ApYMnVcX08x_Q90TjjUqYGi&response_type=code")
                // required scope for Ride Request Widget features
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                // optional: set Sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.PRODUCTION)
                .build();

        UberSdk.initialize(config);

        fragmentManager = getSupportFragmentManager();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View headerView = naview.inflateHeaderView(R.layout.nav_header_main);

        profilePic= (CircularImageView) findViewById(R.id.profilepic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                accountProfile=true;
                //startActivityForResult(intent, 1);
                startActivity(intent);
            }
        });
        profilePicMenu= (CircularImageView) headerView.findViewById(R.id.profilepicmenu);
        profilePicMenu.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        pref = new PrefManager(getApplicationContext());
        HashMap<String, String> profile = pref.getUserDetails();

        nameMenu=(TextView)headerView.findViewById(R.id.txtName);
        nameMenu.setText(profile.get("name"));

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setHint("Search an event..");
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStuff(query);
                searchText=query;
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //searchStuff(newText);
                return false;
            }
        });
                searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                searchStuff(searchText);
            }

            @Override
            public void onSearchViewClosed() {
                searchStuff(searchText);
                searchText="";
            }
        });
//        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) findViewById(R.id.toolbar_title);
        Typeface khandBold = Typeface.createFromAsset(getApplication().getAssets(), "fonts/Bariol_Light.otf");
        mTitle.setTypeface(khandBold);

//        setSupportActionBar(toolbarTop);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mTitle.setText("Entranze");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);

        NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
        navigationViewRight.setNavigationItemSelectedListener(this);

        getRefresherToken();

        LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver, new IntentFilter("tokenReceiver"));
        sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());

        bindValues();
    }
//
    private void sendRegistrationToServer(String token) {

        DeviceUser user=new DeviceUser();
        user.setFcmToken(token);
        user.setName(android.os.Build.MODEL);
        int osVersion = android.os.Build.VERSION.SDK_INT;
        user.setOs(String.valueOf("Android "+osVersion));

        if(checkAndRequestPermissions()) {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            user.setImei(telephonyManager.getDeviceId());
        }else {
            user.setImei("No permissions set");
        }

        PrefManager pref = new PrefManager(this);
        String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);

        Call<FollowDTO> call = ServiceLocator.INSTANCE.getMyApiService().sendDevice(user);
        call.enqueue(new Callback<FollowDTO>() {

            @Override
            public void onResponse(Call<FollowDTO> call, Response<FollowDTO> response) {
                if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        String succcess="success";
                    } else {
                        String fail="fail";
                    }
                } else {
                    String fail="fail";
                }
            }

            @Override
            public void onFailure(Call<FollowDTO> call, Throwable t) {
                String fail="fail";
            }
        });
    }
    //
    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),0);
            return false;
        }
        return true;
    }

    private void getRefresherToken(){
        if (EntranzeApp.hasNetworkConnection(this)) {
            PrefManager pref = new PrefManager(this);
            String tokenRefresh = pref.getRefreshToken();
            showProgress();

            MyApiService service = MyApiService.retrofit.create(MyApiService.class);
            Call<NewRefreshAccessToken> call = service.getNewAccessToken("refresh_token", tokenRefresh);
            call.enqueue(new Callback<NewRefreshAccessToken>() {
                @Override
                public void onResponse(Call<NewRefreshAccessToken> call, Response<NewRefreshAccessToken> response) {
                    if (response.body() != null) {
                        hideProgress();
                        PrefManager pref = new PrefManager(HomeActivity.this);
                        pref.createAccessToken(response.body().getAccessToken());
                        pref.createRefreshToken(response.body().getRefreshToken());
                        setupViewPager(viewPager);
                    } else {
                        hideProgress();
                        new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Error!")
                                .setContentText("We're having difficulty connecting to the server. Check your connection or try again later.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        getRefresherToken();
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<NewRefreshAccessToken> call, Throwable t) {
                    hideProgress();
                    new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error!")
                            .setContentText("We're having difficulty connecting to the server. Check your connection or try again later.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    getRefresherToken();
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            });
        } else {
            hideProgress();
            showErrorNoInternet();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mainnew, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_right_menu) {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent intent1 = new Intent(HomeActivity.this, AccountActivity.class);
            accountProfile=true;
            startActivity(intent1);
        } else if (id == R.id.nav_add) {
            Intent intent3 = new Intent(HomeActivity.this, AddEventActivity.class);
            startActivityForResult(intent3, 10001);
        } else if (id == R.id.nav_manage) {
            Intent intent4 = new Intent(HomeActivity.this, ManageEventsActivity.class);
            startActivityForResult(intent4, 10001);
        }else if (id == R.id.nav_settings) {
            Intent intent4 = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent4);
        } else if (id == R.id.nav_logout) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Logout? Are you sure?")
                        .setCancelText("Cancel")
                        .setConfirmText("Logout")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                PrefManager pref;
                                pref = new PrefManager(HomeActivity.this);
                                if (pref.getLoginPin()==null) {
                                    new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("No Pin Set")
                                            .setContentText("Please set a personal pin first in settings in order to logout.")
                                            .show();
                                } else {
                                    pref.logOutReg();
                                    Intent intent5 = new Intent(HomeActivity.this,LogoutPinlockActivity.class);
                                    startActivity(intent5);
                                    finish();
                                }
                                sDialog.cancel();
                            }
                        })
                        .show();

        } else if (id == R.id.nav_share) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Get Entranze - the all-in=one events based app.");
            String sAux = "\nDownloade here\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.godlontonconsulting.entranze \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i,"Share via"));
        } else if (id == R.id.nav_send) {
            String to = "ryang@rklambo.com";
            String subject = "Entranze information and query";
            String message = "I'd like to find out more about Entranze. Please contact me.";
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
            //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
            //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
            email.putExtra(Intent.EXTRA_TEXT, message);
            //need this to prompts email client only
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }


    private void registerUser() {

        PrefManager pref = new PrefManager(getApplicationContext());
        String token = pref.getToken();
        String authorization = "Bearer "+token;

        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<Me> call = service.getMeAccount(authorization);
        call.enqueue(new Callback<Me>() {

            @Override
            public void onResponse(Call<Me> call, Response<Me> response) {

                if (response.body() !=null){
                    //hideProgress();
                    //Toast.makeText(getApplicationContext(), "Registered on our network.", Toast.LENGTH_SHORT).show();
                } else {
                    //hideProgress();
                   // Toast.makeText(getApplicationContext(), "Cannot access registration server invalid token.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Me> call, Throwable t) {
                Log.e("Home Activity", t.toString());
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), "EVENTS");
        adapter.addFrag(new FollowingFragment(), "FOLLOWING");
        adapter.addFrag(new TicketsFragment(), "TICKETS");
        viewPager.setAdapter(adapter);
    }

    private void showErrorNoInternet(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No internet connection.")
                .setContentText("Please check your internet connection and try again.")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        getRefresherToken();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void bindValues() {
        SharedPreferences prefs = getSharedPreferences(Config.USER_PROFILE, 0);
        String restoredPic = prefs.getString("profilepic", "");
        profilePicMenu.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        profilePic.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // if (!loginData.getData().getImageProfile().isEmpty()) {
        //
        // }
//        if (!restoredPic.isEmpty()) {
//            // progressBar.setVisibility(View.GONE);
//            profile_pic = new File(restoredPic);
//            Picasso.with(this).load(profile_pic).placeholder(R.drawable.ic_no_profile_pic).into(profilePic);
//            Picasso.with(this).load(profile_pic).placeholder(R.drawable.ic_no_profile_pic).into(profilePicMenu);
//
//        } else {
            profilePicMenu.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            profilePic.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            final PrefManager pref = new PrefManager(this);
            final String tokenRefresh = pref.getToken();
            ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
            String imageUri = "https://accounts.bluespine.co.za/api/me/avatar.file";
            ServiceLocator.INSTANCE.getPicasso(this).load(imageUri).fit().centerCrop().placeholder(R.drawable.ic_no_profile_pic).into(profilePic);
            ServiceLocator.INSTANCE.getPicasso(this).load(imageUri).resize(62, 62).centerCrop().placeholder(R.drawable.ic_no_profile_pic).into(profilePicMenu);

            Picasso.with(this).load(imageUri).into(getTarget(imageUri));
       // }
    }

    private static Target getTarget(final String url){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + url);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
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

    public void searchStuff(String query) {

        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        // based on the current position you can then cast the page to the correct
        // class and call the method:
        if (viewPager.getCurrentItem() == 0 && page != null) {
            ((HomeFragment)page).searchList(query);
        } else if (viewPager.getCurrentItem() == 1 && page != null) {
           // ((LikedFragment)page).searchList(query);
        }else if (viewPager.getCurrentItem() == 2 && page != null) {
            //((TicketsFragment)page).searchList(query);
        }
    }


    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (accountProfile){
            accountProfile=false;
            bindValues();
        }
    }
    //
    public void showProgress() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
        pDialog.setTitleText("Loading data...");
        pDialog.setCancelable(false);
        pDialog.show();
    }


    public void hideProgress() {
        pDialog.hide();
    }
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    //
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
    //
    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
