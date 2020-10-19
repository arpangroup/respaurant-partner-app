package com.pureeats.restaurant.views.auth;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.pureeats.restaurant.R;
import com.pureeats.restaurant.databinding.ActivityAuthBinding;
import com.pureeats.restaurant.databinding.ActivityMainBinding;
import com.pureeats.restaurant.models.response.Dashboard;
import com.pureeats.restaurant.viewmodels.AuthenticationViewModel;
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

        authenticationViewModel.isFirebaseTokenAvailable().observe(this, isPushTokenAvailable -> {
            if(!isPushTokenAvailable){
                initFirebaseInstance();
            }
        });

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
    private void initFirebaseInstance(){
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.d(TAG, "PUSH_NOTIFICATION_TOKEN: "+token);
                authenticationViewModel.setFirebaseToken(token);
            }
        });


        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(task -> {
                    String msg = "Successfull";
                    if(!task.isSuccessful())msg = "Failed";
                    //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                });


    }


}