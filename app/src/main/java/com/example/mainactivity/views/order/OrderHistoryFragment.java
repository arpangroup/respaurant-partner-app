package com.example.mainactivity.views.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.OrderHistoryAdapter;
import com.example.mainactivity.adapters.OrderListAdapter;
import com.example.mainactivity.databinding.FragmentOrderHistoryBinding;
import com.example.mainactivity.databinding.FragmentOrderListBinding;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.viewmodels.RestaurantViewModel;

import java.util.Collections;

public class OrderHistoryFragment extends Fragment implements OrderHistoryAdapter.OrderHistoryInterface{
    private final String TAG = this.getClass().getSimpleName();

    private FragmentOrderHistoryBinding mBinding;
    OrderViewModel orderViewModel;
    RestaurantViewModel restaurantViewModel;
    private OrderHistoryAdapter orderHistoryAdapter;
    private NavController navController;
    private Dashboard mDashboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentOrderHistoryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.init();
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        restaurantViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        mBinding.toolbar.title.setText("Order History");

        // Initialize RecyclerView
        orderHistoryAdapter = new OrderHistoryAdapter(this);
        mBinding.orderRecycler.setAdapter(orderHistoryAdapter);

        mBinding.toolbar.back.setOnClickListener(view -> navController.popBackStack());


        restaurantViewModel.loadDashboard();

        restaurantViewModel.getDashboard().observe(requireActivity(), dashboard -> {
            Log.d(TAG, "HISTORY_SIZE: "+dashboard.getAllOrders().size());
            orderHistoryAdapter.submitList(dashboard.getAllOrders());
            orderHistoryAdapter.notifyDataSetChanged();
        });

        restaurantViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            Log.d(TAG, "Inside isLoading.......................");
            Log.d(TAG, "VALUE: " + aBoolean);
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });

        mBinding.swiperefreshLayout.setOnRefreshListener(() -> {
            restaurantViewModel.loadDashboard();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(mBinding.swiperefreshLayout.isRefreshing()) {
                    mBinding.swiperefreshLayout.setRefreshing(false);
                }
            }, 1000);
        });





    }

    @Override
    public void onOrderHistoryClick(Order order) {
        Log.d(TAG, "ORDER:  "+order);
        orderViewModel.setOrderDetails(order);
        navController.navigate(R.id.action_orderHistoryFragment_to_orderDetailsFragment);
    }
}