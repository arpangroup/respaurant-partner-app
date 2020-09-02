package com.example.mainactivity.api;

import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.Restaurant;
import com.example.mainactivity.models.User;
import com.example.mainactivity.models.request.LoginRequest;
import com.example.mainactivity.models.request.NewOrderRequest;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.models.response.LoginResponse;
import com.example.mainactivity.models.response.RestaurantItemResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("/api/send-login-otp/{phone}")
    Call<ApiResponse> sendLoginOtp(@Path("phone") String phone);

    @POST("/api/login-using-otp")
    Call<LoginResponse<User>> loginUsingOtp(@Body LoginRequest loginRequest);

    @GET("/api/store-owner/stores/{storeOwnerId}")
    Call<List<Restaurant>> getRestaurants(@Path("storeOwnerId") String storeOwnerId);

    @GET("/api/store-owner/dashboard/{storeOwnerId}")
    Call<Dashboard> getDashboard(@Path("storeOwnerId") String storeOwnerId);

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



    @GET("/api/store-owner/items/{storeOwnerId}")
    Call<RestaurantItemResponse> getAllItems(@Path("storeOwnerId") String storeOwnerId);

    @POST("/api/get-restaurant-items/{restaurant_slug}")
    Call<ItemCategory> getRestaurantItems(@Path("restaurant_slug") String slug);


}
