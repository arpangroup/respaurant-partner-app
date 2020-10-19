package com.pureeats.restaurant.models.request;


import com.pureeats.restaurant.commons.LoginType;
import com.pureeats.restaurant.models.Address;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Data
@ToString
public class LoginRequest {
    @NonNull
    @SerializedName("login_type")
    private String loginType;
    private String name;
    private  String email;
    private String password;
    private String accessToken;
    private String phone;
    private String otp;
    private String provider;
    private Address address;
    @SerializedName("push_token")
    private String pushToken;

    public LoginRequest() {
    }

    public LoginRequest(String name, String email, String password, String accessToken, String phone, String provider, Address address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.accessToken = accessToken;
        this.phone = phone;
        this.provider = provider;
        this.address = address;
    }


    public LoginRequest(String phone, String otpOrPassword, Address defaultAddress, LoginType loginType) {
        this.phone = phone;
        this.address = defaultAddress;
        this.loginType = loginType.name();

        switch (loginType){
            case OTP:
                this.otp = otpOrPassword;
                break;
            case MOBILE_AND_PASSWORD:
                this.password = otpOrPassword;
                break;
            default:
                break;
        }


    }
}
