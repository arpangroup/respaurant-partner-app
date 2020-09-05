package com.example.mainactivity.views.menuitem;

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
import com.example.mainactivity.adapters.ItemCategoryAdapter;
import com.example.mainactivity.databinding.FragmentEditItemBinding;
import com.example.mainactivity.databinding.FragmentMenuListBinding;
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.viewmodels.RestaurantViewModel;

public class EditItemFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentEditItemBinding mBinding;
    RestaurantViewModel restaurantViewModel;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentEditItemBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        restaurantViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(view);

        restaurantViewModel.getMenuItem().observe(getViewLifecycleOwner(), menuItem -> {
            ItemCategory category = restaurantViewModel.getSelectedCategory().getValue();
            mBinding.toolbar.title.setText(menuItem.getName());
            mBinding.etItemName.setText(menuItem.getName());
            mBinding.etDescription.setText(menuItem.getDesc());
            mBinding.etMarkPrice.setText(menuItem.getOldPrice());
            mBinding.etSellingPrice.setText(menuItem.getPrice());
            mBinding.etCategory.setText(category.getName());
            mBinding.image.setImageResource(R.drawable.foodimg_1);
            mBinding.switchRecommended.setChecked(menuItem.getIsRecommended() == 1);
            mBinding.switchPopular.setChecked(menuItem.getIsPopular() == 1);
            mBinding.switchNew.setChecked(menuItem.getIsNew() == 1);
            mBinding.switchVeg.setChecked(menuItem.getIsVeg() == 1);
        });
    }
}