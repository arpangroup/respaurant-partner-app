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
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.views.menuitem.MenuActivity;
import com.example.mainactivity.views.MoreActivity;
import com.example.mainactivity.views.menuitem.MenuListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderListFragment extends Fragment implements OrderListAdapter.OrderPrepareInterface {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentOrderListBinding mBinding;
    OrderViewModel orderViewModel;
    private OrderListAdapter orderListAdapter;
    private NavController navController;
    //private Dashboard mDashboard;
    private List<Order> mOrders =  new ArrayList<>();

    @Override
    public void onAutoCancelOrder(Order order) {

    }

    private static enum FilterType {
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
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.init();

        initClicks();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        // Initialize RecyclerView
        orderListAdapter = new OrderListAdapter(this);
        mBinding.orderRecycler.setAdapter(orderListAdapter);


//        orderViewModel.getDashboard().observe(requireActivity(), dashboard -> {
//            mDashboard = dashboard;
//            orderViewModel.setFilterOrders(dashboard.getAllOrders());
//        });

        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });

        orderViewModel.getAllAcceptedOrders().observe(requireActivity(), orders -> {
            mOrders = orders;
            mBinding.toolbar.tagAll.setText("All ("+orders.size() +")");
            orderViewModel.setFilterOrders(orders);
        });

        orderViewModel.getAllFilteredOrders().observe(requireActivity(), orders -> {
            mBinding.toolbar.tagAll.setText("All (" + mOrders.size() +")");

            List<Order> preparingOrders = filterOrders(mOrders,  FilterType.PREPARE);
            mBinding.toolbar.tagPreparing.setText("Preparing (" + preparingOrders.size() +")");

            List<Order> readyOrders = filterOrders(mOrders,  FilterType.READY);
            mBinding.toolbar.tagReady.setText("Ready (" + readyOrders.size() +")");

            List<Order> pickedUpOrders = filterOrders(mOrders,  FilterType.PICKED);
            mBinding.toolbar.tagPicked.setText("PickedUp (" + pickedUpOrders.size() +")");

            orderListAdapter.submitList(orders);
        });




    }





    private void initClicks() {
        //Toolbar Click:
        mBinding.toolbar.tagAll.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                List<Order> filterItems = filterOrders(mOrders, FilterType.ALL);
                orderViewModel.setFilterOrders(filterItems);
            }
        });
        mBinding.toolbar.tagPreparing.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                List<Order> filterItems = filterOrders(mOrders, FilterType.PREPARE);
                orderViewModel.setFilterOrders(filterItems);
            }
        });
        mBinding.toolbar.tagReady.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                List<Order> filterItems = filterOrders(mOrders, FilterType.READY);
                orderViewModel.setFilterOrders(filterItems);
            }
        });
        mBinding.toolbar.tagPicked.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                List<Order> filterItems = filterOrders(mOrders, FilterType.PICKED);
                orderViewModel.setFilterOrders(filterItems);
            }
        });
        //BottomNavigation Click:
        mBinding.bottomNavigation.menuLinear.setOnClickListener(view -> startActivity(new Intent(requireActivity(), MenuActivity.class)));
        mBinding.bottomNavigation.accountLinear.setOnClickListener(view -> startActivity(new Intent(requireActivity(), MoreActivity.class)));
    }

    public List<Order> filterOrders(List<Order> orderList, FilterType filterType){
        if (orderList == null) return new ArrayList<>();

        List<Order> filteredOrders;
        if(filterType == FilterType.PREPARE){
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() ==  OrderStatus.ORDER_RECEIVED.value() || order.getOrderStatusId() ==  OrderStatus.DELIVERY_GUY_ASSIGNED.value()).collect(Collectors.toList());
        }else if(filterType == FilterType.READY){
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() ==  OrderStatus.ORDER_RECEIVED.value() && order.getOrderStatusId() ==  OrderStatus.DELIVERY_GUY_ASSIGNED.value()).collect(Collectors.toList());
        }else if(filterType == FilterType.PICKED){
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() ==  OrderStatus.ON_THE_WAY.value()).collect(Collectors.toList());
        }else{// ALL
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() !=  OrderStatus.ORDER_PLACED.value()).collect(Collectors.toList());
        }

        return filteredOrders;

    }

}