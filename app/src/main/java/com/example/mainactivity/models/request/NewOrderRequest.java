package com.example.mainactivity.models.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewOrderRequest extends RequestToken {
    private List<String>listed_order_ids;

    public NewOrderRequest(List<String> listed_order_ids) {
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
