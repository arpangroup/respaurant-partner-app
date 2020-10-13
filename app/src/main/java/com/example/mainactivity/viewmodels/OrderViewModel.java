package com.example.mainactivity.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.repositories.OrderRepository;
import com.example.mainactivity.views.order.OrderListFragment;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private OrderRepository orderRepository;
    //private MutableLiveData<ORDER_TYPE> mutableOrderType = new MutableLiveData<>(ORDER_TYPE.ALL);
    private MutableLiveData<List<Order>> mutableOrders = null;
    private MutableLiveData<Order> mutableOrderDetails = new MutableLiveData<>();
    private MutableLiveData<OrderListFragment.FilterType> mutableFilterType = null;

    private List<Order> ordersNotAccepted = new ArrayList<>();
    private List<Order> newOrders = new ArrayList<>();
    private MutableLiveData<List<Order>> mutableNewOrders = new MutableLiveData<>(newOrders);

    public void init(){
        if (mutableOrders != null){
            return;
        }
        orderRepository = OrderRepository.getInstance();
    }

    public void setNewOrder(Order order){
        Log.d("MessagingService", "Adding new order to view model");
        if (newOrders == null) {
            newOrders = new ArrayList<>();
        }
        newOrders.add(order);
        mutableNewOrders.setValue(newOrders);
    }
    public void setNewOrder(List<Order> orders){
        if (newOrders == null) {
            newOrders = new ArrayList<>();
        }
        newOrders.addAll(orders);
        mutableNewOrders.setValue(newOrders);
    }
    public void removeOrderFromNewOrderList(Order order){
        if(newOrders != null) {
            List<Order>  orders = newOrders;
            orders.removeIf(orderObj -> orderObj.getId() == order.getId());
            Log.d(TAG, "Removed order "+order.getId());
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
            //updatableOrder.getRestaurant().setDeliveryTime(String.valueOf(time));
            updatableOrder.setPrepareTime(time);
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

    public void loadAllAcceptedOrders(){
        orderRepository.loadAcceptedOrders();
    }
    public LiveData<List<Order>> getAllAcceptedOrders(){
        return orderRepository.getAllAcceptedOrders();
    }

    public void loadRunningOrderStatus(){
        orderRepository.loadRunningOrderStatus();
    }
    public void loadRunningOrderStatus(List<Order> listedOrders){
        orderRepository.loadRunningOrderStatus(listedOrders);
    }
    public LiveData<List<Order>> getRunningOrderStatus(){
        return orderRepository.getRunningOrderStatus();
    }

    public void setFilterType(OrderListFragment.FilterType filterType){
        if(mutableFilterType == null){
            mutableFilterType = new MutableLiveData<>();
        }
        mutableFilterType.setValue(filterType);
    }
    public LiveData<OrderListFragment.FilterType> getCurrentFilterType(){
        if(mutableFilterType == null){
            mutableFilterType = new MutableLiveData<>();
        }
       return mutableFilterType;
    }

    /*======================Deprecated====================*/
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
    /*=======================================================*/

    public LiveData<ApiResponse> acceptOrder(Order order, int userId){
        int foodPrepareTime = order.getPrepareTime();
        return orderRepository.acceptOrder(order, foodPrepareTime, userId);
    }
    public LiveData<ApiResponse> cancelOrder(Order order, int userId, String cancelReason){
        return orderRepository.cancelOrder(order, userId, cancelReason);
    }
    public LiveData<ApiResponse> makeOrderAsReady(int orderId){
        return orderRepository.makeOrderAsReady(orderId);
    }

//    public boolean assignDeliveryPerson(Order order, DeliveryGuy  deliveryGuy){
//        return orderRepository.assignDeliveryPerson(order, deliveryGuy);
//    }
    public boolean setStatusChange(Order order){
        return orderRepository.setStatusChanged(order);
    }


    public void setOrderDetails(Order order){
        mutableOrderDetails.setValue(order);
    }
    public LiveData<Order> getOrderDetails(){
        if(mutableOrderDetails == null){
            mutableOrderDetails = new MutableLiveData<>();
        }
        return mutableOrderDetails;
    }
}
