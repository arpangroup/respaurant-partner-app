package com.example.mainactivity.models.request;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCancelRequest extends RequestToken {
    @SerializedName("order_id")
    private int orderId;

    @SerializedName("cancel_reason")
    private String cancelReason;

    public OrderCancelRequest(int orderId, String cancelReason){
        super();
        this.orderId = orderId;
        this.cancelReason = cancelReason;
    }
}
