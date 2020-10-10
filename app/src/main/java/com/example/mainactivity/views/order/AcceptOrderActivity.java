package com.example.mainactivity.views.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.OrderAcceptListAdapter;
import com.example.mainactivity.commons.Constants;
import com.example.mainactivity.commons.NotificationSoundType;
import com.example.mainactivity.databinding.ActivityAcceptOrderBinding;
import com.example.mainactivity.databinding.ItemOrderAcceptBinding;
import com.example.mainactivity.firebase.MessagingService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.User;
import com.example.mainactivity.sharedpref.UserSession;
import com.example.mainactivity.util.CommonUtils;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.views.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AcceptOrderActivity extends AppCompatActivity implements OrderAcceptListAdapter.OrderAcceptInterface{
    private final String TAG = this.getClass().getSimpleName();
    public static boolean ACTIVE = false;
    ActivityAcceptOrderBinding mBinding;

    private boolean isMusicEnable = true;
    private MediaPlayer mMediaPlayer;
    Timer timer;

    OrderViewModel orderViewModel;
    private OrderAcceptListAdapter orderAcceptListAdapter;
    private NavController navController;
    private int mNewOrderCount = 0;
    TypeToken<List<Order>> convertType = new TypeToken<List<Order>>() {};

    private User mUser = null;




    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                String ordersJson = intent.getStringExtra(MessagingService.INTENT_EXTRA_ORDER_STATUS);
                System.out.println("==================RECEIVED==========================");
                System.out.println(ordersJson);
                System.out.println("====================================================");
                Order order = new Gson().fromJson(ordersJson, Order.class);
                Log.d(TAG, "ORDER_JSON: "+ordersJson);
                Log.d(TAG, "ORDER: "+order);
                if(order.getOrderStatusId() == 1){
                    List<Order> existingOrders =  orderViewModel.getNewOrders().getValue();
                    if(existingOrders != null){
                        if(existingOrders.size() > 0){
                            boolean isOrderPresent = existingOrders.stream().anyMatch(order1 -> order1.getId() == order.getId()) ;
                           if(!isOrderPresent){
                               orderViewModel.setNewOrder(order);
                               startMediaPlayer(NotificationSoundType.ORDER_ARRIVE);
                           }
                        }
                    }
                }
                if(order.getOrderStatusId() == 6){// if user cancel the order
                    Log.d(TAG, "ORDER CANCELLED.......");
                    orderViewModel.removeOrderFromNewOrderList(order);
                    CommonUtils.showPushNotification(getApplicationContext(), "Order Cancelled",  "You have missed one order, Customer cancelled the order");
                    startMediaPlayer(NotificationSoundType.ORDER_CANCELED);
                }
                ACTIVE = true;
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context, "RECEIVER: EXCEPTION", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()....");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, new IntentFilter(MessagingService.MESSAGE_ORDER_STATUS));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()....");
    }

    @Override
    protected void onStop() {
        super.onStop();
        ACTIVE = false;
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ACTIVE = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(TAG, "Inside onCreate()..........................");
        //setContentView(R.layout.activity_accept_order);
        mBinding = ActivityAcceptOrderBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        mUser = UserSession.getUserData(this);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.init();

        if(orderAcceptListAdapter == null){
            orderAcceptListAdapter = new OrderAcceptListAdapter(this);
            mBinding.orderRecycler.setAdapter(orderAcceptListAdapter);
        }




        //int orderId = getIntent().getIntExtra("ORDER_ID", 0);
        //String title = getIntent().getStringExtra("TITLE");
        //Log.d("MessagingService", "Inside onCreate() of AcceptOrderActivity..................");
        //Log.d("MessagingService", "ORDER_ID: "+orderId);
        //Log.d("MessagingService", "TITLE: "+title);
       if(!ACTIVE){
       }
        try{
            String ordersJson = getIntent().getStringExtra(MessagingService.INTENT_EXTRA_ORDER_STATUS);
            //List<Order> orders = new Gson().fromJson(ordersJson, convertType.getType());
            //orderViewModel.setNewOrder(orders.get(0));
            Order order = new Gson().fromJson(ordersJson, Order.class);
            Log.d(TAG, "ORDER_JSON: "+ordersJson);
            Log.d(TAG, "ORDER: "+order);
            if(order.getOrderStatusId() == 1){
                orderViewModel.setNewOrder(order);
                startMediaPlayer(NotificationSoundType.ORDER_ARRIVE);
            }
            if(order.getOrderStatusId() == 6){// if user cancel the order
                Log.d(TAG, "ORDER CANCELLED.......");
                orderViewModel.removeOrderFromNewOrderList(order);
                CommonUtils.showPushNotification(getApplicationContext(), "Order Cancelled",  "You have missed one order, Customer cancelled the order");
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "INTENT: EXCEPTION", Toast.LENGTH_SHORT).show();
        }


//        orderViewModel.setNewOrder(new Order(1, "ORD-hebehdhcb-shjsdh001"));
//        orderViewModel.setNewOrder(new Order(2, "ORD-hebehdhcb-shjsdh002"));
//        orderViewModel.setNewOrder(new Order(3, "ORD-hebehdhcb-shjsdh003"));
//        orderViewModel.setNewOrder(new Order(4, "ORD-hebehdhcb-shjsdh004"));


        orderViewModel.getNewOrders().observe(this, orders -> {
            if(orders.size() == 0){
                if(mMediaPlayer != null){
                    isMusicEnable = false;
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                Intent  intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                finish();
            }
            orderAcceptListAdapter.submitList(orders);
            mBinding.toolbar.title.setText(orders.size() +" New Order");
        });


        //mBinding.toolbar.close.setVisibility(View.GONE);
        mBinding.toolbar.close.setOnClickListener(view -> {
            List<Order> notAcceptedOrders = orderViewModel.getNewOrders().getValue();
            onDialogDismiss(notAcceptedOrders);
        });




    }


    private void startMediaPlayer(NotificationSoundType soundType) {
        mMediaPlayer = new MediaPlayer();
        Context context = getApplicationContext();
        if(soundType == NotificationSoundType.ORDER_ARRIVE)mMediaPlayer = MediaPlayer.create(context, R.raw.order_arrived_ringtone);
        else if(soundType == NotificationSoundType.ORDER_CANCELED)mMediaPlayer = MediaPlayer.create(context, R.raw.swiggy_order_cancel_ringtone);
        else mMediaPlayer = MediaPlayer.create(context, R.raw.default_notification_sound);

       if(soundType == NotificationSoundType.ORDER_ARRIVE){
           timer = new Timer();
           timer.schedule(new TimerTask() {
               @Override
               public void run() {
                   if(isMusicEnable){
                       try{
                           mMediaPlayer.start();
                       }catch (Exception e){
                           //e.printStackTrace();
                       }
                   }
               }
           }, 0, 1000);

       }else{
           try{
               mMediaPlayer.start();
           }catch (Exception e){
               //e.printStackTrace();
           }
       }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isMusicEnable = false;
        ACTIVE = false;
        stopMediaPlayer();

    }
    private void stopMediaPlayer(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {
        try{
            List<Order> notAcceptedOrders = orderViewModel.getNewOrders().getValue();
            if (notAcceptedOrders != null){
                if(notAcceptedOrders.size() > 0){
                    notAcceptedOrders.forEach(order -> orderViewModel.cancelOrder(order,  mUser.getId(), "CANCELLED_BY_RESTAURANT"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onBackPressed();
        isMusicEnable = false;


    }

    @Override
    public void onIncreasePreparationTime(Order order) {
        int preparationTime = Integer.parseInt(order.getRestaurant().getDeliveryTime());
        if(preparationTime == Constants.FOOD_PREPARE_TIME_MAX)return;
        preparationTime += 1;
        orderViewModel.changeDeliveryTimeOfNotAcceptedOrder(order.getId(), preparationTime);
    }

    @Override
    public void onDecreasePreparationTime(Order order) {
        int preparationTime = Integer.parseInt(order.getRestaurant().getDeliveryTime());
        if(preparationTime == Constants.FOOD_PREPARE_TIME_MIN)return;
        preparationTime -= 1;
        if(preparationTime < 1) preparationTime = 0;
        orderViewModel.changeDeliveryTimeOfNotAcceptedOrder(order.getId(), preparationTime);
    }

    @Override
    public void onRejectClick(Order order, ItemOrderAcceptBinding binding) {
        if(binding != null)binding.layoutProgress.setVisibility(View.VISIBLE);
        orderViewModel.cancelOrder(order,  mUser.getId(), "REJECT_BY_RESTAURANT").observe(this, apiResponse -> {
            if(binding != null)binding.layoutProgress.setVisibility(View.GONE);
            if(apiResponse.isSuccess()){
                orderViewModel.removeOrderFromNewOrderList(order);
            }
        });
    }



    @Override
    public void onAcceptClick(Order order, ItemOrderAcceptBinding binding) {
        binding.layoutProgress.setVisibility(View.VISIBLE);
        orderViewModel.acceptOrder(order, mUser.getId()).observe(this, apiResponse -> {
            binding.layoutProgress.setVisibility(View.GONE);
            System.out.println(apiResponse);
            if(apiResponse.isSuccess()){
                orderViewModel.removeOrderFromNewOrderList(order);
            }
        });
    }

    @Override
    public void onAutoCancelOrder(Order order) {
        try{
//            Toast.makeText(this, "Order Cancelled: "+order.getId(), Toast.LENGTH_SHORT).show();
//            List<Order> orders = orderViewModel.getAllOrders().getValue();
//            orders.removeIf(orderObj -> orderObj.getId() == order.getId());
//            orderAcceptListAdapter.submitList(orders);
//            orderAcceptListAdapter.notifyItemRemoved(order.getId());
        }catch (Exception e){

        }

    }

    @Override
    public void onDialogDismiss(List<Order> notAcceptedOrders) {
        onBackPressed();
    }
}