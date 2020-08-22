package com.example.mainactivity.viewmodels;

import android.location.Address;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.request.NewOrderRequest;
import com.example.mainactivity.repositories.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private OrderRepository orderRepository;

    private LiveData<List<Order>> mOrders;

    public void init(){
        if (mOrders != null){
            return;
        }
        orderRepository = OrderRepository.getInstance();
    }

    public LiveData<List<Order>> getNewOrders(){
        NewOrderRequest newOrderRequest = new NewOrderRequest(6, getListedOrderIds());
        return orderRepository.getNewOrders(newOrderRequest);
    }




    /*=================================================HANDLER_METHODS[START]==============================================*/
    private List<String> getListedOrderIds(){
        if(mOrders != null){
            List<Order> orders = mOrders.getValue();
            if(orders != null)return orders.stream().map(order -> String.valueOf(order.getId())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    /*=================================================HANDLER_METHODS[END]==============================================*/

}
