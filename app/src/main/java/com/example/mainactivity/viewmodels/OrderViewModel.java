package com.example.mainactivity.viewmodels;

import android.location.Address;
import android.util.Log;

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

    private List<Order> ordersNotAccepted = new ArrayList<>();
    private List<Order> newOrders = new ArrayList<>();
    private MutableLiveData<List<Order>> mutableNewOrders = new MutableLiveData<>(newOrders);

    public void init(){
        if (mutableOrders != null){
            return;
        }
        orderRepository = OrderRepository.getInstance();
    }

    public void setNewOrder(List<Order> orders){
        Log.d("MessagingService", "Adding new order to view model");
        newOrders.addAll(orders);
        mutableNewOrders.setValue(newOrders);
    }
    public void removeOrderFromNewOrderList(Order order){
        if(newOrders != null) {
            List<Order>  orders = newOrders;
            orders.removeIf(orderObj -> orderObj.getId() == order.getId());
            mutableNewOrders.setValue(orders);
        }
    }
    public LiveData<List<Order>> getNewOrders(){
        if(newOrders == null){
            newOrders = new ArrayList<>();
        }
        return mutableNewOrders;
    }
    public void changeDeliveryTimeOfNotAcceptedOrder(int orderId, int time){
        //orderRepository.changePrepareTime(orderId, time);
        if(mutableNewOrders != null && mutableNewOrders.getValue() != null){
            List<Order> orderList = mutableNewOrders.getValue();

            Order updatableOrder = orderList.stream().filter(order -> order.getId() == orderId).findAny().get();
            updatableOrder.getRestaurant().setDeliveryTime(String.valueOf(time));
            mutableNewOrders.setValue(orderList);
        }
        //orderList.set(orderList.indexOf(order.get), order);
        //orderList.stream().filter(order1 -> order1.getId() == order.getId()).map(order1 -> order1 = order);
//        orderList.forEach(order1 -> {
//            if(order1.getId() == order.getId())order1.getRestaurant().setDeliveryTime(order.getRestaurant().getDeliveryTime());
//        });

        //mutableOrderList.setValue(null);
    }

    public LiveData<Boolean> getIsLoading(){
        LiveData<Boolean> isLoading=orderRepository.getIsLoading();
        return isLoading;
    }
    public LiveData<Dashboard> getDashboard(){
         int userId = new RequestToken().getUserId();
         return orderRepository.getDashboard(userId);
    }

    public void loadAllAcceptedOrders(){
        orderRepository.loadAcceptedOrders();
    }
    public LiveData<List<Order>> getAllAcceptedOrders(){
        return orderRepository.getAllAcceptedOrders();
    }

    public void setFilterOrders(List<Order> orders){
        if(mutableOrders == null){
            mutableOrders = new MutableLiveData<>();
        }
        mutableOrders.setValue(orders);
    }
    public LiveData<List<Order>> getAllFilteredOrders(){
       if(mutableOrders == null){
           mutableOrders = new MutableLiveData<>();
       }
       return mutableOrders;
    }

    public LiveData<ApiResponse> acceptOrder(Order order, int userId){
        int foodPrepareTime = Integer.parseInt(order.getRestaurant().getDeliveryTime());
        return orderRepository.acceptOrder(order, foodPrepareTime, userId);
    }
    public LiveData<ApiResponse> cancelOrder(Order order, int userId, String cancelReason){
        return orderRepository.cancelOrder(order, userId, cancelReason);
    }
    public LiveData<ApiResponse> makeOrderAsReady(int orderId){
        return orderRepository.makeOrderAsReady(orderId);
    }
}
