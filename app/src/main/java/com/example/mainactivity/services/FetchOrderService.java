package com.example.mainactivity.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.App;
import com.example.mainactivity.R;
import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.commons.Actions;
import com.example.mainactivity.commons.NotificationSoundType;
import com.example.mainactivity.commons.OrderStatus;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.User;
import com.example.mainactivity.models.request.NewOrderRequest;
import com.example.mainactivity.models.response.NewOrderResponse;
import com.example.mainactivity.sharedpref.ServiceTracker;
import com.example.mainactivity.sharedpref.UserSession;
import com.example.mainactivity.util.CommonUtils;
import com.example.mainactivity.views.MoreActivity;
import com.example.mainactivity.views.order.AcceptOrderActivityDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchOrderService extends LifecycleService {
    private static final String TAG = "FetchOrderService";
    private static final long ORDER_FETCH_INTERVAL = 1000 * 10;
    private boolean isLoading = false;

    public static MutableLiveData<Boolean> isFetching = new MutableLiveData<>(false);
    private static MutableLiveData<List<Order>> listedOrders = new MutableLiveData<>(new ArrayList<>());

    public static MutableLiveData<List<Order>> mutableNewOrders = new MutableLiveData<>(new ArrayList<>());
    public static MutableLiveData<List<Order>> mutableStatusList = new MutableLiveData<>(new ArrayList<>());



    private MediaPlayer mMediaPlayer;


    User user = null;
    Timer timer = null;


    private PowerManager.WakeLock wakeLock;
    private boolean isServiceStarted = false;

    private List<String> getListedOrderIds(){
        if(listedOrders == null || listedOrders.getValue() == null) {
            return new ArrayList<>();
        }
        else{
            return listedOrders.getValue().stream().map(order->String.valueOf(order.getId())).collect(Collectors.toList());
        }
    }



    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand executed with startId: "+startId);
        if(intent  != null){
            String action = intent.getAction();
            Log.d(TAG, "Using an Intent with action "+action);
            if(action != null){
                if(action.equals(Actions.START.name()))startService();
                else if(action.equals(Actions.STOP.name()))stopService();
                else Log.d(TAG, "This should never happen. No action in the received intent");
            }
        }else{
            Log.d(TAG, "with a null intent. It has been probably restarted by the system.");
        }
        return super.onStartCommand(intent, flags, startId);
    }



    private void startService() {
        if(isServiceStarted) return;
        Log.d(TAG, "Starting the foreground service task");
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
        isServiceStarted = true;
        ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STARTED);

        // we need this lock so our service gets not affected by Doze Mode
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "whatever");
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NewOrderFetchService::lock");
        wakeLock.acquire();

        // we're starting a loop in a coroutine
        doInBackground();
        Log.d(TAG, "End of the loop for the service");
    }
    private void stopService() {
        Log.d(TAG, "Stopping the foreground service");
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();

        try{
            if(wakeLock != null){
                if(wakeLock.isHeld()){
                    wakeLock.release();
                }
            }
            stopForeground(true);
            stopSelf();
            if(mMediaPlayer != null){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Service stopped without being started: "+e.getMessage());
        }
        isServiceStarted = false;
        ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STOPPED);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        user = UserSession.getUserData(this);
        timer = new Timer();
        Notification notification = createNotification();

        startForeground(App.NOTIFICATION_ID_NEW_ORDER, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "TASK REMOVED..............");

        Intent restartServiceIntent  = new Intent(getApplicationContext(), FetchOrderService.class);
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmService =(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePendingIntent);

//        PendingIntent service = PendingIntent.getService(
//                getApplicationContext(),
//                1001,
//                new Intent(getApplicationContext(), EndlessService.class),
//                PendingIntent.FLAG_ONE_SHOT);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }


    private Notification createNotification(){
        Intent notificationIntent = new Intent(this, MoreActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER);
        builder.setContentTitle("Fetching New Order")
                .setContentText("Fetching....")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)// clear notification after click
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(App.CHANNEL_ID_NEW_ORDER)
                .setOngoing(true);
        Notification notification = builder.build();


        Log.d(TAG, "Opening DialogActivity from Notification......");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(App.NOTIFICATION_ID_NEW_ORDER, notification);

        return notification;

    }

    private void doInBackground(){
        timer = new Timer();
        if(user != null){
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    NewOrderRequest newOrderRequest = new NewOrderRequest(user, getListedOrderIds());
                    //NewOrderRequest newOrderRequest = new NewOrderRequest(userId, new ArrayList<>());
                    if(!isLoading){
                        fetchNewOrders(newOrderRequest);
                    }
                }
            }, 0, ORDER_FETCH_INTERVAL);
        }else{
            Log.d(TAG, "USER IS NULL");
            Toast.makeText(this, "USER IS NULL", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchNewOrders(NewOrderRequest newOrderRequest){
        isLoading = true;
        ApiInterface apiInterface = ApiService.getApiService();
        Log.d(TAG, "FETCHING NEW ORDER........");
        Log.d(TAG, "LISTED_ORDERS: "+newOrderRequest.getListed_order_ids());
        apiInterface.getNewOrders(newOrderRequest).enqueue(new Callback<NewOrderResponse>() {
            @Override
            public void onResponse(Call<NewOrderResponse> call, Response<NewOrderResponse> response) {
                try{
                    List<Order> currentOrders = response.body() != null ? response.body().getNewOrders() : null;
                    List<Order> statusList = response.body() != null ? response.body().getListedOrderStatuses() : null;
                    if(currentOrders != null && currentOrders.size() > 0){
                        Log.d(TAG, "FETCHED_NEW_ORDERS: " + currentOrders.size());
                        List<Order> existingOrders = listedOrders.getValue();
                        if(existingOrders == null) existingOrders = new ArrayList<>();
                        if(existingOrders.size() != currentOrders.size()){
                            existingOrders.addAll(currentOrders);
                            listedOrders.postValue(existingOrders);
                            mutableNewOrders.postValue(currentOrders);
                            showFullScreenOrderArriveNotification();
                        }
                    }

                    if(statusList != null){
                        List<Order> changeList = statusList.stream().filter(order -> order.getOrderStatusId() != 1).collect(Collectors.toList());
                        Log.d(TAG, "###########################STATUS_CHANGES#####################################");
                        changeList.forEach(order -> System.out.println("ORDER_ID: "+ order.getId()  + "STATUS: "+ order.getOrderStatusId() + ", UID"+ order.getUniqueOrderId()));
                        // remove the ID from the existing list:
                        List<Order> existingOrders = listedOrders.getValue();
                        if(existingOrders != null){
                            changeList.forEach(order -> {
                                existingOrders.removeIf(order1 -> order.getId() == order1.getId());
                                handleOrderStatusChanged(order);
                            });
                        }
                        Log.d(TAG, "##############################################################################");
                        mutableStatusList.postValue(changeList);
                    }

                    isLoading = false;
                }catch (Exception e){
                    isLoading = false;
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<NewOrderResponse> call, Throwable t) {
                isLoading = false;
                t.printStackTrace();
            }
        });
    }


    private void showFullScreenOrderArriveNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_PUSH_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Intent fullScreenIntent = new Intent(this, AcceptOrderActivityDialog.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
            //fullScreenIntent.putExtra(Constants.NOTIFICATION_IDS, notificationId);
            //fullScreenIntent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("New Order Arrive")
                    .setContentText("A new order is arrived, please accept the order")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            Notification notification = builder.build();

            Log.d(TAG, "Opening DialogActivity from Notification......");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(App.NOTIFICATION_ID_PUSH_NOTIFICATION, notification);
        }else{
            Intent intent = new Intent(this, AcceptOrderActivityDialog.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            //intent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.d(TAG, "Starting DialogActivity......");
            startActivity(intent);
        }
    }


    private void handleOrderStatusChanged(Order fetchedOrder) {
        Log.d(TAG, "Inside handleOrderStatusChanged");
        OrderStatus orderStatus =  CommonUtils.mapOrderStatus(fetchedOrder.getOrderStatusId());
        switch (orderStatus){
            case ORDER_RECEIVED:
                startMediaPlayer(NotificationSoundType.ORDER_DELIVERED);
                CommonUtils.showPushNotification(this, "Order Accepted", fetchedOrder.getUniqueOrderId() + " has been accepted");
                break;
            case CANCELED:
                startMediaPlayer(NotificationSoundType.ORDER_CANCELED);
                CommonUtils.showPushNotification(this, "Order Cancelled", fetchedOrder.getUniqueOrderId() + " has been cancelled");
                break;
            default:
                return;
        }
    }


    private void startMediaPlayer(NotificationSoundType soundType) {
        mMediaPlayer = new MediaPlayer();
        Context context = getApplicationContext();
        if(soundType == NotificationSoundType.ORDER_ARRIVE)mMediaPlayer = MediaPlayer.create(context, R.raw.order_arrived_ringtone);
        else if(soundType == NotificationSoundType.ORDER_CANCELED)mMediaPlayer = MediaPlayer.create(context, R.raw.order_cancel_ringtone);
        else mMediaPlayer = MediaPlayer.create(context, R.raw.default_notification_sound);

        try{
            mMediaPlayer.start();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }


}
