package com.example.mainactivity.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.OrderAcceptListAdapter;
import com.example.mainactivity.adapters.OrderListAdapter;
import com.example.mainactivity.databinding.FragmentAcceptOrderBinding;
import com.example.mainactivity.databinding.FragmentOrderListBinding;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.viewmodels.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment implements OrderListAdapter.OrderPrepareInterface {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentOrderListBinding mBinding;
    OrderViewModel orderViewModel;
    private OrderListAdapter orderListAdapter;
    private NavController navController;

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

        // Initialize NavController
        navController = Navigation.findNavController(view);

        // Initialize RecyclerView
        orderListAdapter = new OrderListAdapter(this);
        mBinding.orderRecycler.setAdapter(orderListAdapter);

        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });

        orderViewModel.getAllOrders().observe(getViewLifecycleOwner(), orders -> {
            orderListAdapter.submitList(orders);
        });


    }
}