package com.pureeats.restaurant.views.order;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.pureeats.restaurant.R;
import com.pureeats.restaurant.adapters.OrderListAdapter;
import com.pureeats.restaurant.commons.NotificationSoundType;
import com.pureeats.restaurant.commons.OrderStatus;
import com.pureeats.restaurant.databinding.FragmentOrderListBinding;
import com.pureeats.restaurant.models.Order;
import com.pureeats.restaurant.util.CommonUtils;
import com.pureeats.restaurant.viewmodels.OrderViewModel;
import com.pureeats.restaurant.views.menuitem.MenuActivity;
import com.pureeats.restaurant.views.MoreActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment implements OrderListAdapter.OrderPrepareInterface {
    private final String TAG = this.getClass().getSimpleName();

    private MediaPlayer mMediaPlayer;
    private FragmentOrderListBinding mBinding;
    OrderViewModel orderViewModel;
    private OrderListAdapter orderListAdapter;
    private NavController navController;
    //private Dashboard mDashboard;
    private List<Order> mOrders =  new ArrayList<>();

    public static enum FilterType {
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

        orderViewModel.getRunningOrderStatus().observe(requireActivity(), fetchedOrders -> {
            // First get all accepted orders
            List<Order> acceptedOrders = orderViewModel.getAllAcceptedOrders().getValue();
            if(acceptedOrders != null){
                acceptedOrders.forEach(acceptedOrder -> {
                    fetchedOrders.forEach(fetchedOrder -> {
                        if(acceptedOrder.getId()  == fetchedOrder.getId()){
                            if(acceptedOrder.getOrderStatusId() != fetchedOrder.getOrderStatusId()){// i.e, status changed
                                handleOrderStatusChanged(fetchedOrder);
                            }
                        }
                    });
                });
            }

        });


        orderViewModel.getAllAcceptedOrders().observe(requireActivity(), orders -> {
            mOrders = orders;
            mBinding.toolbar.tagAll.setText("All ("+orders.size() +")");

            handleFilterItemCount(mOrders);

            orderListAdapter.submitList(new ArrayList<>());
            orderListAdapter.submitList(orders);

            new Handler().postDelayed(() -> mBinding.orderRecycler.scrollToPosition(0), 300);


            //Log.d(TAG, "#########################Scroll to top..........");
            //RecyclerView.LayoutManager layoutManager  = mBinding.orderRecycler.getLayoutManager();
            //mBinding.orderRecycler.getLayoutManager().scrollToPosition(-1);
            //layoutManager.scrollToPositionWithOffset(0, -2);
            //layoutManager.smoothScrollToPosition(mBinding.orderRecycler, null, 0);
            //mBinding.orderRecycler.scrollToPosition(0);
            //mBinding.orderRecycler.refreshDrawableState();
//            if (layoutManager != null) {
//                layoutManager.smoothScrollToPosition(mBinding.orderRecycler, new RecyclerView.State(), 0);
//            }
        });

        orderViewModel.getCurrentFilterType().observe(requireActivity(), filterType -> {
            Log.d(TAG, "CURRENT_FILTER_TYPE: " + filterType.name());
            Log.d(TAG, "Orders: " + mOrders);
            Log.d(TAG, "ORDER_SIZE: " + mOrders.size());
            //orderListAdapter.submitList(mOrders);
            orderListAdapter.getFilter().filter(filterType.name());
            new Handler().postDelayed(() -> mBinding.orderRecycler.scrollToPosition(0), 300);
            orderListAdapter.notifyDataSetChanged();
        });
    }

    private void handleFilterItemCount(List<Order> mOrders) {
        List<Order> preparingOrders = OrderListAdapter.filterOrders(mOrders,  FilterType.PREPARE);
        List<Order> readyOrders = OrderListAdapter.filterOrders(mOrders,  FilterType.READY);
        List<Order> pickedUpOrders = OrderListAdapter.filterOrders(mOrders,  FilterType.PICKED);

        mBinding.toolbar.tagAll.setText("All (" + mOrders.size() +")");
        mBinding.toolbar.tagPreparing.setText("Preparing (" + preparingOrders.size() +")");
        mBinding.toolbar.tagReady.setText("Ready (" + readyOrders.size() +")");
        mBinding.toolbar.tagPicked.setText("PickedUp (" + pickedUpOrders.size() +")");
    }

    private void handleOrderStatusChanged(Order order) {
        OrderStatus orderStatus = CommonUtils.mapOrderStatus(order.getOrderStatusId());
        orderViewModel.setStatusChange(order);
        if(orderStatus == OrderStatus.DELIVERED){
            CommonUtils.showPushNotification(requireActivity(), "Order Delivered", "Order " +order.getUniqueOrderId()+ " has been delivered successfully");
            startMediaPlayer(NotificationSoundType.ORDER_DELIVERED);

        }else if(orderStatus == OrderStatus.CANCELED){
            startMediaPlayer(NotificationSoundType.ORDER_CANCELED);
        }else{
            //Toast.makeText(requireActivity(), "SOMETHING ERROR HAPPENED", Toast.LENGTH_SHORT).show();
        }




        orderViewModel.getCurrentFilterType().observe(requireActivity(), filterType -> {
            Log.d(TAG, "CURRENT_FILTER_TYPE: " + filterType.name());
            Log.d(TAG, "Orders: " + mOrders);
            Log.d(TAG, "ORDER_SIZE: " + mOrders.size());
            //orderListAdapter.submitList(mOrders);
            orderListAdapter.getFilter().filter(filterType.name());
        });

        new Handler().postDelayed(() -> mBinding.orderRecycler.scrollToPosition(0), 300);
        orderListAdapter.notifyDataSetChanged();
        handleFilterItemCount(mOrders);
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
                handleFilterItemCount(mOrders);
            }
        });
    }

    @Override
    public void onCallToDriver(String mobileNumber) {
        CommonUtils.makePhoneCall(requireActivity(), mobileNumber);
    }

    private void initClicks() {
        //Toolbar Click:
        mBinding.toolbar.tagAll.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                orderViewModel.setFilterType(FilterType.ALL);
            }
        });
        mBinding.toolbar.tagPreparing.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                orderViewModel.setFilterType(FilterType.PREPARE);
            }
        });
        mBinding.toolbar.tagReady.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                orderViewModel.setFilterType(FilterType.READY);
            }
        });
        mBinding.toolbar.tagPicked.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                orderViewModel.setFilterType(FilterType.PICKED);
            }
        });
        //BottomNavigation Click:
        mBinding.bottomNavigation.menuLinear.setOnClickListener(view -> startActivity(new Intent(requireActivity(), MenuActivity.class)));
        mBinding.bottomNavigation.accountLinear.setOnClickListener(view -> startActivity(new Intent(requireActivity(), MoreActivity.class)));
    }






    private void startMediaPlayer(NotificationSoundType soundType) {
        mMediaPlayer = new MediaPlayer();
        Context context = requireActivity();
        if(soundType == NotificationSoundType.ORDER_ARRIVE)mMediaPlayer = MediaPlayer.create(context, R.raw.order_arrived_ringtone);
        else if(soundType == NotificationSoundType.ORDER_CANCELED)mMediaPlayer = MediaPlayer.create(context, R.raw.order_cancel_ringtone);
        else mMediaPlayer = MediaPlayer.create(context, R.raw.default_notification_sound);
        try{
            mMediaPlayer.start();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }
    private void stopMediaPlayer(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMediaPlayer();
    }



}