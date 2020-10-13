package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mainactivity.databinding.ActivityMoreBinding;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.viewmodels.RestaurantViewModel;
import com.example.mainactivity.views.menuitem.MenuActivity;

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