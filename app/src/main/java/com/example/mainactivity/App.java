package com.example.mainactivity;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class App extends Application {
    public static final String CHANNEL_ID_NEW_ORDER = "channel_new_orders";
    public static final String CHANNEL_ID_PUSH_NOTIFICATION = "channel_push_notifications";
    public static final String CHANNEL_ID_NEW_ORDER_FETCH_SERVICE = "channel_new_order_fetch_service";
    public static final String CHANNEL_NAME_NEW_ORDER = "channel new orders";
    public static final String CHANNEL_NAME_PUSH_NOTIFICATION = "channel push notifications";
    public static final String CHANNEL_NAME_NEW_ORDER_FETCH_SERVICE = "channel new order fetch service";
    public static final int NOTIFICATION_ID_NEW_ORDER = 600;
    public static final int NOTIFICATION_ID_PUSH_NOTIFICATION = 601;

    public static final Intent[] POWERMANAGER_INTENTS = {
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.default_notification_sound);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannelNewOrder = new NotificationChannel(
                    CHANNEL_ID_NEW_ORDER,
                    CHANNEL_NAME_NEW_ORDER,
                    NotificationManager.IMPORTANCE_HIGH
                    );

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID_NEW_ORDER_FETCH_SERVICE,
                    CHANNEL_NAME_NEW_ORDER_FETCH_SERVICE,
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
            notificationChannelNewOrder.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannelPushNotification.setDescription("This is Push Notifications channel");
            serviceChannel.setDescription("This is New Order Fetch Service channel");
            serviceChannel.enableLights(true);
            serviceChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            serviceChannel.setLightColor(Color.RED);
            serviceChannel.enableVibration(true);
            serviceChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            // Register the channels with Notification Framework
            //NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannelNewOrder);
            manager.createNotificationChannel(notificationChannelPushNotification);
            manager.createNotificationChannel(serviceChannel);

        }
    }

}
