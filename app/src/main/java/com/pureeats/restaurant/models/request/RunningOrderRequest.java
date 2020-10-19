package com.pureeats.restaurant.models.request;

import com.pureeats.restaurant.models.User;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RunningOrderRequest extends RequestToken {
    private List<String>listed_order_ids;

    public RunningOrderRequest(List<String> listed_order_ids) {
        this.listed_order_ids = listed_order_ids;
    }

    public RunningOrderRequest(User user, List<String> listed_order_ids) {
        this.setRestaurantId(user.getRestaurants().get(0).getId());
        this.setUserId(user.getId());
        this.setToken(user.getAuthToken());
        this.listed_order_ids = listed_order_ids;
    }

    public List<String>addOrderId(int orderId){
        if(this.listed_order_ids == null){
            this.listed_order_ids = new ArrayList<>();
        }
        this.listed_order_ids.add(String.valueOf(orderId));
        return this.listed_order_ids;
    }
}
