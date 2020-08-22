package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mainactivity.adapters.OrderListAdapter;
import com.example.mainactivity.databinding.ActivityOrderBinding;
import com.example.mainactivity.viewmodels.OrderViewModel;

public class OrderActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    ActivityOrderBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        //orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);




        mBinding.bottomNavigation.orderLinear.setOnClickListener(view ->{
        });

        mBinding.bottomNavigation.menuLinear.setOnClickListener(view ->{
        });

        mBinding.bottomNavigation.accountLinear.setOnClickListener(view ->{
        });
    }
}