package com.example.mainactivity.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.request.NewOrderRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private final String TAG = this.getClass().getSimpleName();
    private static OrderRepository orderRepository;

    MutableLiveData<List<Order>> mutableOrderList;

    public static OrderRepository getInstance(){
        if (orderRepository == null){
            orderRepository = new OrderRepository();
        }
        return orderRepository;
    }


    public LiveData<List<Order>> getNewOrders(NewOrderRequest newOrderRequest){
        if(mutableOrderList == null){
            mutableOrderList = new MutableLiveData<>();
            loadOrders(newOrderRequest);
        }
        return mutableOrderList;
    }

    private void loadOrders(NewOrderRequest newOrderRequest){
        ApiInterface apiInterface = ApiService.getApiService();
        apiInterface.getNewOrders(newOrderRequest).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                List<Order> orders = response.body();
                mutableOrderList.setValue(orders);
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {

            }
        });
    }
}
