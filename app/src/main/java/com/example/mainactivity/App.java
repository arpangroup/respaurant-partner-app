package com.example.mainactivity;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class App extends Application {
    public static final String CHANNEL_ID_NEW_ORDER = "channel_new_orders";
    public static final String CHANNEL_ID_PUSH_NOTIFICATION = "channel_push_notifications";
    public static final String CHANNEL_NAME_NEW_ORDER = "channel new orders";
    public static final String CHANNEL_NAME_PUSH_NOTIFICATION = "channel push notifications";
    public static final int NOTIFICATION_ID_NEW_ORDER = 600;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

    }

    private void createNotificationChannels() {
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_mp3);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannelNewOrder = new NotificationChannel(
                    CHANNEL_ID_NEW_ORDER,
                    CHANNEL_NAME_NEW_ORDER,
                    NotificationManager.IMPORTANCE_HIGH
                    );

            NotificationChannel notificationChannelPushNotification = new NotificationChannel(
                    CHANNEL_ID_PUSH_NOTIFICATION,
                    CHANNEL_NAME_PUSH_NOTIFICATION,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            notificationChannelNewOrder.setSound(sound, attributes);


            notificationChannelNewOrder.setDescription("This is New Order Notification Channel");
            notificationChannelPushNotification.setDescription("This is Push Notifications channel");

            // Register the channels with Notification Framework
            //NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannelNewOrder);
            notificationManager.createNotificationChannel(notificationChannelPushNotification);

        }
    }
}
