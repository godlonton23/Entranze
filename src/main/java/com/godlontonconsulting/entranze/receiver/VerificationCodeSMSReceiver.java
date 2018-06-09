package com.godlontonconsulting.entranze.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
//import za.co.bluespine.entranze.installation.NameCaptureActivity;

public class VerificationCodeSMSReceiver extends BroadcastReceiver {
    private static final String SMS_FLAG = "is your Bluespine verification code.";

    public VerificationCodeSMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Log.i("cs.fsu", "smsReceiver : Reading Bundle");

            Object[] pdus = (Object[])bundle.get("pdus");
            SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[0]);

            if(sms.getMessageBody().contains(SMS_FLAG)){
                String notification = "Bluespine verification code SMS received. Processing ...";
                Log.d(VerificationCodeSMSReceiver.class.getName(), notification);
                Toast.makeText(context, notification, Toast.LENGTH_LONG).show();

                Log.e(VerificationCodeSMSReceiver.class.getName(), "Broadcast :"+notification);

                Bundle extras = intent.getExtras();
                Intent i = new Intent("bluespineVerificationCodeReceivedBroadCast");
                // Data you need to pass to activity
                i.putExtra("verificationCode", extractVerificationCode(sms.getMessageBody()));

                context.sendBroadcast(i);

            }
        }
    }

    private String extractVerificationCode(String messageBody) {
            //Extract the verification code.
            int start = messageBody.indexOf(":B-") + 3;
            int length = 7;
            return messageBody.substring(start, Math.min(start + length, messageBody.length()));
    }
}
