package com.pureeats.restaurant.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.pureeats.restaurant.models.Address;
import com.pureeats.restaurant.models.User;
import com.pureeats.restaurant.models.response.ApiResponse;
import com.pureeats.restaurant.models.response.LoginResponse;

public interface AuthRepository {
    public LiveData<Boolean> getIsLoading();
    public LiveData<ApiResponse> sendLoginOtp(@NonNull String phone);
    public LiveData<LoginResponse<User>> loginByOtp(@NonNull String phone, @NonNull String otp, Address defaultAddress, String pushNotificationToken);
    public LiveData<LoginResponse<User>> loginByMobileAndPassword(@NonNull String phone, @NonNull String password, Address defaultAddress, String pushNotificationToken);
    public LiveData<LoginResponse<User>> getLoginResponse();
    public LiveData<Boolean> isLoggedIn();
    public void logout();
    public void setPushNotificationToken(@NonNull String token);
    public String getPushNotificationToken();
    public LiveData<Boolean> isPushNotificationTokenAvailable();
}
