package com.example.mainactivity.views.order;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.OrderListAdapter;
import com.example.mainactivity.commons.OrderStatus;
import com.example.mainactivity.databinding.FragmentOrderListBinding;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.views.menuitem.MenuActivity;
import com.example.mainactivity.views.MoreActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderListFragment extends Fragment implements OrderListAdapter.OrderPrepareInterface {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentOrderListBinding mBinding;
    OrderViewModel orderViewModel;
    private OrderListAdapter orderListAdapter;
    private NavController navController;
    private Dashboard mDashboard;

    public static enum OrderType {
        ALL,
        PREPARE,
        READY,
        PICKED
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentOrderListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.init();

        initClicks();

        // Initialize NavController
        navController = Navigation.findNavController(view);

        // Initialize RecyclerView
        orderListAdapter = new OrderListAdapter(this);
        mBinding.orderRecycler.setAdapter(orderListAdapter);


        orderViewModel.getDashboard().observe(requireActivity(), dashboard -> {
            mDashboard = dashboard;
            orderViewModel.setFilterOrders(dashboard.getAllOrders());
        });

        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });

        orderViewModel.getAllOrders().observe(getViewLifecycleOwner(), orders -> {
            orderListAdapter.submitList(orders);
        });


    }





    private void initClicks() {
        //Toolbar Click:
        mBinding.toolbar.tagAll.setOnClickListener(view -> changeToolbarTag(OrderType.ALL));
        mBinding.toolbar.tagPreparing.setOnClickListener(view -> changeToolbarTag(OrderType.PREPARE));
        mBinding.toolbar.tagReady.setOnClickListener(view -> changeToolbarTag(OrderType.READY));
        mBinding.toolbar.tagPicked.setOnClickListener(view -> changeToolbarTag(OrderType.PICKED));
        //BottomNavigation Click:
        mBinding.bottomNavigation.menuLinear.setOnClickListener(view -> startActivity(new Intent(requireActivity(), MenuActivity.class)));
        mBinding.bottomNavigation.accountLinear.setOnClickListener(view -> startActivity(new Intent(requireActivity(), MoreActivity.class)));
    }

    public void changeToolbarTag(OrderType orderType){
        mBinding.toolbar.tagAll.setBackgroundResource(R.drawable.rounded_gray_border);
        mBinding.toolbar.tagPreparing.setBackgroundResource(R.drawable.rounded_gray_border);
        mBinding.toolbar.tagReady.setBackgroundResource(R.drawable.rounded_gray_border);
        mBinding.toolbar.tagPicked.setBackgroundResource(R.drawable.rounded_gray_border);

        mBinding.toolbar.tagAll.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black));
        mBinding.toolbar.tagPreparing.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black));
        mBinding.toolbar.tagReady.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black));
        mBinding.toolbar.tagPicked.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black));

        if(orderType == OrderType.ALL){
            mBinding.toolbar.tagAll.setBackgroundResource(R.drawable.rounded_red_border);
            mBinding.toolbar.tagAll.setTextColor(ContextCompat.getColor(requireActivity(), R.color.holo_red_dark));

            orderViewModel.getDashboard().observe(requireActivity(), dashboard -> orderViewModel.setFilterOrders(dashboard.getAllOrders()));
            return;
        }

        if(orderType == OrderType.PREPARE){
            mBinding.toolbar.tagPreparing.setBackgroundResource(R.drawable.rounded_red_border);
            mBinding.toolbar.tagPreparing.setTextColor(ContextCompat.getColor(requireActivity(), R.color.holo_red_dark));

            List<Order> filterOrders = filterOrders(mDashboard.getAllOrders(), orderType);
            orderViewModel.setFilterOrders(filterOrders);
            return;
        }

        if(orderType == OrderType.READY){
            mBinding.toolbar.tagReady.setBackgroundResource(R.drawable.rounded_red_border);
            mBinding.toolbar.tagReady.setTextColor(ContextCompat.getColor(requireActivity(), R.color.holo_red_dark));

            List<Order> filterOrders = filterOrders(mDashboard.getAllOrders(), orderType);
            orderViewModel.setFilterOrders(filterOrders);
            return;
        }

        if(orderType == OrderType.PICKED){
            mBinding.toolbar.tagPicked.setBackgroundResource(R.drawable.rounded_red_border);
            mBinding.toolbar.tagPicked.setTextColor(ContextCompat.getColor(requireActivity(), R.color.holo_red_dark));

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