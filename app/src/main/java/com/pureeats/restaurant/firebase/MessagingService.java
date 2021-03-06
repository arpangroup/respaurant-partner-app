package com.pureeats.restaurant.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pureeats.restaurant.App;
import com.pureeats.restaurant.R;
import com.pureeats.restaurant.commons.OrderStatus;
import com.pureeats.restaurant.sharedpref.ServiceTracker;
import com.pureeats.restaurant.sharedpref.UserSession;
import com.pureeats.restaurant.util.CommonUtils;
import com.pureeats.restaurant.views.order.AcceptOrderActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.stream.IntStream;

public class MessagingService extends FirebaseMessagingService {
    private final String TAG = this.getClass().getSimpleName();
    public static final String SERVICE_MESSAGE = "MessagingService";
    public static final String MESSAGE_ORDER_STATUS = "MESSAGE_ORDER_STATUS";
    public static final String INTENT_EXTRA_ORDER_STATUS = "INTENT_EXTRA_ORDER_STATUS";

    enum PushNotificationSource {
        CONSOLE,
        API_WITH_NOTIFICATION,
        API_WITHOUT_NOTIFICATION,
        UNKNOWN_SOURCE

    }
    int[] validOrderStatusList = {
//            OrderStatus.ORDER_PLACED.value(),//1
//            OrderStatus.DELIVERY_GUY_ASSIGNED.value(),//3
            //OrderStatus.REACHED_PICKUP_LOCATION.value(),//8
//            OrderStatus.ON_THE_WAY.value(),// 4 ORDER_PICKED_FROM_RESTAURANT_BY_DELIVERY_GUY
            OrderStatus.DELIVERED.value(),//5
            OrderStatus.CANCELED.value()//6
    };

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Inside onCreate().....................");
        Log.d(TAG, "TOKEN:  " + UserSession.getPushNotificationToken());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived() called....");
        PushNotificationSource notificationSource = getNotificationSource(remoteMessage);

        switch (notificationSource){
            case CONSOLE:
                break;
            case API_WITH_NOTIFICATION:
                break;
            case API_WITHOUT_NOTIFICATION:
                if(remoteMessage.getData().get("order") != null){
                    if(ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STARTED){
                        processOrderStatusChangedData(remoteMessage);
                    }
                }
                break;
            case UNKNOWN_SOURCE:
                break;
        }
    }

    private void processOrderStatusChangedData(RemoteMessage remoteMessage){
        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, "#######################################################################");
        Log.d(TAG, "TITLE: "+data.get("title"));
        Log.d(TAG, "MESSAGE: "+data.get("message"));
        Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        Log.d(TAG, "UNIQUE_ORDER_ID: " + data.get("unique_order_id"));
        Log.d(TAG, "ORDER_ID: " + data.get("order_id"));
        Log.d(TAG, "ORDER_STATUS_ID: " + data.get("order_status_id"));
        Log.d(TAG, "#######################################################################");


        String orderStatusIdStr = data.get("order_status_id");
        if(orderStatusIdStr == null) return;
        int orderStatusId = Integer.parseInt(orderStatusIdStr);

        boolean isValidOrderStatus = IntStream.of(validOrderStatusList).anyMatch(val -> val ==orderStatusId);
        if(isValidOrderStatus){
            String orderJson = remoteMessage.getData().get("order");
            String title = data.get("title");
            String message = data.get("message");
            if(orderStatusId == OrderStatus.ORDER_PLACED.value()){
                // Show full screen notification
                //showFullScreenOrderArriveNotification(title, message, orderJson);
                //sendMessageToBroadCastReceiver(orderJson);
            }else if(orderStatusId == OrderStatus.CANCELED.value() ||  orderStatusId == OrderStatus.DELIVERED.value()){
                CommonUtils.showPushNotification(this, data.get("title"), data.get("message"));
            }
            else{
                if (ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STARTED){
                    sendMessageToBroadCastReceiver(orderJson);
                }
            }
        }

    }


    private void showFullScreenOrderArriveNotification(String title, String message, String orderJson) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Intent fullScreenIntent = new Intent(this, AcceptOrderActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
            //fullScreenIntent.putExtra(Constants.NOTIFICATION_IDS, notificationId);
            fullScreenIntent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            Notification notification = builder.build();

            Log.d(TAG, "Opening DialogActivity from Notification......");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(App.NOTIFICATION_ID_NEW_ORDER, notification);
        }else{
            Intent intent = new Intent(this, AcceptOrderActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            intent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.d(TAG, "Starting DialogActivity......");
            startActivity(intent);
        }
    }
    private void sendMessageToBroadCastReceiver(String orderJson) {
        Intent intent = new Intent(MESSAGE_ORDER_STATUS);
        intent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
        // local broadcast receiver
        SystemClock.sleep(1000);
        Log.d(TAG, "SENDING: "+orderJson);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }
    private PushNotificationSource getNotificationSource(RemoteMessage remoteMessage){
        PushNotificationSource notificationSource;

        RemoteMessage.Notification notification =  remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();

        if(notification != null && data != null){
            if(data.size() == 0){
                notificationSource = PushNotificationSource.CONSOLE;
            }else {
                notificationSource = PushNotificationSource.API_WITH_NOTIFICATION;
            }
        }else if(remoteMessage.getData() != null){// used to notify the app without user being made aware of it
            notificationSource = PushNotificationSource.API_WITHOUT_NOTIFICATION;
        }else{
            notificationSource = PushNotificationSource.UNKNOWN_SOURCE;
        }
        return notificationSource;
    }


}
