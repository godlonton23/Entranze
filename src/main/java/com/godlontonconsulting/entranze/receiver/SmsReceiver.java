package com.godlontonconsulting.entranze.receiver;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.godlontonconsulting.entranze.R;
import com.godlontonconsulting.entranze.activity.RegActivity;
import com.godlontonconsulting.entranze.service.HttpService;


public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_FLAG = "is your Bluespine verification code.";

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Log.i("cs.fsu", "smsReceiver : Reading Bundle");

            Object[] pdus = (Object[])bundle.get("pdus");
            SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[0]);

            if(sms.getMessageBody().contains(SMS_FLAG)){
                String notification = "Entranze verification code SMS received. Processing ...";
                Log.d(SmsReceiver.class.getName(), notification);
                //Toast.makeText(context, notification, Toast.LENGTH_LONG).show();

//                final ProgressDialog progressDialog = new ProgressDialog(context,
//                        R.style.AppTheme_Dark_Dialog);
//                progressDialog.setIndeterminate(true);
//                progressDialog.setMessage("Entranze verification code SMS received. Processing ...");
//                progressDialog.show();

                Log.e(SmsReceiver.class.getName(), "Broadcast :"+notification);

                Intent httpIntent = new Intent(context, HttpService.class);
                httpIntent.putExtra("otp", extractVerificationCode(sms.getMessageBody()));
                context.startService(httpIntent);

            }
        }
    }

    private String extractVerificationCode(String sms) {
        // Extract the verification code.
        int start = sms.indexOf(":B-") + 3;
        int length = 7;
        return sms.substring(start, Math.min(start + length, sms.length()));
    }
}
