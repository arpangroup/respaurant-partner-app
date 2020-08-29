package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.example.mainactivity.R;
import com.example.mainactivity.databinding.ActivityMenuBinding;
import com.example.mainactivity.databinding.ActivityOrderBinding;

public class MenuActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    ActivityMenuBinding mBinding;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        //NavigationUI.setupActionBarWithNavController(this, navController);


        mBinding.bottomNavigation.orderLinear.setOnClickListener(view ->{
            startActivity(new Intent(this, OrderActivity.class));
            finish();
        });

        mBinding.bottomNavigation.accountLinear.setOnClickListener(view ->{
            startActivity(new Intent(this, MoreActivity.class));
            finish();
        });
    }
}