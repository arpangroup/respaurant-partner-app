package com.example.mainactivity.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;

public class OtpReceiver extends BroadcastReceiver {

    private static EditText et1, et2, et3, et4, et5;

    public void setEditTextOtp(EditText et1, EditText et2, EditText et3, EditText et4, EditText et5){
        OtpReceiver.et1 = et1;
        OtpReceiver.et2 = et2;
        OtpReceiver.et3 = et3;
        OtpReceiver.et4 = et4;
        OtpReceiver.et5 = et5;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for(SmsMessage smsMessage :  smsMessages){
            try{
                String messageBody = smsMessage.getMessageBody();
                int lastIndex = messageBody.lastIndexOf(" ");
                String otp = messageBody.substring(lastIndex+1);

                String char1 = String.valueOf(otp.charAt(0)) ;
                String char2 = String.valueOf(otp.charAt(1)) ;
                String char3 = String.valueOf(otp.charAt(2)) ;
                String char4 = String.valueOf(otp.charAt(3)) ;
                String char5 = String.valueOf(otp.charAt(4));


                et1.setText(char1);
                et2.setText(char2);
                et3.setText(char3);
                et4.setText(char4);
                et5.setText(char5);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
