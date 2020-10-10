package com.example.mainactivity.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class DeliveryGuy {
    private int id;
    private String name;
    private String email;
    private String age;
    private String description;
    private String phone;
    private String photo;

    @SerializedName("vehicle_number")
    private String vehicleNumber;

    @SerializedName("commission_rate")
    private String commissionRate;

    @SerializedName("is_notifiable")
    private int isNotifiable;

    @SerializedName("max_accept_delivery_limit")
    private int maxAcceptDeliveryLimit;

    @SerializedName("delivery_pin")
    private String deliveryPin;

    public String getOtp(){
        int lastIndex = this.phone.length();
        String otp = this.phone.substring(lastIndex -4, lastIndex);
        return otp;
    }

}
