package com.pureeats.restaurant.views.menuitem;

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

import com.pureeats.restaurant.R;
import com.pureeats.restaurant.adapters.ItemCategoryAdapter;
import com.pureeats.restaurant.adapters.MenuItemAdapter;
import com.pureeats.restaurant.databinding.FragmentEditCategoryBinding;
import com.pureeats.restaurant.databinding.FragmentEditItemBinding;
import com.pureeats.restaurant.models.ItemCategory;
import com.pureeats.restaurant.models.MenuItem;
import com.pureeats.restaurant.viewmodels.RestaurantViewModel;
import com.google.android.material.snackbar.Snackbar;

public class EditCategoryFragment extends Fragment implements ItemCategoryAdapter.ItemCategoryInterface {

    private final String TAG = this.getClass().getSimpleName();

    private FragmentEditCategoryBinding mBinding;
    RestaurantViewModel restaurantViewModel;
    private MenuItemAdapter menuItemAdapter;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentEditCategoryBinding.inflate(inflater, container, false);
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

        mBinding.toolbar.back.setOnClickListener(view1 -> navController.popBackStack());

        // Initialize RecyclerView
        menuItemAdapter = new MenuItemAdapter(this);
        restaurantViewModel.getSelectedCategory().observe(getViewLifecycleOwner(), itemCategory -> {
            mBinding.toolbar.title.setText(itemCategory.getName());
            menuItemAdapter.submitList(itemCategory.getMenuItems());
            mBinding.menuRecycler.setAdapter(menuItemAdapter);
        });
    }

    @Override
    public void onMenuItemClickListener(MenuItem menuItem) {
        restaurantViewModel.setMenuItem(menuItem);
        navController.navigate(R.id.action_editCategoryFragment_to_editItemFragment);
    }

    @Override
    public void onSwitchClickListener(MenuItem menuItem, boolean isActive) {
        restaurantViewModel.toggleMenuItem(menuItem.getId()).observe(getViewLifecycleOwner(), apiResponse -> {
            Snackbar.make(requireView(), apiResponse.getMessage(), Snackbar.LENGTH_LONG).show();
        });
    }

    @Override
    public void onEditCategoryListener(ItemCategory itemCategory) {

    }
}