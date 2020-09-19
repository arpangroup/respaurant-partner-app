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

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.OrderHistoryAdapter;
import com.example.mainactivity.adapters.OrderListAdapter;
import com.example.mainactivity.databinding.FragmentOrderHistoryBinding;
import com.example.mainactivity.databinding.FragmentOrderListBinding;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.viewmodels.OrderViewModel;

public class OrderHistoryFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentOrderHistoryBinding mBinding;
    OrderViewModel orderViewModel;
    private OrderHistoryAdapter orderHistoryAdapter;
    private NavController navController;
    private Dashboard mDashboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentOrderHistoryBinding.inflate(inflater, container, false);
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
        mBinding.toolbar.title.setText("Order History");

        // Initialize RecyclerView
        orderHistoryAdapter = new OrderHistoryAdapter();
        mBinding.orderRecycler.setAdapter(orderHistoryAdapter);


//        orderViewModel.getDashboard().observe(requireActivity(), dashboard -> {
//            mDashboard = dashboard;
//            orderViewModel.setFilterOrders(dashboard.getAllOrders());
//        });

//        orderViewModel.getAllOrders().observe(getViewLifecycleOwner(), orders -> {
//            orderHistoryAdapter.submitList(orders);
//        });

        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });



    }
}