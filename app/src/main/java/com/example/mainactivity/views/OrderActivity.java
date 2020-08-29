package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.example.mainactivity.R;
import com.example.mainactivity.commons.OrderStatus;
import com.example.mainactivity.databinding.ActivityOrderBinding;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.viewmodels.OrderViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityOrderBinding mBinding;
    OrderViewModel orderViewModel;
    NavController navController;

    Dashboard mDashboard = null;

    public static enum OrderType {
        ALL,
        PREPARE,
        READY,
        PICKED
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.init();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        //NavigationUI.setupActionBarWithNavController(this, navController);

        initClicks();

        orderViewModel.getDashboard().observe(this, dashboard -> {
            mDashboard = dashboard;
            orderViewModel.setFilterOrders(dashboard.getAllOrders());
        });

    }

    private void initClicks() {
        //Toolbar Click:
        mBinding.toolbar.tagAll.setOnClickListener(view -> changeToolbarTag(OrderType.ALL));
        mBinding.toolbar.tagPreparing.setOnClickListener(view -> changeToolbarTag(OrderType.PREPARE));
        mBinding.toolbar.tagReady.setOnClickListener(view -> changeToolbarTag(OrderType.READY));
        mBinding.toolbar.tagPicked.setOnClickListener(view -> changeToolbarTag(OrderType.PICKED));
        //BottomNavigation Click:
        mBinding.bottomNavigation.menuLinear.setOnClickListener(view -> startActivity(new Intent(this, MenuActivity.class)));
        mBinding.bottomNavigation.accountLinear.setOnClickListener(view -> startActivity(new Intent(this, MoreActivity.class)));
    }

    public void changeToolbarTag(OrderType orderType){
        mBinding.toolbar.tagAll.setBackgroundResource(R.drawable.rounded_gray_border);
        mBinding.toolbar.tagPreparing.setBackgroundResource(R.drawable.rounded_gray_border);
        mBinding.toolbar.tagReady.setBackgroundResource(R.drawable.rounded_gray_border);
        mBinding.toolbar.tagPicked.setBackgroundResource(R.drawable.rounded_gray_border);

        mBinding.toolbar.tagAll.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.toolbar.tagPreparing.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.toolbar.tagReady.setTextColor(ContextCompat.getColor(this, R.color.black));
        mBinding.toolbar.tagPicked.setTextColor(ContextCompat.getColor(this, R.color.black));

        if(orderType == OrderType.ALL){
            mBinding.toolbar.tagAll.setBackgroundResource(R.drawable.rounded_red_border);
            mBinding.toolbar.tagAll.setTextColor(ContextCompat.getColor(this, R.color.holo_red_dark));

            orderViewModel.getDashboard().observe(this, dashboard -> orderViewModel.setFilterOrders(dashboard.getAllOrders()));
            return;
        }

        if(orderType == OrderType.PREPARE){
            mBinding.toolbar.tagPreparing.setBackgroundResource(R.drawable.rounded_red_border);
            mBinding.toolbar.tagPreparing.setTextColor(ContextCompat.getColor(this, R.color.holo_red_dark));

            List<Order> filterOrders = filterOrders(mDashboard.getAllOrders(), orderType);
            orderViewModel.setFilterOrders(filterOrders);
            return;
        }

        if(orderType == OrderType.READY){
            mBinding.toolbar.tagReady.setBackgroundResource(R.drawable.rounded_red_border);
            mBinding.toolbar.tagReady.setTextColor(ContextCompat.getColor(this, R.color.holo_red_dark));

            List<Order> filterOrders = filterOrders(mDashboard.getAllOrders(), orderType);
            orderViewModel.setFilterOrders(filterOrders);
            return;
        }

        if(orderType == OrderType.PICKED){
            mBinding.toolbar.tagPicked.setBackgroundResource(R.drawable.rounded_red_border);
            mBinding.toolbar.tagPicked.setTextColor(ContextCompat.getColor(this, R.color.holo_red_dark));

            List<Order> filterOrders = filterOrders(mDashboard.getAllOrders(), orderType);
            orderViewModel.setFilterOrders(filterOrders);
            return;
        }


    }


    public List<Order> filterOrders(List<Order> orderList, OrderType orderType){
        if (orderList == null) return new ArrayList<>();

        List<Order> filteredOrders;
        if(orderType == OrderType.PREPARE){
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() ==  OrderStatus.ORDER_RECEIVED.value() || order.getOrderStatusId() ==  OrderStatus.DELIVERY_GUY_ASSIGNED.value()).collect(Collectors.toList());
        }else if(orderType ==OrderType.READY){
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() ==  OrderStatus.ORDER_RECEIVED.value() && order.getOrderStatusId() ==  OrderStatus.DELIVERY_GUY_ASSIGNED.value()).collect(Collectors.toList());
        }else if(orderType == OrderType.PICKED){
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() ==  OrderStatus.ON_THE_WAY.value()).collect(Collectors.toList());
        }else{// ALL
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() !=  OrderStatus.ORDER_PLACED.value()).collect(Collectors.toList());
        }

        return filteredOrders;

    }

}