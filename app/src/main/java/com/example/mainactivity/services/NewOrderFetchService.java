package com.example.mainactivity.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mainactivity.App;
import com.example.mainactivity.R;
import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.request.NewOrderRequest;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.views.MoreActivity;
import com.example.mainactivity.views.order.AcceptOrderActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewOrderFetchService extends Service {
    private static final String TAG = "NewOrderFetchService";
    public static final String NEW_ORDER_FETCH_SERVICE_MESSAGE = "NEW_ORDER_FETCH_SERVICE_MESSAGE";
    public static final String INTENT_EXTRA_INPUT_LISTED_ORDERS = "INTENT_EXTRA_INPUT_LISTED_ORDERS";
    public static final String INTENT_EXTRA_OUTPUT_NEW_ORDERS   = "INTENT_EXTRA_OUTPUT_NEW_ORDERS";
    List<String> listedOrderIds = new ArrayList<>();
    boolean isLoading = false;
    private static final long ORDER_FETCH_INTERVAL = 1000 * 5;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        String passedListedOrderIds = intent.getStringExtra(INTENT_EXTRA_INPUT_LISTED_ORDERS);
//        if(passedListedOrderIds != null){
//            String[] ids = passedListedOrderIds.split(", ");
//        }

        Intent notificationIntent = new Intent(this, MoreActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER_FETCH_SERVICE)
                .setContentTitle("Fetching New Order")
                .setContentText(passedListedOrderIds)
                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();


        doInBackground();
        startForeground(1, notification);

        //do heavy work in a background thread
        //stopSelf()

        return START_NOT_STICKY;
    }

    private void doInBackground() {
        int userId = new RequestToken().getUserId();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                NewOrderRequest newOrderRequest = new NewOrderRequest(userId, listedOrderIds);
                //NewOrderRequest newOrderRequest = new NewOrderRequest(userId, new ArrayList<>());
                if(!isLoading){
                    fetchNewOrders(newOrderRequest);
                }
            }
        }, 0, ORDER_FETCH_INTERVAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }


    private void fetchNewOrders(NewOrderRequest newOrderRequest){
        isLoading = true;
        ApiInterface apiInterface = ApiService.getApiService();
        apiInterface.getNewOrders(newOrderRequest).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                try{
                    List<Order> orders = response.body();
                    String ordersJson = new Gson().toJson(orders);
                    if(orders != null && orders.size() > 0){
                        Log.d(TAG, "FETCHED_NEW_ORDERS: "+ orders.size());
                        createFullScreenNotification(ordersJson);
                        sendMessageToUI(ordersJson);

                        orders.forEach(order -> listedOrderIds.add(order.getId()+""));
                    }
                    isLoading = false;
                }catch (Exception e){
                    isLoading = false;
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                isLoading = false;
                t.printStackTrace();
            }
        });
    }
    private void createFullScreenNotification(String orders){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Intent fullScreenIntent = new Intent(this, AcceptOrderActivity.class);
//            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
//            taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
            //fullScreenIntent.putExtra(Constants.NOTIFICATION_IDS, notificationId);
            fullScreenIntent.putExtra(INTENT_EXTRA_OUTPUT_NEW_ORDERS, orders);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Random notification title")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .setOngoing(true);
            Notification notification = builder.build();

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(App.NOTIFICATION_ID_NEW_ORDER, notification);
        }else{
            Intent intent = new Intent(this, AcceptOrderActivity.class);
            intent.putExtra(INTENT_EXTRA_OUTPUT_NEW_ORDERS, orders);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }
    private void sendMessageToUI(String orders){
        Intent intent = new Intent(NEW_ORDER_FETCH_SERVICE_MESSAGE);
        //intent.putExtra(SERVICE_PAYLOAD, data);
        Log.d(TAG, "SENDING: "+orders);
        intent.putExtra(INTENT_EXTRA_OUTPUT_NEW_ORDERS, orders);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
