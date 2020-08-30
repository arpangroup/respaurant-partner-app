package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import android.content.Intent;
import android.os.Bundle;

import com.example.mainactivity.databinding.ActivityMoreBinding;
import com.example.mainactivity.views.menuitem.MenuActivity;

public class MoreActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    ActivityMoreBinding mBinding;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMoreBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

    }
}