package com.pureeats.restaurant.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.os.Bundle;

import com.pureeats.restaurant.databinding.ActivityMoreBinding;
import com.pureeats.restaurant.viewmodels.RestaurantViewModel;

public class MoreActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    ActivityMoreBinding mBinding;
    NavController navController;
    RestaurantViewModel restaurantViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMoreBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        restaurantViewModel.init();



        restaurantViewModel.loadDashboard();


    }
}