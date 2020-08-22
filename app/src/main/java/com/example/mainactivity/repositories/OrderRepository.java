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
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();

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


    public LiveData<List<Order>> getNewOrders(NewOrderRequest newOrderRequest){
        if(mutableOrderList == null){
            mutableOrderList = new MutableLiveData<>();
            loadOrders(newOrderRequest);
        }
        return mutableOrderList;
    }

    public void changePrepareTime(Order order){
        if(mutableOrderList.getValue() == null)return ;

        List<Order> orderList = mutableOrderList.getValue();

        orderList.set(orderList.indexOf(order), order);

        mutableOrderList.setValue(orderList);
    }

    private void loadOrders(NewOrderRequest newOrderRequest){
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
}
