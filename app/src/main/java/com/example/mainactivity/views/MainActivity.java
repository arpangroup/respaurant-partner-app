package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.example.mainactivity.R;
import com.example.mainactivity.commons.OrderStatus;
import com.example.mainactivity.databinding.ActivityMainBinding;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.viewmodels.OrderViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityMainBinding mBinding;
    OrderViewModel orderViewModel;
    NavController navController;

    Dashboard mDashboard = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.init();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        //NavigationUI.setupActionBarWithNavController(this, navController);

    }

}