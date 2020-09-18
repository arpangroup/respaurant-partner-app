package com.example.mainactivity.models.request;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
