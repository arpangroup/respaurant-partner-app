package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.example.mainactivity.R;
import com.example.mainactivity.databinding.ActivityMenuBinding;
import com.example.mainactivity.databinding.ActivityMoreBinding;

public class MoreActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    ActivityMoreBinding mBinding;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMoreBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.bottomNavigation.orderLinear.setOnClickListener(view ->{
            startActivity(new Intent(this, OrderActivity.class));
            finish();
        });

        mBinding.bottomNavigation.menuLinear.setOnClickListener(view ->{
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        });
    }
}