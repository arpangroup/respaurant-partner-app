package com.example.mainactivity.views.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mainactivity.R;
import com.example.mainactivity.databinding.ActivityAuthBinding;
import com.example.mainactivity.databinding.ActivityMainBinding;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.viewmodels.OrderViewModel;

public class AuthActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityAuthBinding mBinding;
    OrderViewModel orderViewModel;
    NavController navController;

    Dashboard mDashboard = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.init();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        //NavigationUI.setupActionBarWithNavController(this, navController);

    }

}