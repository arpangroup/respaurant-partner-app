package com.example.mainactivity.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mainactivity.App;
import com.example.mainactivity.R;
import com.example.mainactivity.commons.OrderStatus;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.services.NewOrderFetchService;
import com.example.mainactivity.views.order.AcceptOrderActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private final String TAG = this.getClass().getSimpleName();
    public static final String SERVICE_MESSAGE = "MessagingService";
    public static final String MESSAGE_ORDER_STATUS = "MESSAGE_ORDER_STATUS";
    public static final String INTENT_EXTRA_ORDER_STATUS = "INTENT_EXTRA_ORDER_STATUS";
    enum PUSH_NOTIFICATION_SOURCE{
        CONSOLE,
        API_WITH_NOTIFICATION,
        API_WITHOUT_NOTIFICATION,
        UNKNOWN_SOURCE

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        PUSH_NOTIFICATION_SOURCE notificationSource = getNotificationSource(remoteMessage);
        Log.d(TAG, "onMessageReceived() called....");
        //Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        //String orderId = remoteMessage.getData().get("order_id");
        //String title = remoteMessage.getData().get("title");
        //Order order = new Order(Integer.parseInt(orderId), title);
        switch (notificationSource){
            case CONSOLE:
                break;
            case API_WITH_NOTIFICATION:
                break;
            case API_WITHOUT_NOTIFICATION:
                if(remoteMessage.getData().get("order") != null){
                    String orderJson = remoteMessage.getData().get("order");
                    Order order = new Gson().fromJson(orderJson, Order.class);
                    sendOrderNotification(order.getOrderStatusId(), order);
                    Log.d(TAG, "ID: "+order.getId());
                }
                break;
            case UNKNOWN_SOURCE:
                break;
        }
    }


    private void sendOrderNotification(int orderStatusId, Order order){
        String orderJson = null;
        switch (orderStatusId){
            case 1:
                List<Order> orders = Collections.singletonList(order);
                orderJson = new Gson().toJson(orders);
                //sendMessageToNewOrderActivity(orderJson);
                //openNewOrderActivityDialog(orderJson);
                if(AcceptOrderActivity.ACTIVE){
                    Log.d(TAG, "Sending Broadcat Message......");
                    sendMessageToNewOrderActivity(orderJson);
                }else{
                    Log.d(TAG, "Opening new Activity..........");
                    openNewOrderActivityDialog(orderJson);
                }
                break;
            case 2:
                //Accepted
                break;
            case 3:
            case 4:
                //Order is pickedUp and DeliveryGuy is on the way to deliver order
                //Delivery Guy Assigned
                orderJson = new Gson().toJson(order);
                sendOrderStatusChangedMessage(orderJson);
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
        }
    }


    private PUSH_NOTIFICATION_SOURCE getNotificationSource(RemoteMessage remoteMessage){
        PUSH_NOTIFICATION_SOURCE notificationSource;

        RemoteMessage.Notification notification =  remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();

        if(notification != null && data != null){
            if(data.size() == 0){
                notificationSource = PUSH_NOTIFICATION_SOURCE.CONSOLE;
            }else {
                notificationSource = PUSH_NOTIFICATION_SOURCE.API_WITH_NOTIFICATION;
            }
        }else if(remoteMessage.getData() != null){// used to notify the app without user being made aware of it
            notificationSource = PUSH_NOTIFICATION_SOURCE.API_WITHOUT_NOTIFICATION;
        }else{
            notificationSource = PUSH_NOTIFICATION_SOURCE.UNKNOWN_SOURCE;
        }
        return notificationSource;
    }
    private PendingIntent openScreen(int notificationId, String orderJson) {
        Intent fullScreenIntent = new Intent(this, AcceptOrderActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
        taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
        fullScreenIntent.putExtra("ORDER", orderJson);
        return PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    private void openNewOrderActivityDialog(String orders){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Intent fullScreenIntent = new Intent(this, AcceptOrderActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
            //fullScreenIntent.putExtra(Constants.NOTIFICATION_IDS, notificationId);
            fullScreenIntent.putExtra(NewOrderFetchService.INTENT_EXTRA_OUTPUT_NEW_ORDERS, orders);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Random notification title")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            Notification notification = builder.build();

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(App.NOTIFICATION_ID_NEW_ORDER, notification);
        }else{
            Intent intent = new Intent(this, AcceptOrderActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            intent.putExtra(NewOrderFetchService.INTENT_EXTRA_OUTPUT_NEW_ORDERS, orders);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


            startActivity(intent);
        }


    }
    private void sendMessageToNewOrderActivity(String orders){
        Intent intent = new Intent(NewOrderFetchService.NEW_ORDER_FETCH_SERVICE_MESSAGE);
        Log.d(TAG, "SENDING: "+orders);
        intent.putExtra("INTENT_EXTRA_OUTPUT_NEW_ORDERS", orders);
        // local broadcast receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    private void sendOrderStatusChangedMessage(String orderJson) {
        Intent intent = new Intent(MESSAGE_ORDER_STATUS);
        Log.d(TAG, "SENDING: "+orderJson);
        intent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
        // local broadcast receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }


}
