package com.example.mainactivity.viewmodels;

import android.location.Address;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mainactivity.commons.OrderStatus;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.request.NewOrderRequest;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.repositories.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private OrderRepository orderRepository;
    //private MutableLiveData<ORDER_TYPE> mutableOrderType = new MutableLiveData<>(ORDER_TYPE.ALL);
    private MutableLiveData<List<Order>> mutableOrders = null;

    public void init(){
        if (mutableOrders != null){
            return;
        }
        orderRepository = OrderRepository.getInstance();
    }

    public LiveData<Boolean> getIsLoading(){
        LiveData<Boolean> isLoading=orderRepository.getIsLoading();
        return isLoading;
    }
    public LiveData<Dashboard> getDashboard(){
         String userId = new RequestToken().getUser_id();
         return orderRepository.getDashboard(userId);
    }

    public LiveData<List<Order>> getNewOrders(){
        NewOrderRequest newOrderRequest = new NewOrderRequest(6, getListedOrderIds());
        return orderRepository.getNewOrders(newOrderRequest);
    }

    public void changeDeliveryTime(int orderId, int time){
        orderRepository.changePrepareTime(orderId, time);
    }

    public void setFilterOrders(List<Order> orders){
        if(mutableOrders == null){
            mutableOrders = new MutableLiveData<>();
        }
        mutableOrders.setValue(orders);
    }
    public LiveData<List<Order>> getAllOrders(){
       if(mutableOrders == null){
           mutableOrders = new MutableLiveData<>();
           getDashboard();
       }
       return mutableOrders;
    }

    public LiveData<ApiResponse> acceptOrder(int orderId){
        RequestToken requestToken  = new RequestToken(orderId);
        return orderRepository.acceptOrder(requestToken);
    }

    public LiveData<ApiResponse> cancelOrder(int orderId){
        RequestToken requestToken  = new RequestToken(orderId);
        return orderRepository.cancelOrder(requestToken);
    }




    /*=================================================HANDLER_METHODS[START]==============================================*/
    private List<String> getListedOrderIds(){
        if(mutableOrders != null){
            List<Order> orders = mutableOrders.getValue();
            if(orders != null)return orders.stream().map(order -> String.valueOf(order.getId())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /*=================================================HANDLER_METHODS[END]==============================================*/

}
