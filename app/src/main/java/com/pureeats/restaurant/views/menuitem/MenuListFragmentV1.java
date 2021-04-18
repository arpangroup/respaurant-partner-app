package com.pureeats.restaurant.views.menuitem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.pureeats.restaurant.R;
import com.pureeats.restaurant.adapters.ItemCategoryAdapter;
import com.pureeats.restaurant.adapters.MenuItemAdapter;
import com.pureeats.restaurant.databinding.FragmentMenuListBinding;
import com.pureeats.restaurant.models.ItemCategory;
import com.pureeats.restaurant.models.MenuItem;
import com.pureeats.restaurant.util.DelayedOnQueryTextListener;
import com.pureeats.restaurant.viewmodels.RestaurantViewModel;
import com.pureeats.restaurant.views.MainActivity;
import com.pureeats.restaurant.views.MoreActivity;

import java.util.ArrayList;
import java.util.List;

public class MenuListFragmentV1 extends Fragment implements ItemCategoryAdapter.ItemCategoryInterface{
    private final String TAG = this.getClass().getSimpleName();

    private FragmentMenuListBinding mBinding;
    RestaurantViewModel restaurantViewModel;
    //private ItemCategoryAdapter itemCategoryAdapter;
    private MenuItemAdapter menuItemAdapter;
    private NavController navController;
    private List<ItemCategory> mItemCategoryList;

    List<MenuItem> mMenuItems = new ArrayList<>();

    private static enum FilterTpe {
        ALL,
        OUT_OF_STACK
    }


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
        initClicks();

        // Initialize RecyclerView
        //itemCategoryAdapter = new ItemCategoryAdapter(this);
        menuItemAdapter = new MenuItemAdapter(new ItemCategoryAdapter.ItemCategoryInterface() {
            @Override
            public void onMenuItemClickListener(MenuItem menuItem) {
                // skip...
            }

            @Override
            public void onSwitchClickListener(MenuItem menuItem, boolean isActive) {
                Log.d(TAG, "onSwitchClickListener: " + menuItem);
                restaurantViewModel.toggleMenuItem(menuItem.getId()).observe(getViewLifecycleOwner(), apiResponse -> {
                    refresh();
                });
            }

            @Override
            public void onEditCategoryListener(ItemCategory itemCategory) {
                //restaurantViewModel.setCategory(itemCategory);
                //navController.navigate(R.id.action_menuListFragment_to_editCategoryFragment);
            }
        });

        mBinding.menuRecycler.setAdapter(menuItemAdapter);

        restaurantViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
//            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
//            else mBinding.progressbar.setVisibility(View.GONE);
        });

        restaurantViewModel.getRestaurantsMenuItems().observe(getViewLifecycleOwner(), itemCategories -> {
            mItemCategoryList = itemCategories;
            restaurantViewModel.setFilterMenus(mItemCategoryList);
        });

        restaurantViewModel.getAllFilteredMenus().observe(getViewLifecycleOwner(), categoryList -> {
            mBinding.toolbar.radiobuttonAllItems.setText("All Items (" + getMenuItemSize(mItemCategoryList) +")");

            List<ItemCategory> outOfStockItems = filterItems(categoryList,  FilterTpe.OUT_OF_STACK);
            mBinding.toolbar.radiobuttonOutOfStock.setText("Out of Stock (" + getOutOfStockItemSize(outOfStockItems) +")");


            List<MenuItem> menuItems = new ArrayList<>();
            categoryList.forEach(itemCategory -> {
                menuItems.addAll(itemCategory.getMenuItems());
            });
            mMenuItems = menuItems;

            //itemCategoryAdapter.submitList(categoryList);
            menuItemAdapter.submitList(menuItems);
        });

        mBinding.swiperefreshLayout.setOnRefreshListener(() -> {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(mBinding.swiperefreshLayout.isRefreshing()) {
                    mBinding.swiperefreshLayout.setRefreshing(false);
                }
            }, 1000);
        });

        mBinding.toolbar.searchView.clearFocus();
        mBinding.toolbar.searchView.setQuery("", false);
        mBinding.toolbar.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return false;
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.toolbar.searchView.setQuery("", false);
        mBinding.getRoot().requestFocus();
    }

    private void performSearch(String query){
        //menuItemAdapter.getFilter().filter(query);
        Log.d(TAG, "performSearch: " + query);
        List<MenuItem> filteredList = new ArrayList<>();
        if(TextUtils.isEmpty(query)){
            // Clear all the items
           filteredList.addAll(mMenuItems);
        }else {
            mMenuItems.forEach(menuItem -> {
                if(menuItem.getName().toLowerCase().contains(query.toLowerCase())){
                    filteredList.add(menuItem);
                }
            });
        }
        menuItemAdapter.submitList(filteredList);
    }

    private void initClicks(){
        //Toolbar Click:
        mBinding.toolbar.radiobuttonAllItems.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                List<ItemCategory> filterItems = filterItems(mItemCategoryList, FilterTpe.ALL);
                //mBinding.toolbar.radiobuttonAllItems.setText("All Items (" + getMenuItemSize(filterItems) +")");
                restaurantViewModel.setFilterMenus(filterItems);
            }
        });
        mBinding.toolbar.radiobuttonOutOfStock.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                List<ItemCategory> filterItems = filterItems(mItemCategoryList, FilterTpe.OUT_OF_STACK);
                //mBinding.toolbar.radiobuttonOutOfStock.setText("Out of Stock (" + getMenuItemSize(filterItems) +")");
                restaurantViewModel.setFilterMenus(filterItems);
            }
        });
        //BottomNavigation Click:
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
    public void onMenuItemClickListener(MenuItem menuItem) {
    }

    @Override
    public void onSwitchClickListener(MenuItem menuItem, boolean isActive) {
        restaurantViewModel.toggleMenuItem(menuItem.getId()).observe(getViewLifecycleOwner(), apiResponse -> {
            refresh();
        });

    }

    @Override
    public void onEditCategoryListener(ItemCategory itemCategory) {
        restaurantViewModel.setCategory(itemCategory);
        navController.navigate(R.id.action_menuListFragment_to_editCategoryFragment);
    }

    private void refresh(){
        Log.d(TAG,  "refreshing.......");
        //restaurantViewModel.getRestaurantsMenuItems();
    }

    private List<ItemCategory> filterItems(List<ItemCategory> categoryList,  FilterTpe  filterTpe){
        List<ItemCategory> filterCategories = new ArrayList<>();
        if (filterTpe == FilterTpe.ALL)filterCategories = categoryList;
        if(filterTpe == FilterTpe.OUT_OF_STACK){
            // ie., filter items, which are disabled
            List<ItemCategory> categories = new ArrayList<>();
            categoryList.forEach(itemCategory -> {
                itemCategory.getMenuItems().forEach(menuItem -> {
                    if(menuItem.getIsActive()  == 0) categories.add(itemCategory);
                });
            });
            filterCategories = categories;
        }
        return filterCategories;
    }

    private int getMenuItemSize(List<ItemCategory> categoryList){
        return categoryList.stream().mapToInt(itemCategory -> itemCategory.getMenuItems().size()).sum();
    }

    private int getOutOfStockItemSize(List<ItemCategory> categoryList){
       long count = 0;
       count = categoryList.stream()
               .mapToInt(itemCategory -> itemCategory.getMenuItems().stream().filter(menuItem -> menuItem.getIsActive()==0).mapToInt(MenuItem::getId).sum())
               .count();
       return (int)count;
    }

}