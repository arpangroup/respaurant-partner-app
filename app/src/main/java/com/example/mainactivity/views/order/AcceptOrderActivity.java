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
import com.example.mainactivity.firebase.MessagingService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AcceptOrderActivity extends AppCompatActivity implements OrderAcceptListAdapter.OrderAcceptInterface{
    private final String TAG = this.getClass().getSimpleName();
    public static boolean ACTIVE = false;
    ActivityAcceptOrderBinding mBinding;
    private MediaPlayer mMediaPlayer;

    OrderViewModel orderViewModel;
    private OrderAcceptListAdapter orderAcceptListAdapter;
    private NavController navController;


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //int id =  intent.getIntExtra("ORDER_ID", 0);
            //String title =  intent.getStringExtra("TITLE");
            //Log.d("MessagingService", "onReceive()......");
            //Log.d("MessagingService", "ID: "+id);
            String orderJson = intent.getStringExtra("ORDER");
            Order orderObj = new Gson().fromJson(orderJson, Order.class);
            //Toast.makeText(context, "ID: "+orderObj.getId(), Toast.LENGTH_SHORT).show();

            orderViewModel.setNewOrder(orderObj);
            setupMediaPlayer();
            ACTIVE = true;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, new IntentFilter(MessagingService.SERVICE_MESSAGE));
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
           String orderJson = getIntent().getStringExtra("ORDER");
           Order orderObj = new Gson().fromJson(orderJson, Order.class);
           orderViewModel.setNewOrder(orderObj);

           setupMediaPlayer();
       }




        orderViewModel.getNewOrders().observe(this, orders -> {
            //System.out.println("==========================ORDERS================================\n");
            //orders.forEach(order -> System.out.println("ID: "+order.getId() +", Title: "+order.getUniqueOrderId()));
            //Log.d(TAG, "ORDER: "+orders.get(0));
            orderAcceptListAdapter.submitList(orders);
            mBinding.toolbar.title.setText(orders.size() +" New Order");
            orderAcceptListAdapter.notifyDataSetChanged();
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

    }

    @Override
    public void onDecreasePreparationTime(Order order) {

    }

    @Override
    public void onRejectClick(Order order) {

    }

    @Override
    public void onAcceptClick(Order order) {

    }
}