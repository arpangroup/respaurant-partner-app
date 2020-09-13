package com.example.mainactivity.views.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mainactivity.R;
import com.example.mainactivity.databinding.ActivityAuthBinding;
import com.example.mainactivity.databinding.ActivityMainBinding;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.viewmodels.AuthenticationViewModel;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.views.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class AuthActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityAuthBinding mBinding;
    AuthenticationViewModel authenticationViewModel;
    NavController navController;

    Dashboard mDashboard = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        authenticationViewModel.init();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        //NavigationUI.setupActionBarWithNavController(this, navController);

//        authenticationViewModel.isLoggedIn().observe(this, isLoggedIn -> {
//            if(isLoggedIn){
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//            }else{
//                startActivity(new Intent(this, AuthActivity.class));
//                finish();
//            }
//        });
    }

}