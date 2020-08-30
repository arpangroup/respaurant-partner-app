package com.example.mainactivity.views;

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
import com.example.mainactivity.adapters.MenuItemAdapter;
import com.example.mainactivity.databinding.FragmentEditCategoryBinding;
import com.example.mainactivity.databinding.FragmentEditRestaurantBinding;
import com.example.mainactivity.viewmodels.RestaurantViewModel;

public class EditRestaurantFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentEditRestaurantBinding mBinding;
    RestaurantViewModel restaurantViewModel;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentEditRestaurantBinding.inflate(inflater, container, false);
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

        // Initialize RecyclerView
        //menuItemAdapter = new MenuItemAdapter(this);
    }

}