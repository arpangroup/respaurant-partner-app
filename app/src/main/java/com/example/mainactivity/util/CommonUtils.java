package com.example.mainactivity.util;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.mainactivity.App;
import com.example.mainactivity.R;
import com.example.mainactivity.commons.OrderStatus;
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

    public static OrderStatus mapOrderStatus(int statusId){
        if(statusId == 1) return OrderStatus.ORDER_PLACED;
        if(statusId == 2) return OrderStatus.ORDER_RECEIVED;
        if(statusId == 3) return OrderStatus.DELIVERY_GUY_ASSIGNED;
        if(statusId == 4) return OrderStatus.ON_THE_WAY;
        if(statusId == 5) return OrderStatus.DELIVERED;
        if(statusId == 6) return OrderStatus.CANCELED;
        if(statusId == 7) return OrderStatus.ORDER_READY;
        if(statusId == 8) return OrderStatus.REACHED_PICKUP_LOCATION;
        if(statusId == 9) return OrderStatus.REACHED_DELIVERY_LOCATION;
        if(statusId == 10) return OrderStatus.READY_FOR_PICKUP;
        return null;
    }

    public static void makePhoneCall(Activity activity, String phoneNumber){
        String number = phoneNumber;
        if(number.trim().length() > 0){
            phoneNumber = number; // this is because if call permission is not available then after permission access we will  call this makePhoneCall() function
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, com.example.mainactivity.commons.Constants.REQUEST_CALL_ACTIVITY);
            }else{
                String dial = "tel:" + number;
                Log.i("APP", "Calling to......."+number+"....................");
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(dial));
                activity.startActivity(intent);
            }
        }

    }


}
