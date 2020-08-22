package com.example.mainactivity.api;

import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.request.NewOrderRequest;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.models.response.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    @POST("/api/store-owner/orders/get-new-orders")
    Call<List<Order>> getNewOrders(@Body NewOrderRequest newOrderRequest);

    @POST("/api/store-owner/orders/accept-order")
    Call<ApiResponse> acceptOrder(@Body RequestToken requestToken);

    @POST("/api/store-owner/orders/cancel-order")
    Call<ApiResponse> cancelOrder(@Body RequestToken requestToken);

    @POST("/api/store-owner/orders/mark-order-ready")
    Call<ApiResponse> makeOrderReady(@Body RequestToken requestToken);

    @POST("/api/store-owner/orders/mark-selfpickup-order-completed")
    Call<ApiResponse> markSelfPickupOrderCompleted(@Body RequestToken requestToken);

    @POST("/api/store-owner/orders/searchOrders")
    Call<List<Object>> searchOrder(@Query("user_id") String userId, @Query("query") String query);


}
