package com.example.mainactivity.views.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mainactivity.adapters.OrderListAdapter;
import com.example.mainactivity.databinding.FragmentPrepareOrderBinding;
import com.example.mainactivity.viewmodels.OrderViewModel;

public class OrderPrepareFragment extends Fragment implements OrderListAdapter.OrderPrepareInterface {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentPrepareOrderBinding mBinding;
    OrderViewModel orderViewModel;
    private OrderListAdapter orderListAdapter;
    private NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPrepareOrderBinding.inflate(inflater, container, false);
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


        orderViewModel.getNewOrders().observe(getViewLifecycleOwner(), orders -> {
            //Log.d(TAG, "ORDER: "+orders.get(0));
            orderListAdapter.submitList(orders);
        });

    }
}