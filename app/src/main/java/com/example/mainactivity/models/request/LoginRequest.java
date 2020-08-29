package com.example.mainactivity.models.request;


import com.example.mainactivity.models.Address;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginRequest {
    private String name;
    private  String email;
    private String password;
    private String accessToken;
    private String phone;
    private String otp;
    private String provider;
    private Address address;

    public LoginRequest(String name, String email, String password, String accessToken, String phone, String provider, Address address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.accessToken = accessToken;
        this.phone = phone;
        this.provider = provider;
        this.address = address;
    }


    public LoginRequest(String phone, String otp, Address defaultAddress) {
        this.phone = phone;
        this.otp = otp;
        this.address = defaultAddress;
    }
}
