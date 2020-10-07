package com.example.mainactivity.views.order;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.OrderAcceptListAdapter;
import com.example.mainactivity.commons.Constants;
import com.example.mainactivity.commons.NotificationSoundType;
import com.example.mainactivity.databinding.ActivityAcceptOrderBinding;
import com.example.mainactivity.databinding.ItemOrderAcceptBinding;
import com.example.mainactivity.firebase.MessagingService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.User;
import com.example.mainactivity.services.FetchOrderService;
import com.example.mainactivity.sharedpref.UserSession;
import com.example.mainactivity.util.CommonUtils;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.views.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AcceptOrderActivityDialog extends AppCompatActivity implements OrderAcceptListAdapter.OrderAcceptInterface{
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void subscribeToObserver(){
        Log.d(TAG, "Inside subscribeToObserver....");
        FetchOrderService.mutablePendingNewOrders.observe(this, orders -> {
            System.out.println("============================================================================");
            Log.d(TAG, "ORDERS: "+orders);
            orderViewModel.setNewOrder(orders);
            System.out.println("============================================================================");
        });
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

        subscribeToObserver();
        startMediaPlayer(NotificationSoundType.ORDER_ARRIVE);

        if(orderAcceptListAdapter == null){
            orderAcceptListAdapter = new OrderAcceptListAdapter(this);
            mBinding.orderRecycler.setAdapter(orderAcceptListAdapter);
        }

        orderViewModel.getNewOrders().observe(this, orders -> {
            if(orders.size() == 0){
                stopMediaPlayer();
                Intent  intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                orderAcceptListAdapter.submitList(orders);
                mBinding.toolbar.title.setText(orders.size() +" New Order");
            }
        });


        //mBinding.toolbar.close.setVisibility(View.GONE);
        mBinding.toolbar.close.setOnClickListener(view -> {
            stopMediaPlayer();
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
           }, 0, 3000);

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
        isMusicEnable  = false;
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
        if(orderViewModel.getNewOrders().getValue().size() == 1)stopMediaPlayer();
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