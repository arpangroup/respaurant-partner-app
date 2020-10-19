package com.pureeats.restaurant.views.order;

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

import com.pureeats.restaurant.adapters.OrderListAdapter;
import com.pureeats.restaurant.databinding.FragmentPrepareOrderBinding;
import com.pureeats.restaurant.models.Order;
import com.pureeats.restaurant.util.CommonUtils;
import com.pureeats.restaurant.viewmodels.OrderViewModel;

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

    @Override
    public void onAutoCancelOrder(Order order) {

    }

    @Override
    public void onOrderReady(int position, int orderId) {

    }

    @Override
    public void onCallToDriver(String mobileNumber) {
        CommonUtils.makePhoneCall(requireActivity(), mobileNumber);
    }
}