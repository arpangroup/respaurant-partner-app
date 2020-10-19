package com.pureeats.restaurant.views.menuitem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.pureeats.restaurant.R;
import com.pureeats.restaurant.databinding.ActivityMenuBinding;

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

    }
}