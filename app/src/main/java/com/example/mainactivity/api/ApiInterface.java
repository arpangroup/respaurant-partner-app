package com.example.mainactivity.api;

import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.request.NewOrderRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/api/store-owner/orders/get-new-orders")
    Call<List<Order>> getNewOrders(@Body NewOrderRequest newOrderRequest);

}
