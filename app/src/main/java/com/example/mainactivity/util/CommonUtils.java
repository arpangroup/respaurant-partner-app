package com.example.mainactivity.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.mainactivity.App;
import com.example.mainactivity.R;
import com.example.mainactivity.models.CouponDetails;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.views.MoreActivity;

import java.util.List;

public class CommonUtils {
    public static String getFormattedTime(int hourIn24HourFormat, int minute){
        int selectedHour = hourIn24HourFormat;
        int selectedMinute = minute;
        String AM_PM = " AM";
        String mmPreCode = "";
        if(selectedHour >= 12){
            AM_PM =  " PM";
            if(selectedHour >= 13 && selectedHour <24){
                selectedHour -= 12;
            }else{
                selectedHour = 12;
            }
        }else if(selectedHour == 0){
            selectedHour = 12;
        }
        if(minute < 10){
            mmPreCode = "0";
        }

        return selectedHour + ":"+mmPreCode+selectedMinute +" "+ AM_PM;
    }
    public static double getCouponAmount(CouponDetails couponDetails, double orderAmount){
        double discountAmount = 0.0;
        boolean isDiscountInPercentage = couponDetails.getDiscountType().equals("PERCENTAGE");
        double discount = Double.parseDouble(couponDetails.getDiscount());

        if(isDiscountInPercentage) discountAmount =  (orderAmount * discount) / 100;
        else discountAmount = discount;


        double uptoAmount = Double.parseDouble(couponDetails.getUptoAmount());
        if(discountAmount > uptoAmount) discountAmount = uptoAmount;

        return discountAmount;

    }
    public static void openWhatsApp(Context context) {
//        Uri uri = Uri.parse("smsto:" + Constants.WHATSAPP_SUPPORT_NUMBER + "?body="+"");
//        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//        intent.setType("text/plain");
//        String text = "How may I help you";
//        intent.setPackage("com.whatsapp");
//        intent.putExtra(Intent.EXTRA_TEXT, text);
//        startActivity(intent);

        String text = "How may I help you";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+Constants.WHATSAPP_SUPPORT_NUMBER +"&text="+text));
        context.startActivity(intent);

    }

    public static void showPushNotification(Context context, String title, String message){
        Intent notificationIntent = new Intent(context, MoreActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.CHANNEL_ID_PUSH_NOTIFICATION);
        builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)// clear notification after click
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(false)
                .setChannelId(App.CHANNEL_ID_PUSH_NOTIFICATION)
                .setOngoing(true);
        Notification notification = builder.build();

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(App.NOTIFICATION_ID_PUSH_NOTIFICATION, notification);
    }

    public static boolean isStatusChanged(List<Order> sourceOrders, Order targetOrder){
        final boolean[] result = {false};
        sourceOrders.forEach(order -> {
            if(order.getId()  == targetOrder.getId()){
                if(order.getOrderStatusId() == targetOrder.getOrderStatusId()){
                    result[0] = true;
                    return;
                }
            }
        });
        return result[0];
    }

}
