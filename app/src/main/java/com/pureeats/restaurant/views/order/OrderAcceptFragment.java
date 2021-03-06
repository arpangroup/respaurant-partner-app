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

import com.pureeats.restaurant.adapters.OrderAcceptListAdapter;
import com.pureeats.restaurant.databinding.FragmentAcceptOrderBinding;
import com.pureeats.restaurant.databinding.ItemOrderAcceptBinding;
import com.pureeats.restaurant.models.Order;
import com.pureeats.restaurant.viewmodels.OrderViewModel;

import java.util.List;

public class OrderAcceptFragment extends Fragment implements OrderAcceptListAdapter.OrderAcceptInterface {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentAcceptOrderBinding mBinding;
    OrderViewModel orderViewModel;
    private OrderAcceptListAdapter orderAcceptListAdapter;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentAcceptOrderBinding.inflate(inflater, container, false);
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
        orderAcceptListAdapter = new OrderAcceptListAdapter(this);
        mBinding.orderRecycler.setAdapter(orderAcceptListAdapter);

        orderViewModel.getNewOrders().observe(getViewLifecycleOwner(), orders -> {
            //Log.d(TAG, "ORDER: "+orders.get(0));
            orderAcceptListAdapter.submitList(orders);
        });

        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onIncreasePreparationTime(Order order) {
        int preparationTime = Integer.parseInt(order.getRestaurant().getDeliveryTime());
        preparationTime += 1;
        orderViewModel.changeDeliveryTimeOfNotAcceptedOrder(order.getId(), preparationTime);
    }

    @Override
    public void onDecreasePreparationTime(Order order) {
        int preparationTime = Integer.parseInt(order.getRestaurant().getDeliveryTime());
        preparationTime -= 1;
        if(preparationTime < 1) preparationTime = 0;
        orderViewModel.changeDeliveryTimeOfNotAcceptedOrder(order.getId(), preparationTime);
    }

    @Override
    public void onRejectClick(Order order, ItemOrderAcceptBinding binding) {
        mBinding.progressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAcceptClick(Order order, ItemOrderAcceptBinding binding) {
//        orderViewModel.acceptOrder(order.getId()).observe(getViewLifecycleOwner(), apiResponse -> {
//            if(apiResponse.isSuccess()){
//                navController.navigate(R.id.action_acceptOrderFragment_to_orderPrepareFragment);
//            }
//        });
        //navController.navigate(R.id.action_acceptOrderFragment_to_orderPrepareFragment);
        //((OrderActivity)requireActivity()).changeToolbarTag(2);
    }

    @Override
    public void onAutoCancelOrder(Order order) {

    }

    @Override
    public void onDialogDismiss(List<Order> notAcceptedOrders) {

    }
}