package com.pureeats.restaurant.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pureeats.restaurant.api.ApiInterface;
import com.pureeats.restaurant.api.ApiService;
import com.pureeats.restaurant.commons.LoginType;
import com.pureeats.restaurant.models.Address;
import com.pureeats.restaurant.models.User;
import com.pureeats.restaurant.models.request.LoginRequest;
import com.pureeats.restaurant.models.response.ApiResponse;
import com.pureeats.restaurant.models.response.LoginResponse;
import com.pureeats.restaurant.sharedpref.UserSession;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepositoryImpl implements AuthRepository {
    private final String TAG = this.getClass().getSimpleName();
    private static AuthRepositoryImpl authRepository;


    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isPushNotificationAvailable = new MutableLiveData<>(false);
    private MutableLiveData<ApiResponse> sendOtpResponse = new MutableLiveData<>();
    private MutableLiveData<LoginResponse<User>> loginResponse = new MutableLiveData<>();


    public static AuthRepository getInstance(){
        if (authRepository == null){
            authRepository = new AuthRepositoryImpl();
        }
        return authRepository;
    }

    @Override
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Override
    public LiveData<ApiResponse> sendLoginOtp(String phone) {
        if(sendOtpResponse == null){
            sendOtpResponse = new MutableLiveData<>();
        }
        return sendOtp(phone);
    }

    @Override
    public LiveData<LoginResponse<User>> loginByOtp(@NonNull String phone, @NonNull String otp, Address defaultAddress, String pushToken) {
        if(loginResponse == null){
            loginResponse = new MutableLiveData<>();
        }
        LoginRequest loginRequest = new LoginRequest(phone, otp, defaultAddress, LoginType.OTP);
        loginRequest.setPushToken(pushToken);
        return loginNow(loginRequest);
    }

    @Override
    public LiveData<LoginResponse<User>> loginByMobileAndPassword(@NonNull String phone, @NonNull String password, Address defaultAddress, String pushToken) {
        if(loginResponse == null){
            loginResponse = new MutableLiveData<>();
        }
        LoginRequest loginRequest = new LoginRequest(phone, password, defaultAddress, LoginType.MOBILE_AND_PASSWORD);
        loginRequest.setPushToken(pushToken);
        return loginNow(loginRequest);
    }

    @Override
    public LiveData<LoginResponse<User>> getLoginResponse() {
        return loginResponse;
    }

    @Override
    public LiveData<Boolean> isLoggedIn() {
        if(UserSession.isLoggedIn()){
            isLoggedIn.setValue(true);
        }else {
            isLoggedIn.setValue(false);
        }
        return isLoggedIn;
    }

    @Override
    public void logout() {
        isLoading.setValue(true);
        UserSession.logOut();
        isLoggedIn.setValue(false);
        isLoading.setValue(false);
        isPushNotificationAvailable.setValue(false);
        sendOtpResponse = new MutableLiveData<>();
        loginResponse= new MutableLiveData<>();
    }

    @Override
    public void setPushNotificationToken(@NonNull String token) {
        UserSession.setPushNotificationToken(token);
    }

    @Override
    public String getPushNotificationToken() {
        return UserSession.getPushNotificationToken();
    }

    @Override
    public LiveData<Boolean> isPushNotificationTokenAvailable() {
        Boolean result = UserSession.isPushNotificationAvailable();
        isPushNotificationAvailable.setValue(result);
        return isPushNotificationAvailable;
    }


    private LiveData<ApiResponse> sendOtp(String phone){
        isLoading.setValue(true);

        ApiInterface apiInterface = ApiService.getApiService();
        apiInterface.sendLoginOtp(phone).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                try{
                    sendOtpResponse.setValue(response.body());
                }catch (Exception e){
                    Log.d(TAG, "Error inside sendOtp()... method");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return sendOtpResponse;
    }
    private LiveData<LoginResponse<User>> loginNow(LoginRequest loginRequest){
        isLoading.setValue(true);
        Log.d(TAG, "Inside loginNow()......................");
        Log.d(TAG, "LOGIN_REQUEST: "+ new Gson().toJson(loginRequest));
        ApiInterface apiInterface = ApiService.getApiService();
        apiInterface.loginUsingOtp(loginRequest).enqueue(new Callback<LoginResponse<User>>() {
            @Override
            public void onResponse(Call<LoginResponse<User>> call, Response<LoginResponse<User>> response) {
                isLoading.setValue(false);
                try{
                    LoginResponse<User> resp  = response.body();
                    Log.d(TAG, "RESPONSE: "+resp);
                    if (resp != null && resp.isSuccess()) {
                        UserSession.setUserData(resp.getData());
                        isLoggedIn.setValue(true);
                        String pushToken = resp.getData().getPushToken();
                        if(pushToken != null){
                            UserSession.setPushNotificationToken(pushToken);
                        }
                    }
                    loginResponse.setValue(resp);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse<User>> call, Throwable t) {
                Log.d(TAG, "fAIL API Call");
                isLoading.setValue(false);
            }
        });
        return loginResponse;
    }
}
