package com.pureeats.restaurant.models.response;

import com.pureeats.restaurant.models.Order;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewOrderResponse {
    @SerializedName("new_orders")
    private List<Order> newOrders;

    @SerializedName("listed_order_status")
    private List<Order> listedOrderStatuses;
}
