package com.example.mainactivity.views.menuitem;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.DishListAdapter;
import com.example.mainactivity.adapters.ItemCategoryAdapter;
import com.example.mainactivity.adapters.OrderAcceptListAdapter;
import com.example.mainactivity.databinding.FragmentAcceptOrderBinding;
import com.example.mainactivity.databinding.FragmentMenuListBinding;
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.viewmodels.RestaurantViewModel;
import com.example.mainactivity.views.MainActivity;
import com.example.mainactivity.views.MoreActivity;

import java.util.Arrays;
import java.util.List;

public class MenuListFragment extends Fragment implements ItemCategoryAdapter.ItemCategoryInterface{
    private final String TAG = this.getClass().getSimpleName();

    private FragmentMenuListBinding mBinding;
    RestaurantViewModel restaurantViewModel;
    private ItemCategoryAdapter itemCategoryAdapter;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentMenuListBinding.inflate(inflater, container, false);
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

        // Initialize RecyclerView
        itemCategoryAdapter = new ItemCategoryAdapter(this);
        mBinding.menuRecycler.setAdapter(itemCategoryAdapter);

        restaurantViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });

        restaurantViewModel.getRestaurantsMenuItems().observe(getViewLifecycleOwner(), itemCategories -> {
            System.out.println("----------------------------------------------------");
            itemCategories.forEach(itemCategory -> System.out.println(itemCategory));
            System.out.println("----------------------------------------------------");
            itemCategoryAdapter.submitList(itemCategories);
        });


        mBinding.bottomNavigation.orderLinear.setOnClickListener(view -> {
            requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });

        mBinding.bottomNavigation.accountLinear.setOnClickListener(view ->{
            requireActivity().startActivity(new Intent(requireActivity(), MoreActivity.class));
            requireActivity().finish();
        });


    }

    @Override
    public void onMenuItemClickListner(MenuItem menuItem) {
    }

    @Override
    public void onSwitchClickListner(MenuItem menuItem, boolean isActive) {
        Toast.makeText(requireActivity(), "SWITCH CLICKED: "+isActive, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditCategoryListner(ItemCategory itemCategory) {
        restaurantViewModel.setCategory(itemCategory);
        navController.navigate(R.id.action_menuListFragment_to_editCategoryFragment);
    }
}