package com.pureeats.restaurant.api;

import com.pureeats.restaurant.models.Order;
import com.pureeats.restaurant.models.Restaurant;
import com.pureeats.restaurant.models.User;
import com.pureeats.restaurant.models.request.AcceptOrderRequest;
import com.pureeats.restaurant.models.request.DisableCategoryRequest;
import com.pureeats.restaurant.models.request.DisableItemRequest;
import com.pureeats.restaurant.models.request.LoginRequest;
import com.pureeats.restaurant.models.request.NewOrderRequest;
import com.pureeats.restaurant.models.request.OrderCancelRequest;
import com.pureeats.restaurant.models.request.ReadyOrderRequest;
import com.pureeats.restaurant.models.request.RequestToken;
import com.pureeats.restaurant.models.request.RunningOrderRequest;
import com.pureeats.restaurant.models.response.ApiResponse;
import com.pureeats.restaurant.models.response.Dashboard;
import com.pureeats.restaurant.models.response.LoginResponse;
import com.pureeats.restaurant.models.response.NewOrderResponse;
import com.pureeats.restaurant.models.response.RestaurantItemResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("/api/send-login-otp/{phone}")
    Call<ApiResponse> sendLoginOtp(@Path("phone") String phone);

//    @POST("/api/login-using-otp")
//    Call<LoginResponse<User>> login(@Body LoginRequest loginRequest);

    @POST("/api/store-owner/login-using-otp")
    Call<LoginResponse<User>> loginUsingOtp(@Body LoginRequest loginRequest);

    @GET("/api/store-owner/dashboard/{storeOwnerId}")
    Call<Dashboard> getDashboard(@Path("storeOwnerId") int storeOwnerId);

    @POST("/api/store-owner/orders/get-new-orders")
    Call<NewOrderResponse> getNewOrders(@Body NewOrderRequest newOrderRequest);


    @POST("/api/store-owner/orders/get-accepted-orders")
    Call<List<Order>> getAcceptedOrders(@Body RequestToken requestToken);

    @POST("/api/store-owner/orders/running-orders")
    Call<List<Order>> getRunningOrders(@Body RunningOrderRequest runningOrderRequest);

    @POST("/api/store-owner/orders/accept-order")
    Call<ApiResponse> acceptOrder(@Body AcceptOrderRequest acceptOrderRequest);

    @POST("/api/store-owner/orders/cancel-order")
    Call<ApiResponse> cancelOrder(@Body OrderCancelRequest orderCancelRequest);

    @POST("/api/store-owner/orders/mark-order-ready")
    Call<ApiResponse> makeOrderReady(@Body ReadyOrderRequest readyOrderRequest);

    @POST("/api/store-owner/orders/mark-selfpickup-order-completed")
    Call<ApiResponse> markSelfPickupOrderCompleted(@Body RequestToken requestToken);

    @POST("/api/store-owner/orders/searchOrders")
    Call<List<Object>> searchOrder(@Query("user_id") String userId, @Query("query") String query);


    /*=============================================================*/
    @GET("/api/store-owner/items/{storeOwnerId}")
    Call<RestaurantItemResponse> getAllItems(@Path("storeOwnerId") int storeOwnerId);

    @GET("/api/store-owner/stores/{storeOwnerId}")
    Call<List<Restaurant>> getRestaurants(@Path("storeOwnerId") int storeOwnerId);





    @POST("/api/store-owner/store/disable")
    Call<ApiResponse> disableRestaurant(@Body RequestToken requestToken);

    @POST("/api/store-owner/item/disable")
    Call<ApiResponse> disableItem(@Body DisableItemRequest disableItemRequest);

    @POST("/api/store-owner/itemcategory/disable")
    Call<ApiResponse> disableCategory(@Body DisableCategoryRequest disableCategoryRequest);

    @Multipart
    @POST("/api/store-owner/store/edit/save")
    Call<ApiResponse> updateRestaurant(
            @Part("user_id") RequestBody userId,
            @Part("restaurant_id") RequestBody restaurantId,
            @Part("item_category_id") RequestBody itemCategoryId,
            @Part("item_id") RequestBody itemId,
            @Part("name") RequestBody name,
            @Part("desc") RequestBody desc,
            @Part("price") RequestBody price,
            @Part("old_price") RequestBody oldPrice,
            @Part("is_recommended") RequestBody isRecommended,
            @Part("is_popular") RequestBody isPopular,
            @Part("is_new") RequestBody isNew,
            @Part("is_veg") RequestBody isVeg,
            @Part MultipartBody.Part image
    );




    @POST("/api/coordinate-to-address")
    Call<String> coordinateToAddress(@Query("lat") String lat, @Query("lng") String lng);


}
