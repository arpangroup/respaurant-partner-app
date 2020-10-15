package com.example.mainactivity.views.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mainactivity.R;
import com.example.mainactivity.commons.Constants;
import com.example.mainactivity.databinding.FragmentLoginUsingOTPBinding;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.receivers.OtpReceiver;
import com.example.mainactivity.viewmodels.AuthenticationViewModel;
import com.example.mainactivity.views.MainActivity;

import java.util.Locale;

public class LoginUsingOTPFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private static long COUNT_DOWN_TIME = 1 * 60 *1000; // 1 Min;

    private FragmentLoginUsingOTPBinding mBinding;
    AuthenticationViewModel authenticationViewModel;
    NavController navController;

    private long mTimeLeftInMills = COUNT_DOWN_TIME;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //mBinding = FragmentLoginUsingOTPBinding.inflate(inflater, container, false);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_using_o_t_p, null, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        authenticationViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        authenticationViewModel.init();
        initOTPInput();
        startTimer();
        requestPermissions();
        new OtpReceiver().setEditTextOtp(mBinding.et1, mBinding.et2, mBinding.et3, mBinding.et4, mBinding.et5);
        mBinding.layoutLoginWithPassword.setVisibility(View.GONE);
        mBinding.layoutAlert.setVisibility(View.GONE);

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        authenticationViewModel.getPhoneNumber().observe(requireActivity(), phoneNumber -> {
            mBinding.txtPhone.setText(phoneNumber);
        });

        mBinding.loginWithPassword.setOnClickListener(view -> {
            navController.navigate(R.id.action_loginUsingOTPFragment_to_loginUsingPasswordFragment);
        });

        mBinding.txtResend.setOnClickListener(view -> {
            authenticationViewModel.sendLoginOtp(authenticationViewModel.getPhoneNumber().getValue());
            mTimeLeftInMills = COUNT_DOWN_TIME;
            mBinding.txtCounter.setVisibility(View.VISIBLE);
            startTimer();
        });

        //Injecting the viewmodel to the view for data-binding
        mBinding.setAuthViewModel(authenticationViewModel);
    }

    private void requestPermissions(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECEIVE_SMS}, Constants.RECEIVE_SMS_PERMISSION);
        }
    }


    private void initOTPInput() {
        //mBinding.et1.requestFocus();
        //requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mBinding.et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et1.getText().toString().trim().length() == 1){
                    mBinding.et2.requestFocus();
                    isValidOTP();
                }
            }
        });
        mBinding.et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et2.getText().toString().trim().length() == 1){
                    mBinding.et3.requestFocus();
                    isValidOTP();
                }
            }
        });
        mBinding.et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et3.getText().toString().trim().length() == 1){
                    mBinding.et4.requestFocus();
                    isValidOTP();
                }
            }
        });
        mBinding.et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et4.getText().toString().trim().length() == 1){
                    mBinding.et5.requestFocus();
                    isValidOTP();
                }
            }
        });
        mBinding.et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et5.getText().toString().trim().length() == 1){
                    //mBinding.et6.requestFocus();
                    isValidOTP();
                }
            }
        });


        mBinding.et5.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et5.setText(null);
                isValidOTP();
                mBinding.et4.requestFocus();
            }
            return false;

        });
        mBinding.et4.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et4.setText(null);
                isValidOTP();
                mBinding.et3.requestFocus();
            }
            return false;

        });
        mBinding.et3.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et3.setText(null);
                isValidOTP();
                mBinding.et2.requestFocus();
            }
            return false;

        });
        mBinding.et2.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et2.setText(null);
                isValidOTP();
                mBinding.et1.requestFocus();
            }
            return false;

        });

    }
    private boolean isValidOTP(){
        if(mBinding.et1.getText().length()==1 && mBinding.et2.getText().length()==1 && mBinding.et3.getText().length()==1 && mBinding.et4.getText().length()==1 && mBinding.et5.getText().length()==1){
            mBinding.btnVerify.setEnabled(true);
            mBinding.btnVerify.setBackgroundResource(R.drawable.ripple_orange);
            loginNow();
            return true;
        }else{
            mBinding.btnVerify.setEnabled(false);
            mBinding.btnVerify.setBackgroundResource(R.drawable.ripple);
            return false;
        }

    }

    private void loginNow() {
        String otp   = mBinding.et1.getText().toString() + mBinding.et2.getText().toString() + mBinding.et3.getText().toString() + mBinding.et4.getText().toString() + mBinding.et5.getText().toString();
        Log.d(TAG, "Inside loginNow().....");
        String phone = authenticationViewModel.getPhoneNumber().getValue();
        //Address defaultAddress = SettingSession.getDefaultAddress();
        authenticationViewModel.loginByOtp(phone, otp, null).observe(getViewLifecycleOwner(), userLoginResponse -> {
            if(userLoginResponse.isSuccess()){
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
                System.out.println("=========================LOGIN_RESPONSE========================");
                System.out.println(userLoginResponse);
                System.out.println("REQUEST_TOKEN:"+new RequestToken());

            }else{
                mBinding.layoutAlert.setVisibility(View.VISIBLE);
                mBinding.layoutLoginWithPassword.setVisibility(View.GONE);
            }
        });
    }

    private void startTimer(){
        mBinding.txtResend.setEnabled(false);
        new CountDownTimer(mTimeLeftInMills, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMills = millisUntilFinished;
                updateCancelTimer();
            }

            @Override
            public void onFinish() {
                mBinding.txtCounter.setVisibility(View.GONE);
                mBinding.txtResend.setEnabled(true);
            }
        }.start();
    }
    private void updateCancelTimer(){
        int minutes = (int) (mTimeLeftInMills / 1000) /60;// divided by 60 seconds
        int seconds = (int) (mTimeLeftInMills / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds);
        mBinding.txtCounter.setText(timeLeftFormatted);

    }



}