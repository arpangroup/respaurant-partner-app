package com.pureeats.restaurant.models.request;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadyOrderRequest extends RequestToken{
    @SerializedName("order_id")
    private int orderId;

    public ReadyOrderRequest(int orderId){
        super();
        this.orderId = orderId;
    }
}
