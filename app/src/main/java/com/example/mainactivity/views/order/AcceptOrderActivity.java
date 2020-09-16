package com.example.mainactivity.views.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
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
import android.view.WindowManager;
import android.widget.Toast;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.OrderAcceptListAdapter;
import com.example.mainactivity.databinding.ActivityAcceptOrderBinding;
import com.example.mainactivity.databinding.FragmentAcceptOrderBinding;
import com.example.mainactivity.databinding.ItemOrderAcceptBinding;
import com.example.mainactivity.firebase.MessagingService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.services.NewOrderFetchService;
import com.example.mainactivity.sharedpref.UserSession;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AcceptOrderActivity extends AppCompatActivity implements OrderAcceptListAdapter.OrderAcceptInterface{
    private final String TAG = this.getClass().getSimpleName();
    public static boolean ACTIVE = false;
    ActivityAcceptOrderBinding mBinding;
    private MediaPlayer mMediaPlayer;

    OrderViewModel orderViewModel;
    private OrderAcceptListAdapter orderAcceptListAdapter;
    private NavController navController;
    private int mNewOrderCount = 0;
    TypeToken<List<Order>> convertType = new TypeToken<List<Order>>() {};



    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                String ordersJson = intent.getStringExtra(NewOrderFetchService.INTENT_EXTRA_OUTPUT_NEW_ORDERS);
                System.out.println("==================RECEIVED==========================");
                System.out.println(ordersJson);
                System.out.println("====================================================");
                List<Order> orders = new Gson().fromJson(ordersJson, convertType.getType());
                //Toast.makeText(context, "ID: "+orderObj.getId(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, orders.size() +" New order received", Toast.LENGTH_SHORT).show();

                orderViewModel.setNewOrder(orders);
                setupMediaPlayer();
                ACTIVE = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, new IntentFilter(NewOrderFetchService.NEW_ORDER_FETCH_SERVICE_MESSAGE));
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.activity_accept_order);
        mBinding = ActivityAcceptOrderBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        UserSession userSession = new UserSession(this);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.init();


        orderAcceptListAdapter = new OrderAcceptListAdapter(this);
        mBinding.orderRecycler.setAdapter(orderAcceptListAdapter);



        //int orderId = getIntent().getIntExtra("ORDER_ID", 0);
        //String title = getIntent().getStringExtra("TITLE");
        //Log.d("MessagingService", "Inside onCreate() of AcceptOrderActivity..................");
        //Log.d("MessagingService", "ORDER_ID: "+orderId);
        //Log.d("MessagingService", "TITLE: "+title);
       if(!ACTIVE){
           try{
               String ordersJson = getIntent().getStringExtra(NewOrderFetchService.INTENT_EXTRA_OUTPUT_NEW_ORDERS);
               List<Order> orders = new Gson().fromJson(ordersJson, convertType.getType());
               orderViewModel.setNewOrder(orders);
               setupMediaPlayer();
           }catch (Exception e){
               e.printStackTrace();
           }

       }


//        orderViewModel.setNewOrder(new Order(1, "ORD-hebehdhcb-shjsdh001"));
//        orderViewModel.setNewOrder(new Order(2, "ORD-hebehdhcb-shjsdh002"));
//        orderViewModel.setNewOrder(new Order(3, "ORD-hebehdhcb-shjsdh003"));
//        orderViewModel.setNewOrder(new Order(4, "ORD-hebehdhcb-shjsdh004"));


        orderViewModel.getNewOrders().observe(this, orders -> {
            //mNewOrderCount = orders.size();
            //System.out.println("==========================ORDERS================================\n");
            //orders.forEach(order -> System.out.println("ID: "+order.getId() +", Title: "+order.getUniqueOrderId()));
            //Log.d(TAG, "ORDER: "+orders.get(0));
            orderAcceptListAdapter.submitList(orders);
            mBinding.toolbar.title.setText(orders.size() +" New Order");
            //orderAcceptListAdapter.notifyDataSetChanged();
        });


        mBinding.toolbar.close.setOnClickListener(view -> {
            onBackPressed();
        });

    }


    private void setupMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        Context context = getApplicationContext();
        mMediaPlayer = MediaPlayer.create(context, R.raw.alert_5);
        mMediaPlayer.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }

    @Override
    public void onIncreasePreparationTime(Order order) {
        Toast.makeText(this, "Click: INCREASE", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDecreasePreparationTime(Order order) {
        Toast.makeText(this, "Click: DECREASE", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRejectClick(Order order) {
        Toast.makeText(this, "CLICK: REJECT", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAcceptClick(Order order, ItemOrderAcceptBinding binding) {
        Toast.makeText(this, "CLICK: ACCEPT", Toast.LENGTH_SHORT).show();
        binding.layoutProgress.setVisibility(View.VISIBLE);
        orderViewModel.acceptOrder(order.getId()).observe(this, apiResponse -> {
            System.out.println("====================ORDER_ACCEPT_RESPONSE======================");
            System.out.println(apiResponse);
            if(apiResponse.isSuccess()){
                binding.layoutProgress.setVisibility(View.GONE);
                finish();//closing the popup
            }
        });
    }

    @Override
    public void onAutoCancelOrder(Order order) {
        try{
            Toast.makeText(this, "Order Cancelled: "+order.getId(), Toast.LENGTH_SHORT).show();
            List<Order> orders = orderViewModel.getAllOrders().getValue();
            orders.removeIf(orderObj -> orderObj.getId() == order.getId());
            orderAcceptListAdapter.submitList(orders);
            orderAcceptListAdapter.notifyItemRemoved(order.getId());
        }catch (Exception e){

        }

    }
}