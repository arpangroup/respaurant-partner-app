package com.example.mainactivity.models.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NewOrderRequest {
    private int user_id;
    private List<String>listed_order_ids;

    public NewOrderRequest(int user_id, List<String> listed_order_ids) {
        this.user_id = user_id;
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
