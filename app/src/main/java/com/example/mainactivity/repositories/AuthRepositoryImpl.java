package com.example.mainactivity.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.models.Address;
import com.example.mainactivity.models.User;
import com.example.mainactivity.models.request.LoginRequest;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.models.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepositoryImpl implements AuthRepository {
    private final String TAG = this.getClass().getSimpleName();
    private static AuthRepositoryImpl authRepository;


    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>(false);
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
    public LiveData<LoginResponse<User>> loginByOtp(String phone, String otp, Address defaultAddress) {
        if(loginResponse == null){
            loginResponse = new MutableLiveData<>();
        }
        return loginUsingOtp(phone, otp, defaultAddress);
    }

    @Override
    public LiveData<LoginResponse<User>> getLoginResponse() {
        return loginResponse;
    }

    @Override
    public void logout() {
        //UserSession.logOut();
        isLoading.setValue(false);
        sendOtpResponse = new MutableLiveData<>();
        loginResponse= new MutableLiveData<>();
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
    private LiveData<LoginResponse<User>> loginUsingOtp(String phone, String otp, Address defaultAddress){
        Log.d(TAG, "Inside loginUsingOtp()......");
        LoginRequest loginRequest = new LoginRequest(phone, otp, defaultAddress);
        Log.d(TAG, "REQUEST: "+loginRequest);
        isLoading.setValue(true);

        ApiInterface apiInterface = ApiService.getApiService();
        apiInterface.loginUsingOtp(loginRequest).enqueue(new Callback<LoginResponse<User>>() {
            @Override
            public void onResponse(Call<LoginResponse<User>> call, Response<LoginResponse<User>> response) {
                isLoading.setValue(false);
                try{
                    LoginResponse<User> resp  = response.body();
                    Log.d(TAG, "RESPONSE: "+resp);
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
