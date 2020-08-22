package com.example.mainactivity.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.OrderListAdapter;
import com.example.mainactivity.databinding.FragmentAcceptOrderBinding;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.viewmodels.OrderViewModel;

public class AcceptOrderFragment extends Fragment implements OrderListAdapter.OrderInterface {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentAcceptOrderBinding mBinding;
    OrderViewModel orderViewModel;
    private OrderListAdapter orderListAdapter;


    public AcceptOrderFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentAcceptOrderBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        orderListAdapter  = new OrderListAdapter(this);
        mBinding.orderRecycler.setAdapter(orderListAdapter);


        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.init();


        orderViewModel.getNewOrders().observe(getViewLifecycleOwner(), orders -> {
            //Log.d(TAG, "ORDER: "+orders.get(0));
            orderListAdapter.submitList(orders);
        });

        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onIncreasePreparationTime(Order order) {
        Toast.makeText(getActivity(), "PLUS CLICKED", Toast.LENGTH_SHORT).show();
        int preparationTime = Integer.parseInt(order.getRestaurant().getDeliveryTime());
        preparationTime += 1;
        Toast.makeText(getActivity(), preparationTime+"", Toast.LENGTH_SHORT).show();
        orderViewModel.changeDeliveryTime(order);
    }

    @Override
    public void onDecreasePreparationTime(Order order) {
        int preparationTime = Integer.parseInt(order.getRestaurant().getDeliveryTime());
        preparationTime -= 1;
        if(preparationTime < 1) preparationTime = 0;
        Toast.makeText(getActivity(), preparationTime+"", Toast.LENGTH_SHORT).show();
        orderViewModel.changeDeliveryTime(order);
    }

    @Override
    public void onRejectClick(Order order) {
        Toast.makeText(getActivity(), "REJECT CLICKED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAcceptClick(Order order) {
        Toast.makeText(getActivity(), "ACCEPT CLICKED", Toast.LENGTH_SHORT).show();
    }
}