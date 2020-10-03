package com.example.mainactivity.views.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.mainactivity.adapters.OrderListAdapter;
import com.example.mainactivity.commons.OrderStatus;
import com.example.mainactivity.databinding.FragmentOrderListBinding;
import com.example.mainactivity.firebase.MessagingService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.views.menuitem.MenuActivity;
import com.example.mainactivity.views.MoreActivity;
import com.google.gson.Gson;

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

    private static enum FilterType {
        ALL,
        PREPARE,
        READY,
        PICKED
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(mReceiver, new IntentFilter(MessagingService.MESSAGE_ORDER_STATUS));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                String ordersJson = intent.getStringExtra(MessagingService.INTENT_EXTRA_ORDER_STATUS);
                System.out.println("==================RECEIVED==========================");
                System.out.println(ordersJson);
                System.out.println("====================================================");
                Order order = new Gson().fromJson(ordersJson, Order.class);
                //Toast.makeText(context, "ID: "+orderObj.getId(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, " New order received", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "RECEIVER TRIGGERED", Toast.LENGTH_SHORT).show();

                boolean isStatusChanged = orderViewModel.setStatusChange(order);
                if(isStatusChanged) orderListAdapter.notifyDataSetChanged();
                mBinding.toolbar.tagAll.performClick();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context, "RECEIVER: EXCEPTION", Toast.LENGTH_SHORT).show();
            }
        }
    };

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

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        initClicks();
        handleSearch();

        // Initialize RecyclerView
        orderListAdapter = new OrderListAdapter(this);
        mBinding.orderRecycler.setAdapter(orderListAdapter);

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

    private void handleSearch() {
        //title_frame, searchView
        mBinding.toolbar.btnSearch.setOnClickListener(view -> {
            mBinding.toolbar.titleFrame.setVisibility(View.GONE);
            mBinding.toolbar.searchView.setVisibility(View.VISIBLE);
            mBinding.toolbar.searchClose.setVisibility(View.GONE);
            mBinding.toolbar.txtSearch.requestFocus();
        });

        mBinding.toolbar.searchBack.setOnClickListener(view -> {
            mBinding.toolbar.txtSearch.setText("");
            mBinding.toolbar.titleFrame.setVisibility(View.VISIBLE);
            mBinding.toolbar.searchView.setVisibility(View.GONE);
            mBinding.toolbar.txtSearch.clearFocus();
            orderListAdapter.getFilter().filter("");
        });
        mBinding.toolbar.searchClose.setOnClickListener(view -> {
            mBinding.toolbar.txtSearch.setText(null);
            mBinding.toolbar.searchClose.setVisibility(View.GONE);
            orderListAdapter.getFilter().filter("");
        });
        mBinding.toolbar.txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence input, int start, int before, int count) {
                if(input.length() != 0){
                    mBinding.toolbar.searchClose.setVisibility(View.VISIBLE);
                    orderListAdapter.getFilter().filter(input);
                }else{
                    mBinding.toolbar.searchClose.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mBinding.toolbar.txtSearch.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }else{
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });
    }

    @Override
    public void onAutoCancelOrder(Order order) {
        Toast.makeText(requireActivity(), "AUTO_CANCEL_ORDER: "+order.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderReady(int position, int orderId) {
        orderViewModel.makeOrderAsReady(orderId).observe(requireActivity(), apiResponse -> {
            if(apiResponse.isSuccess()){
                orderListAdapter.updateStatus(position, OrderStatus.ORDER_READY.value());
            }
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
            filteredOrders = orderList
                    .stream()
                    .filter(order -> order.getOrderStatusId() ==  OrderStatus.ORDER_READY.value()).collect(Collectors.toList());
        }else if(filterType == FilterType.PICKED){
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() ==  OrderStatus.ON_THE_WAY.value()).collect(Collectors.toList());
        }else{// ALL
            filteredOrders = orderList.stream().filter(order -> order.getOrderStatusId() !=  OrderStatus.ORDER_PLACED.value()).collect(Collectors.toList());
        }

        return filteredOrders;

    }

}