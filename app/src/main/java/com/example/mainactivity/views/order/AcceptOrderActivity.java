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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.OrderAcceptListAdapter;
import com.example.mainactivity.databinding.ActivityAcceptOrderBinding;
import com.example.mainactivity.databinding.ItemOrderAcceptBinding;
import com.example.mainactivity.firebase.MessagingService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.User;
import com.example.mainactivity.services.NewOrderFetchService;
import com.example.mainactivity.sharedpref.UserSession;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.views.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
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
                //List<Order> orders = new Gson().fromJson(ordersJson, convertType.getType());
                //Toast.makeText(context, "ID: "+orderObj.getId(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, orders.size() +" New order received", Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "RECEIVER TRIGGERED", Toast.LENGTH_SHORT).show();
                //orderViewModel.setNewOrder(orders.get(0));
                //setupMediaPlayer();
                Order order = new Gson().fromJson(ordersJson, Order.class);
                Log.d(TAG, "ORDER_JSON: "+ordersJson);
                Log.d(TAG, "ORDER: "+order);
                orderViewModel.setNewOrder(order);
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
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, new IntentFilter(MessagingService.MESSAGE_ORDER_STATUS));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ACTIVE = false;
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ACTIVE = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
            orderViewModel.setNewOrder(order);
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
                finish();
            }
            orderAcceptListAdapter.submitList(orders);
            mBinding.toolbar.title.setText(orders.size() +" New Order");
        });


        mBinding.toolbar.close.setOnClickListener(view -> {
            onBackPressed();
        });




    }


    private void setupMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        Context context = getApplicationContext();
        mMediaPlayer = MediaPlayer.create(context, R.raw.alert_5);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isMusicEnable){
                    mMediaPlayer.start();
                }
            }
        }, 0, 1000);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isMusicEnable = false;
        ACTIVE = false;
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isMusicEnable = false;
    }

    @Override
    public void onIncreasePreparationTime(Order order) {
        int preparationTime = Integer.parseInt(order.getRestaurant().getDeliveryTime());
        preparationTime += 1;
        orderViewModel.changeDeliveryTimeOfNotAcceptedOrder(order.getId(), preparationTime);
    }

    @Override
    public void onDecreasePreparationTime(Order order) {
        int preparationTime = Integer.parseInt(order.getRestaurant().getDeliveryTime());
        preparationTime -= 1;
        if(preparationTime < 1) preparationTime = 0;
        orderViewModel.changeDeliveryTimeOfNotAcceptedOrder(order.getId(), preparationTime);
    }

    @Override
    public void onRejectClick(Order order) {
        orderViewModel.cancelOrder(order,  mUser.getId(), "REJECT_BY_RESTAURANT").observe(this, apiResponse -> {
            if(apiResponse.isSuccess()){
                orderViewModel.removeOrderFromNewOrderList(order);
            }
        });
    }



    @Override
    public void onAcceptClick(Order order, ItemOrderAcceptBinding binding) {
        binding.layoutProgress.setVisibility(View.VISIBLE);
        orderViewModel.acceptOrder(order, mUser.getId()).observe(this, apiResponse -> {
            System.out.println(apiResponse);
            if(apiResponse.isSuccess()){
                orderViewModel.removeOrderFromNewOrderList(order);
                binding.layoutProgress.setVisibility(View.GONE);

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
}