package com.example.mainactivity.views.account;

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
import com.example.mainactivity.adapters.AccountSectionAdapter;
import com.example.mainactivity.databinding.FragmentAccountListBinding;
import com.example.mainactivity.databinding.FragmentEarningBinding;
import com.example.mainactivity.util.Constants;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.viewmodels.RestaurantViewModel;

public class EarningFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentEarningBinding mBinding;
    RestaurantViewModel restaurantViewModel;
    private AccountSectionAdapter accountSectionAdapter;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentEarningBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        restaurantViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        mBinding.toolbar.title.setText("Business Insights");
        mBinding.toolbar.more.setVisibility(View.GONE);

        mBinding.toolbar.back.setOnClickListener(view -> navController.popBackStack());

        restaurantViewModel.getIsLoading().observe(requireActivity(), isLoading -> {
            if(isLoading)mBinding.layoutProgress.setVisibility(View.VISIBLE);
            else mBinding.layoutProgress.setVisibility(View.GONE);
        });


        restaurantViewModel.loadDashboard();

        restaurantViewModel.getDashboard().observe(requireActivity(),  dashboard -> {
//            //Earnings
//            mBinding.earningToday.setText(Constants.RUPEE_SYMBOL +  dashboard.getTodaysTotalEarning());
//            mBinding.earningYesterday.setText(Constants.RUPEE_SYMBOL +  dashboard.getYesterdaysTotalEarning());
//            mBinding.earningWeekly.setText(Constants.RUPEE_SYMBOL +  dashboard.getOneWeekTotalEarning());
//            mBinding.earningMonthly.setText(Constants.RUPEE_SYMBOL +  dashboard.getOneMonthTotalEarning());
//            //EarningOrders:
//            mBinding.todayTotalOrder.setText(dashboard.getTodaysAllCompletedOrders().size()  + " orders");
//            mBinding.yesterdayTotalOrder.setText(dashboard.getYesterdaysAllCompletedOrders().size()  + " orders");
//            mBinding.weeklyTotalOrder.setText(dashboard.getOneWeekAllCompletedOrders().size()  + " orders");
//            mBinding.monthlyTotalOrder.setText(dashboard.getOneMonthAllCompletedOrder().size()  + " orders");

            mBinding.setDashboard(dashboard);


        });
    }
}