package com.godlontonconsulting.entranze.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.helper.PrefManager;
import com.godlontonconsulting.entranze.pojos.AssignGateKeepers;
import com.godlontonconsulting.entranze.pojos.AssignGateKeepersResponseDTO;
import com.godlontonconsulting.entranze.pojos.CustomersDTO;
import com.godlontonconsulting.entranze.pojos.PurchaseResponse;
import com.godlontonconsulting.entranze.service.MyApiService;
import com.godlontonconsulting.entranze.service.ServiceLocator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsNotiActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextView txtStart, txtEnd, txtPrice, txtDetails, txtVenue, txtTitle,txtAvailable,txtFollowers,txtFollow,txtHost;

    Button btnPurchase, btnGateKeeperscan,btnInvite;
    ImageView eventImage;
    PrefManager pref;
    LinearLayout org;
    String isGatekeeper;
    SweetAlertDialog pDialog;
    boolean isFree=false;
    String msidn;
    public static boolean followAdded=false;
    String host;
    private CircleImageView imgProfile;
    boolean selectedPay=false;
    //
    /**
     * Handler os handler
     */
    Handler mHandler = new Handler();
    //
    WebView browser;
    //
    /**
     * Order Id
     * To Request for Updating Payment Status if Payment Successfully Done
     */
    int mId;
    private String mMerchantKey = "12209554";//For merchant and salt key you need to contact payu money tech support otherwise you get error
    private String mBaseURL = "https://www.payfast.co.za/eng/process";
    private String mMerchantId ="ff8ifzykkxqlr"; // This will create below randomly
    private double mAmount; // From Previous Activity
    private String mEmail = "";
    private String mSuccessUrl = "http://entranze-app.com/payments/success.html";
    private String mCancelledUrl = "http://entranze-app.com/payments/cancelled.html";

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled", "JavascriptInterface"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnPurchase = (Button) findViewById(R.id.btnPurchase);
        btnGateKeeperscan = (Button) findViewById(R.id.btnGatekeeperscan);
        btnInvite = (Button) findViewById(R.id.btnInvite);
        txtStart = (TextView) findViewById(R.id.starts);
        txtEnd = (TextView) findViewById(R.id.ends);
        txtPrice = (TextView) findViewById(R.id.price);
        txtTitle = (TextView) findViewById(R.id.event_title);
        txtDetails = (TextView) findViewById(R.id.description);
        txtAvailable = (TextView) findViewById(R.id.available);
        txtFollowers =(TextView) findViewById(R.id.followers);
        txtVenue = (TextView) findViewById(R.id.locationvenue);
        imgProfile= (CircleImageView) findViewById(R.id.imgProfile);
        imgProfile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        final PrefManager pref = new PrefManager(this);
        final String tokenRefresh = pref.getToken();
        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        msidn=getIntent().getStringExtra("EVENT_MOBILE");
        String imageUriImg = "https://accounts.bluespine.co.za/api/customers/"+msidn.toString()+"/avatar.file";
        ServiceLocator.INSTANCE.getPicasso(this).load(imageUriImg).fit().centerCrop().placeholder(R.drawable.ic_no_profile_pic).into(imgProfile);

        org = (LinearLayout) findViewById(R.id.organizers);
        org.setVisibility(View.INVISIBLE);
        txtHost = (TextView) findViewById(R.id.host);
        Activity activity = this; // If you're in a fragment you must get the containing Activity!
        int requestCode = 1234;
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtTitle.setText(getIntent().getStringExtra("EVENT_TITLE"));
        txtAvailable.setText("Available seats: "+getIntent().getStringExtra("EVENT_AVAILABLE"));
        txtFollowers.setText("Followers: "+getIntent().getStringExtra("EVENT_FOLLOW"));
        browser = (WebView)findViewById(R.id.webBrowser);

        String date = getIntent().getStringExtra("EVENT_START");
        String input = parseDateToddMMyyyy(date);
        String date2 = getIntent().getStringExtra("EVENT_END");
        String input2 = parseDateToddMMyyyy(date2);
        txtStart.setText(input);
        txtEnd.setText(input2);

        isGatekeeper = getIntent().getStringExtra("EVENT_ISGATEKEEPER");

        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        Call<CustomersDTO> call = ServiceLocator.INSTANCE.getMyApiService().getFollower(Long.parseLong(msidn));
        call.enqueue(new Callback<CustomersDTO>() {

            @Override
            public void onResponse(Call<CustomersDTO> call, Response<CustomersDTO> response) {
                if (response.body() != null) {
                    if (response.body().getSuccess()) {
                        txtHost.setText(response.body().getData().getFirstName());
                        host=response.body().getData().getFirstName();
                        org.setVisibility(View.VISIBLE);
                        //hideProgress();
                    } else {
//                        hideProgress();
//                        showError();
                        String error = "Error";
                    }
                } else {
//                    hideProgress();
//                    showError();
                    String error = "Error";
                }
            }

            @Override
            public void onFailure(Call<CustomersDTO> call, Throwable t) {
//                hideProgress();
//                showError();
                String error = "Error";
            }
        });

        if (getIntent().getStringExtra("EVENT_COST").equalsIgnoreCase("0")) {
            txtPrice.setText("FREE");
            isFree=true;
        } else {
            //txtPrice.setText(getIntent().getStringExtra("EVENT_CURRENCY")+" " + getIntent().getStringExtra("EVENT_COST"));
            txtPrice.setText("R " + getIntent().getStringExtra("EVENT_COST")+" + R15 Admin fee.");
            isFree=false;
        }

        if (getIntent().getStringExtra("EVENT_PRIVATE").equalsIgnoreCase("true")) {
            btnInvite.setVisibility(View.VISIBLE);
        } else {
            btnInvite.setVisibility(View.GONE);
        }
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteFriend();
            }
        });

        txtDetails.setText(getIntent().getStringExtra("EVENT_DETAILS"));
        String eventVenue=getIntent().getStringExtra("EVENT_VENUE");
        txtVenue.setText(eventVenue);
        btnGateKeeperscan.setVisibility(View.GONE);
        eventImage = (ImageView) findViewById(R.id.eventimage);
        eventImage.setScaleType(ImageView.ScaleType.FIT_XY);


        ServiceLocator.INSTANCE.refreshToken(tokenRefresh);
        String imageUri = "https://entranze.bluespine.co.za/api/entranzes/"+getIntent().getStringExtra("EVENT_ID")+"/avatar.file";
        ServiceLocator.INSTANCE.getPicasso(activity).load(imageUri).fit().centerCrop().into(eventImage);

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFree) {
                    EnterEmail();
                }else {
                    makePaymentCreateEventTicket();
                }
            }
        });
        btnGateKeeperscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailsNotiActivity.this, GateKeeperActivity.class);
                intent.putExtra("EVENT_ID", getIntent().getStringExtra("EVENT_ID"));
                startActivity(intent);
            }
        });
     org.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailsNotiActivity.this, FollowersEventsActivity.class);
                intent.putExtra("FOLLOWERS_NAME",host);
                intent.putExtra("EVENT_MOBILE", msidn);
                intent.putExtra("EVENT_ISFOLLOW", getIntent().getStringExtra("EVENT_ISFOLLOW"));
                intent.putExtra("FOLLOWING",getIntent().getStringExtra("EVENT_FOLLOW"));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
            }
        });
        checkGatekeeper();
    }

    private void PayOptions(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.payinputdialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final RadioButton radioButton2 = (RadioButton) promptsView.findViewById(R.id.radioButton2);
        final RadioButton radioButton3 = (RadioButton) promptsView.findViewById(R.id.radioButton3);
        final RadioButton radioButton4 = (RadioButton) promptsView.findViewById(R.id.radioButton4);
        //CardView androidpay = (CardView) findViewById(R.id.apay);
        final CardView pp = (CardView) promptsView.findViewById(R.id.ppal);
        final CardView ccard = (CardView) promptsView.findViewById(R.id.ccard);
        final CardView cash = (CardView) promptsView.findViewById(R.id.cash);
        //
        //
        // .setContentText("You are about to make a purchase of $" +getIntent().getStringExtra("EVENT_COST")+ " with a $2 admin fee.")
        //
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton2.setChecked(true);
                // radioButton1.setChecked(false);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
                selectedPay=true;
            }
        });

        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton3.setChecked(true);
                radioButton2.setChecked(false);
                //radioButton1.setChecked(false);
                radioButton4.setChecked(false);
                selectedPay=true;
            }
        });

        radioButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton4.setChecked(true);
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                selectedPay=true;
                // radioButton1.setChecked(false);
            }
        });
        //
        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton2.setChecked(true);
                // radioButton1.setChecked(false);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
                selectedPay=true;
            }
        });

        ccard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton3.setChecked(true);
                radioButton2.setChecked(false);
                //radioButton1.setChecked(false);
                radioButton4.setChecked(false);
                selectedPay=true;
            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton4.setChecked(true);
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                selectedPay=true;
                // radioButton1.setChecked(false);
            }
        });
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (selectedPay) {
                                    selectedPay=false;
                                    if(radioButton2.isChecked()){
                                        //PurchasePayPalResponse();
                                    }else if (radioButton3.isChecked()){
                                        EnterEmail();
                                    }else if (radioButton4.isChecked()){
                                        CashOptions();
                                    }
                                } else {
                                    new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Please make a payment selection.")
                                            .show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        //

    }
    //
    private void CashOptions(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.cashinputdialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final RadioButton radioButton2 = (RadioButton) promptsView.findViewById(R.id.radioButton2);
        final RadioButton radioButton3 = (RadioButton) promptsView.findViewById(R.id.radioButton3);
        final RadioButton radioButton4 = (RadioButton) promptsView.findViewById(R.id.radioButton4);
        //CardView androidpay = (CardView) findViewById(R.id.apay);
        final CardView pp = (CardView) promptsView.findViewById(R.id.ppal);
        final CardView ccard = (CardView) promptsView.findViewById(R.id.ccard);
        final CardView cash = (CardView) promptsView.findViewById(R.id.cash);
        //
        //
        // .setContentText("You are about to make a purchase of $" +getIntent().getStringExtra("EVENT_COST")+ " with a $2 admin fee.")
        //
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton2.setChecked(true);
                // radioButton1.setChecked(false);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
                selectedPay=true;
            }
        });

        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton3.setChecked(true);
                radioButton2.setChecked(false);
                //radioButton1.setChecked(false);
                radioButton4.setChecked(false);
                selectedPay=true;
            }
        });

        radioButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton4.setChecked(true);
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                selectedPay=true;
                // radioButton1.setChecked(false);
            }
        });
        //
        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton2.setChecked(true);
                // radioButton1.setChecked(false);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
                selectedPay=true;
            }
        });

        ccard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton3.setChecked(true);
                radioButton2.setChecked(false);
                //radioButton1.setChecked(false);
                radioButton4.setChecked(false);
                selectedPay=true;
            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton4.setChecked(true);
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                selectedPay=true;
                // radioButton1.setChecked(false);
            }
        });
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (selectedPay) {

                                    selectedPay=false;

                                    if(radioButton2.isChecked()){
                                        //PurchasePayPalResponse();
                                    }else if (radioButton3.isChecked()){
                                        //loadCreditCardPayment();
                                    }else if (radioButton3.isChecked()){

                                    }

                                } else {
                                    new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Please make a cash payment selection.")
                                            .show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    //
    private void loadCreditCardPayment(){
        /**
         * Creating Transaction Id
         */
        Random rand = new Random();
        mAmount = new BigDecimal(mAmount).setScale(0, RoundingMode.UP).intValue();

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Error accessing payment page.")
                        .setContentText("Please try again.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                browser.setVisibility(View.GONE);
                            }
                        })
                        .show();
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (url.equals(mSuccessUrl)) {
                    makePaymentCreateEventTicket();
                } else if (url.equals(mCancelledUrl)) {
                    browser.setVisibility(View.GONE);
                    new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Cancelled")
                            .setContentText("Payment cancelled.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
                /**
                 * wait 10 seconds to dismiss payu money processing dialog in my case
                 */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //progressBarVisibilityPayuChrome(View.GONE);
                    }
                }, 10000);

                super.onPageFinished(view, url);
            }
        });
        browser.setVisibility(View.VISIBLE);
        browser.getSettings().setBuiltInZoomControls(true);
       // browser.getSettings().setCacheMode(2);
        browser.getSettings().setDomStorageEnabled(true);
        browser.clearHistory();
        browser.clearCache(true);
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        browser.getSettings().setSupportZoom(true);
        browser.getSettings().setUseWideViewPort(false);
        browser.getSettings().setLoadWithOverviewMode(false);
        browser.addJavascriptInterface(new PayFastJavaScriptInterface(EventDetailsNotiActivity.this), "PayFast");
        //
        pref = new PrefManager(EventDetailsNotiActivity.this.getApplicationContext());
        HashMap<String, String> profile = pref.getUserDetails();
        String cost=getIntent().getStringExtra("EVENT_COST");
        double result = Integer.parseInt(cost)+15;
        /**
         * Mapping Compulsory Key Value Pairs
         */
        Map<String, String> mapParams = new HashMap<>();
        mapParams.put("merchant_id", mMerchantKey);
        mapParams.put("merchant_key", mMerchantId);
        mapParams.put("item_name",getIntent().getStringExtra("EVENT_TITLE"));
        mapParams.put("amount", String.valueOf(result));
        mapParams.put("m_payment_id", "123");
        mapParams.put("item_description", "Entranze Event Ticket");
        mapParams.put("name_first", profile.get("name"));
        mapParams.put("email_confirmation","1");
        mapParams.put("confirmation_address","ryan@rklambo.com");
        mapParams.put("email_address", mEmail);
        mapParams.put("return_url", mSuccessUrl);
        mapParams.put("cancel_url", mCancelledUrl);
        webViewClientPost(browser, mBaseURL, mapParams.entrySet());
    }
    //
    /**
     * Posting Data on PayFast Site with Form
     *
     * @param webView
     * @param url
     * @param postData
     */
    public void webViewClientPost(WebView webView, String url, Collection<Map.Entry<String, String>> postData) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head>");
        sb.append("<body onload='form1.submit()'>");
        sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));
        for (Map.Entry<String, String> item : postData) {
            sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
        }
        sb.append("</form></body></html>");
        Log.d("TAG", "webViewClientPost called: " + sb.toString());
        webView.loadData(sb.toString(), "text/html", "utf-8");
    }
    //
    public class PayFastJavaScriptInterface {
        Context mContext;

        PayFastJavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void success(long id, final String paymentId) {

            mHandler.post(new Runnable() {

                public void run() {
                    mHandler = null;
                    makePaymentCreateEventTicket();
                }
            });
        }
    }
    //
    private void EnterEmail(){

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.emailinputdialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (isValidEmail(userInput.getText().toString().trim())) {
                                    mEmail=userInput.getText().toString().trim();
                                    loadCreditCardPayment();
                                } else {
                                    new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Please enter valid email address.")
                                            .show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    //
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //
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

    //
    private void checkGatekeeper() {
        if (isGatekeeper != null) {
            if (isGatekeeper.equalsIgnoreCase("true")) {
                btnGateKeeperscan.setVisibility(View.VISIBLE);
            } else {
                // Toast.makeText(getApplicationContext(), "No assigned gatekeepers", Toast.LENGTH_LONG).show();
            }
        } else {

        }
    }
    //
    private void inviteFriend(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.custominputdialogtransfer, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (isValidPhoneNumber(userInput.getText().toString().trim())) {
                                    invite("27"+mobileNumberFormat(userInput.getText().toString().trim()));
                                } else {
                                    new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Please enter valid mobile number")
                                            .show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    //
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{9,10}$";
        return mobile.matches(regEx);
    }
    //
    private String mobileNumberFormat(String number){
        number = number.startsWith("0") ? number.substring(1) : number;
        return number;
    }
    //
    private void invite (String mobile){
        PrefManager pref = new PrefManager(getApplicationContext());

        AssignGateKeepers invitee = new AssignGateKeepers(Long.parseLong(mobile));

        List<AssignGateKeepers> invites = new ArrayList<AssignGateKeepers>();
        invites.add(invitee);

        String token = pref.getToken();
        String authorization = "Bearer "+token;
        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<AssignGateKeepersResponseDTO> call = service.inviteFriend(getIntent().getStringExtra("EVENT_ID"),authorization,invites);
        call.enqueue(new Callback<AssignGateKeepersResponseDTO>() {
            @Override
            public void onResponse(Call<AssignGateKeepersResponseDTO> call, Response<AssignGateKeepersResponseDTO> response) {
                if (response.body().getSuccess()){
                    new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Awesome!")
                            .setContentText("Invitation successfully sent.")
                            .show();
                } else {
                    new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error")
                            .setContentText(response.body().getMessage())
                            .show();
                }
            }
            @Override
            public void onFailure(Call<AssignGateKeepersResponseDTO> call, Throwable t) {
//                new SweetAlertDialog(TicketDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Error")
//                        .setContentText("Cannot access or connect to server at this time, please try again later.")
//                        .show();
                //
                new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Awesome!")
                        .setContentText("Invitation successfully sent.")
                        .show();
            }
        });
    }
    //
    public void showProgress() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#ff6b6b"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void hideProgress() {
        pDialog.hide();
    }
    //



    private void makePaymentCreateEventTicket() {
        PrefManager pref = new PrefManager(getApplicationContext());

        String id = getIntent().getStringExtra("EVENT_ID");
        String token = pref.getToken();
        String authorization = "Bearer " + token;

        MyApiService service = MyApiService.retrofit.create(MyApiService.class);
        Call<PurchaseResponse> call = service.makePayment(id, authorization);
        call.enqueue(new Callback<PurchaseResponse>() {

            @Override
            public void onResponse(Call<PurchaseResponse> call, Response<PurchaseResponse> response) {

                if (response.body() != null) {
                    if (response.body().getSuccess()) {

                        try {
                            new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Thank you for your purchase!")
                                    .setContentText("Check your tickets tab for your purchase.")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            finish();
                                        }
                                    })
                                    .show();
                        } catch (Exception e) {
                            new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Thank you for your purchase!")
                                    .setContentText("Check your tickets tab for your purchase.")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }

                    } else {
                        new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText("Something went wrong when connecting. Please try again.")
                                .show();
                    }
                } else {
                    new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("Something went wrong when connecting. Please try again.")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<PurchaseResponse> call, Throwable t) {
                new SweetAlertDialog(EventDetailsNotiActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops")
                        .setContentText("Something went wrong when connecting. Please try again.")
                        .show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                String lat = getIntent().getStringExtra("EVENT_LAT");
                String lon = getIntent().getStringExtra("EVENT_LONG");
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                break;
            case R.id.action_share:
                String startTime =  getIntent().getStringExtra("EVENT_START");
                String endTime = getIntent().getStringExtra("EVENT_END");
                StringBuilder shareText = new StringBuilder();
                shareText.append(String.format("Check this event out: "+getIntent().getStringExtra("EVENT_TITLE")));
                shareText.append("\nDescription: ").append(getIntent().getStringExtra("EVENT_DESCRIPTION"));
                shareText.append("\nStarts: ").append(startTime);
                shareText.append("\nEnds: ").append(endTime);
                shareText.append("\nLocation: ").append(getIntent().getStringExtra("EVENT_LOCATION"));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,"Share via"));
                return true;

            case R.id.action_add_to_calendar:
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, getIntent().getStringExtra("EVENT_TITLE"));
                intent.putExtra(CalendarContract.Events.DESCRIPTION, getIntent().getStringExtra("EVENT_DETAILS"));
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, getIntent().getStringExtra("EVENT_VENUE"));
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getIntent().getStringExtra("EVENT_START"));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,getIntent().getStringExtra("EVENT_END"));
                startActivity(intent);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
