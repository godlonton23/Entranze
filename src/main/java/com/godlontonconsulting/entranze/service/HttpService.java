package com.godlontonconsulting.entranze.service;

import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.godlontonconsulting.entranze.activity.HomeActivity;
import com.godlontonconsulting.entranze.activity.RegActivity;
import com.godlontonconsulting.entranze.app.Config;
import com.godlontonconsulting.entranze.helper.PrefManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class HttpService extends IntentService {

    private static String TAG = HttpService.class.getSimpleName();


    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            verifyOtp(otp);
        }
    }

    /**
     * Posting the OTP to server and activating the user
     *
     * @param otp otp received in the SMS
     */
    private void verifyOtp(final String otp) {
        new PostData().execute(otp);
    }

    public class PostData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                //
                HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

                DefaultHttpClient client = new DefaultHttpClient();
//
                HttpClient httpclient = new DefaultHttpClient();
                SchemeRegistry registry = new SchemeRegistry();
                SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
                socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                registry.register(new Scheme("https", socketFactory, 443));
                SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
                DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

// Set verifier
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

                HttpPost httppost = new HttpPost(Config.URL_ACTIVATE);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                PrefManager pref1 = new PrefManager(getApplicationContext());
                String mobile=pref1.getMobile();
                nameValuePairs.add(new BasicNameValuePair("username", mobile));
                nameValuePairs.add(new BasicNameValuePair("password", params[0]));
                nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                    public boolean verify(String string,SSLSession ssls) {
                        return true;
                    }
                });
                //
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    String responseBody = EntityUtils.toString(response.getEntity());
                    //
                    try{
                        JSONObject responseObj = new JSONObject(responseBody);
                        // parsing the user profile information
                        String token = responseObj.getString("access_token");
                        String tokenRefresh = responseObj.getString("refresh_token");
////                        String surname = profileObj.getString("lastName");
////                        String mobile = profileObj.getString("msisdn");
                        PrefManager pref = new PrefManager(getApplicationContext());
//                        pref.createLogin(name, surname, SmsActivity.mobile);
                        pref.createToken(token,tokenRefresh);
                        pref.logIn();
                        //
                        Intent intent = new Intent(HttpService.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //RegActivity.finishRegistration();
                        return responseBody;

                    } catch(Exception e){
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                   e.printStackTrace();
                   ErrorResponse();
                }
                //reset the message text field
            } catch (IOException e) {
                ErrorResponse();
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public void ErrorResponse(){
        //Toast.makeText(getBaseContext(), "Server is down or no connection available please try again.", Toast.LENGTH_LONG).show();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setMessage("Server is down or no connection available please try again. Please retry registering again.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        RegActivity.btnRequestSms.setEnabled(true);
                        //Toast.makeText(RegActivity.this,"Please retry registering again.",Toast.LENGTH_LONG).show();
                        //finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        //progressBar.setVisibility(View.INVISIBLE);
    }
}
