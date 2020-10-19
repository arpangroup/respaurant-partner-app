package com.pureeats.restaurant.models.request;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptOrderRequest extends RequestToken{
    @SerializedName("order_id")
    private int orderId;

    @SerializedName("food_prepare_time")
    private int foodPrepareTime;

    public AcceptOrderRequest(int orderId, int foodPrepareTime){
        super();
        this.orderId = orderId;
        this.foodPrepareTime = foodPrepareTime;
    }
}
