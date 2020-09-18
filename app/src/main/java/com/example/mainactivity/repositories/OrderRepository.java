package com.example.mainactivity.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.request.NewOrderRequest;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.models.response.Dashboard;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private final String TAG = this.getClass().getSimpleName();
    private static OrderRepository orderRepository;
    private MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private MutableLiveData<List<Order>> mutableAcceptedOrders = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<String>> mutableCancelOrders;
    private MutableLiveData<Dashboard> mutableDashboard;
    MutableLiveData<List<Order>> mutableOrderList;

    public static OrderRepository getInstance(){
        if (orderRepository == null){
            orderRepository = new OrderRepository();
        }
        return orderRepository;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }
    public LiveData<Dashboard> getDashboard(int userId){
        if(mutableDashboard == null){
            mutableDashboard = new MutableLiveData<>();
        }
        loadDashboard(userId);
        return mutableDashboard;
    }

    public void loadAcceptedOrders(RequestToken requestToken){
        if(mutableAcceptedOrders == null){
            mutableAcceptedOrders = new MutableLiveData<>();
        }
        loadAllAcceptedOrdersFromApi(requestToken);
    }
    public LiveData<List<Order>> getAllAcceptedOrders(){
        if(mutableAcceptedOrders == null){
            mutableAcceptedOrders = new MutableLiveData<>();
        }
        return mutableAcceptedOrders;
    }
    public LiveData<ApiResponse> acceptOrder(RequestToken requestToken, Order order){
        return acceptOrderApi(requestToken, order);
    }


    public LiveData<List<Order>> getNewOrders(NewOrderRequest newOrderRequest){
        if(mutableOrderList == null){
            mutableOrderList = new MutableLiveData<>();
            loadNewOrdersApi(newOrderRequest);
        }
        return mutableOrderList;
    }
    public LiveData<List<String>> getAllCanceledOrders(){
        if(mutableCancelOrders == null){
            mutableCancelOrders = new MutableLiveData<>();
        }
        return mutableCancelOrders;
    }
    public void changePrepareTime(int orderId, int time){
        if(mutableOrderList.getValue() == null)return ;

        List<Order> orderList = new ArrayList<>(mutableOrderList.getValue());

        Order updatableOrder = orderList.stream().filter(order -> order.getId() == orderId).findAny().get();
        updatableOrder.getRestaurant().setDeliveryTime(String.valueOf(time));

       // Order.itemCallback.areContentsTheSame()

        //orderList.set(orderList.indexOf(order.get), order);
        //orderList.stream().filter(order1 -> order1.getId() == order.getId()).map(order1 -> order1 = order);
//        orderList.forEach(order1 -> {
//            if(order1.getId() == order.getId())order1.getRestaurant().setDeliveryTime(order.getRestaurant().getDeliveryTime());
//        });

        //mutableOrderList.setValue(null);
        mutableOrderList.setValue(orderList);
    }
    public LiveData<ApiResponse> cancelOrder(RequestToken requestToken){
        return cancelOrderApi(requestToken);
    }





    /*========================================================API_CALLS==============================================*/
    private void loadDashboard(int userId){
        Log.d(TAG, "Inside loadDashboard()......");
        Log.d(TAG, "UserId: "+userId);
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getDashboard(userId).enqueue(new Callback<Dashboard>() {
            @Override
            public void onResponse(Call<Dashboard> call, Response<Dashboard> response) {
                isLoading.setValue(false);
                mutableDashboard.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Dashboard> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL:"+t);
            }
        });
    }
    private void loadNewOrdersApi(NewOrderRequest newOrderRequest){
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getNewOrders(newOrderRequest).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                isLoading.setValue(false);
                List<Order> orders = response.body();
                mutableOrderList.setValue(orders);
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
    private void loadAllAcceptedOrdersFromApi(RequestToken requestToken){
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getAcceptedOrders(requestToken).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                isLoading.setValue(false);
                List<Order> orders = response.body();
                mutableAcceptedOrders.setValue(orders);
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
    private LiveData<ApiResponse> acceptOrderApi(RequestToken requestToken, Order order){
        MutableLiveData<ApiResponse> apiResponseMutableLiveData = new MutableLiveData<>();
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.acceptOrder(requestToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                ApiResponse apiResponse = response.body();

                // Update accepted orders list:
                List<Order> acceptedOrderList = mutableAcceptedOrders.getValue();
                if(mutableAcceptedOrders == null) mutableAcceptedOrders = new MutableLiveData<>(new ArrayList<>());
                if (acceptedOrderList == null) acceptedOrderList = new ArrayList<>();
                acceptedOrderList.add(order);
                mutableAcceptedOrders.postValue(acceptedOrderList);

                apiResponseMutableLiveData.setValue(apiResponse);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }
    private LiveData<ApiResponse> cancelOrderApi(RequestToken requestToken){
        MutableLiveData<ApiResponse> apiResponseMutableLiveData = new MutableLiveData<>();
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.cancelOrder(requestToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                ApiResponse apiResponse = response.body();


                // Update accepted orders list
                if(mutableCancelOrders == null) mutableCancelOrders = new MutableLiveData<>();
                if(mutableCancelOrders.getValue() != null){
                    List<String> acceptedOrderList = new ArrayList<>(mutableCancelOrders.getValue());
                    acceptedOrderList.add(String.valueOf(requestToken.getOrder_id()));
                    mutableCancelOrders.setValue(acceptedOrderList);
                }

                apiResponseMutableLiveData.setValue(apiResponse);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }
    private LiveData<ApiResponse> makeOrderReadyApi(RequestToken requestToken){
        MutableLiveData<ApiResponse> apiResponseMutableLiveData = new MutableLiveData<>();
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.makeOrderReady(requestToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                ApiResponse apiResponse = response.body();
                apiResponseMutableLiveData.setValue(apiResponse);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }
}
