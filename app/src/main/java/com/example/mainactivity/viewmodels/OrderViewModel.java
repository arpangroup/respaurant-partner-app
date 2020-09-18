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
    public void removeNewOrder(Order order){
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
        //System.out.println("==========================GET ORDERS================================\n");
        //newOrders.getValue().forEach(orderObj -> System.out.println("ID: "+orderObj.getId() +", Title: "+orderObj.getUniqueOrderId()));
        return mutableNewOrders;
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
        RequestToken requestToken = new RequestToken();
        orderRepository.loadAcceptedOrders(requestToken);
    }
    public LiveData<List<Order>> getAllAcceptedOrders(){
        return orderRepository.getAllAcceptedOrders();
    }

    public LiveData<List<Order>> getNewOrdersFromApi(){
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
    public LiveData<List<Order>> getAllFilteredOrders(){
       if(mutableOrders == null){
           mutableOrders = new MutableLiveData<>();
       }
       return mutableOrders;
    }
    public LiveData<ApiResponse> acceptOrder(Order order){
        RequestToken requestToken  = new RequestToken(order.getId());
        return orderRepository.acceptOrder(requestToken, order);
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
