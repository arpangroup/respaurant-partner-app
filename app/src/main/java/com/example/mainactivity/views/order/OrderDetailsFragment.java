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
import com.example.mainactivity.databinding.FragmentOrderDetailsBinding;
import com.example.mainactivity.databinding.FragmentOrderHistoryBinding;
import com.example.mainactivity.models.Dish;
import com.example.mainactivity.util.CommonUtils;
import com.example.mainactivity.util.FormatPrice;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.viewmodels.RestaurantViewModel;

public class OrderDetailsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentOrderDetailsBinding mBinding;
    OrderViewModel orderViewModel;
    RestaurantViewModel restaurantViewModel;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentOrderDetailsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        orderViewModel.init();
        restaurantViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        mBinding.toolbar.title.setText("ORDER  SUMMARY");

        mBinding.toolbar.back.setOnClickListener(view -> navController.popBackStack());

        orderViewModel.getOrderDetails().observe(requireActivity(), order -> {
            // Calculate total price:
//            double itemTotal = 0.0;
//            for(Dish dish : order.getOrderitems()){
//                double price = Double.parseDouble(dish.getPrice());
//                itemTotal += (price * dish.getQuantity());
//            }
//            order.setItemTotal(FormatPrice.formatDecimalPoint(itemTotal));
//
//            if(order.getCoupon_name() != null){
//                double discountAmount = CommonUtils.getCouponAmount(order.getCouponDetails(), order.getTotal());
//                order.setDiscountAmount(FormatPrice.format(discountAmount));
//            }

            mBinding.setOrder(order);
        });
    }
}