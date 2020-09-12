package com.example.mainactivity.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.models.Address;
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.User;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.models.response.LoginResponse;

import java.util.List;

public interface AuthRepository {
    public LiveData<Boolean> getIsLoading();
    public LiveData<ApiResponse> sendLoginOtp(@NonNull String phone);
    public LiveData<LoginResponse<User>> loginByOtp(@NonNull String phone, @NonNull String otp, Address defaultAddress);
    public LiveData<LoginResponse<User>> loginByMobileAndPassword(@NonNull String phone, @NonNull String password, Address defaultAddress);
    public LiveData<LoginResponse<User>> getLoginResponse();
    public void logout();
}
