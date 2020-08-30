package com.example.mainactivity.views.account;

import android.content.Intent;
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
import com.example.mainactivity.adapters.AccountSectionAdapter;
import com.example.mainactivity.adapters.ItemCategoryAdapter;
import com.example.mainactivity.databinding.FragmentAccountListBinding;
import com.example.mainactivity.databinding.FragmentMenuListBinding;
import com.example.mainactivity.models.AccountSection;
import com.example.mainactivity.viewmodels.RestaurantViewModel;
import com.example.mainactivity.views.MainActivity;
import com.example.mainactivity.views.MoreActivity;
import com.example.mainactivity.views.menuitem.MenuActivity;

import java.util.Arrays;
import java.util.List;

public class AccountListFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentAccountListBinding mBinding;
    RestaurantViewModel restaurantViewModel;
    private AccountSectionAdapter accountSectionAdapter;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentAccountListBinding.inflate(inflater, container, false);
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
        initClicks();

        // Initialize RecyclerView
        accountSectionAdapter = new AccountSectionAdapter();
        mBinding.sectionRecycler.setAdapter(accountSectionAdapter);
        List<AccountSection> sections = Arrays.asList(
            new AccountSection(1, "Schedule Restaurant Open", "Schedule off time in advance", R.drawable.ic_baseline_access_time_24),
            new AccountSection(2, "Order History","View all previous orders",  R.drawable.ic_baseline_shopping_cart_24),
            new AccountSection(3, "Earning","View all earnings",  R.drawable.ic_baseline_shopping_cart_24),
            new AccountSection(4, "Support","Raise support ",  R.drawable.ic_baseline_help_outline_24),
            new AccountSection(5, "Logout","Signout from your current account",  R.drawable.ic_baseline_power_settings_new_24)
        );
        accountSectionAdapter.submitList(sections);


    }

    private void initClicks() {

        mBinding.bottomNavigation.orderLinear.setOnClickListener(view ->{
            requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });

        mBinding.bottomNavigation.menuLinear.setOnClickListener(view ->{
            requireActivity().startActivity(new Intent(requireActivity(), MenuActivity.class));
            requireActivity().finish();
        });

        mBinding.layoutRestaurant.setOnClickListener(view -> {
            navController.navigate(R.id.action_accountListFragment_to_editRestaurantFragment);
        });
    }


}