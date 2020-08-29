package com.example.mainactivity.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mainactivity.models.Address;
import com.example.mainactivity.models.User;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.models.response.LoginResponse;
import com.example.mainactivity.repositories.AuthRepository;
import com.example.mainactivity.repositories.AuthRepositoryImpl;

public class AuthenticationViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private AuthRepository authRepository;
    private MutableLiveData<Address> mCurrentAddress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private MutableLiveData<String> mutablePhoneNumber = new MutableLiveData<>();

    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public void init(){
        //boolean loggedIn = UserSession.isLoggedIn();
        //if(loggedIn) isLoggedIn.setValue(true);
        //else isLoggedIn.setValue(false);

        authRepository = AuthRepositoryImpl.getInstance();
    }


    public LiveData<Boolean> getIsLoading(){
        LiveData<Boolean> isLoading=authRepository.getIsLoading();
        return isLoading;
    }
    public LiveData<ApiResponse> sendLoginOtp(String phone){
        return authRepository.sendLoginOtp(phone);
    }
//    public LiveData<ApiResponse> getOtpResponse(){
//        return authRepository.getOtpResponse();
//    }
    public LiveData<LoginResponse<User>> loginByOtp(String phone, String otp, Address defaultAddress){
        return authRepository.loginByOtp(phone, otp, defaultAddress);
    }
    public LiveData<LoginResponse<User>> getLoginResponse(){
        return authRepository.getLoginResponse();
    }
    public void setPhoneNumber(String phoneNumber){
        if(mutablePhoneNumber == null) mutablePhoneNumber = new MutableLiveData<>();
        mutablePhoneNumber.setValue(phoneNumber);
    }
    public LiveData<String> getPhoneNumber(){
        return mutablePhoneNumber;
    }
    public void logout(){
        authRepository.logout();
        onCleared();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }
}