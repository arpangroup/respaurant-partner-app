package com.pureeats.restaurant.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.pureeats.restaurant.R;
import com.pureeats.restaurant.sharedpref.UserSession;
import com.pureeats.restaurant.viewmodels.AuthenticationViewModel;
import com.pureeats.restaurant.views.auth.AuthActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    private ImageView logo;
    private TextView slogan;

    Animation transitionAnimation;
    Animation topAnimation;
    Animation bottomAnimation;

    AuthenticationViewModel authenticationViewModel;
    private UserSession userSession;
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make the screen as  full-screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_splash_screen);
        userSession = new UserSession(this);

        // Initialize ViewModel
        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        authenticationViewModel.init();

        initializeWidgets();

        new Handler().postDelayed(() -> processActivity(), SPLASH_TIME_OUT);


    }

    private void initializeWidgets() {
        //Initialize Animations:
        transitionAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_transition);
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        // Initialize the widgets:
        logo = (ImageView)findViewById(R.id.iv);
        slogan = (TextView)findViewById(R.id.tv);

        slogan.startAnimation(transitionAnimation);

    }

    private void processActivity() {
        goToNextActivity();
        /*
        // Step1: Check if config is missing or not
        // If one config is missing then call API to fetch settings
        if(!SettingSession.isSettingAvailable()){
            // i.e., Config is missing, hence call API
            getSettingsFromApi();
            return;
        }else{
            // i.e., Config is available, goto next activity
            goToNextActivity();
            return;
        }

         */
    }

    private void goToNextActivity(){
        //startActivity(new Intent(this, AuthActivity.class));
        //finish();
        authenticationViewModel.isLoggedIn().observe(this, isLoggedIn -> {
            if(isLoggedIn){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }else{
                startActivity(new Intent(this, AuthActivity.class));
                finish();
            }
        });
    }
}
