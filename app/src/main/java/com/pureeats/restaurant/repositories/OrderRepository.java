package com.pureeats.restaurant.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pureeats.restaurant.api.ApiInterface;
import com.pureeats.restaurant.api.ApiService;
import com.pureeats.restaurant.commons.OrderStatus;
import com.pureeats.restaurant.models.Order;
import com.pureeats.restaurant.models.request.AcceptOrderRequest;
import com.pureeats.restaurant.models.request.OrderCancelRequest;
import com.pureeats.restaurant.models.request.ReadyOrderRequest;
import com.pureeats.restaurant.models.request.RequestToken;
import com.pureeats.restaurant.models.request.RunningOrderRequest;
import com.pureeats.restaurant.models.response.ApiResponse;
import com.pureeats.restaurant.util.CommonUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private final String TAG = this.getClass().getSimpleName();
    private static OrderRepository orderRepository;
    private MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private MutableLiveData<List<Order>> mutableAcceptedOrders = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Order>> mutableRunningOrdersStatuses = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<String>> mutableCancelOrders;

    List<OrderStatus> statusListToBeUpdate = Arrays.asList(
            //OrderStatus.ORDER_RECEIVED,
            OrderStatus.DELIVERY_GUY_ASSIGNED, // 3<==ORDER_ACCEPTED_BY_DELIVERY
            OrderStatus.ORDER_READY, // 7 <==ORDER_MARK_AS_READY
            OrderStatus.REACHED_PICKUP_LOCATION, // 8<==DELIVERY_GUY_REACHED_PICKUP_LOCATION
            OrderStatus.READY_FOR_PICKUP, // 10<== ORDER_IS_READY_TO_PICKUP
            OrderStatus.ON_THE_WAY,  //4<== ORDER_IS_PICKED_UP_AND_DELIVERY_GUY_IS_ON_THE_WAY_TO_DELIVER
            OrderStatus.DELIVERED, //5<=Delivered the order to customer
            OrderStatus.CANCELED //6<==Cancelled the order by customer
    );

    public static OrderRepository getInstance(){
        if (orderRepository == null){
            orderRepository = new OrderRepository();
        }
        return orderRepository;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public void loadAcceptedOrders(){
        if(mutableAcceptedOrders == null){
            mutableAcceptedOrders = new MutableLiveData<>();
        }
        loadAllAcceptedOrdersFromApi();
    }
    public LiveData<List<Order>> getAllAcceptedOrders(){
        if(mutableAcceptedOrders == null){
            mutableAcceptedOrders = new MutableLiveData<>();
        }
        return mutableAcceptedOrders;
    }

    public void loadRunningOrderStatus(){
        List<String> listed_orders = new ArrayList<>();
        List<Order> acceptedOrders = mutableAcceptedOrders.getValue();
        if(acceptedOrders != null){
            if(acceptedOrders.size() > 0){
                listed_orders = acceptedOrders.stream().map(order -> String.valueOf(order.getId())).collect(Collectors.toList());
                loadRunningOrdersStatus(listed_orders);
            }
        }
    }
    public void loadRunningOrderStatus(List<Order> listedOrders){
        List<String> listed_orders = new ArrayList<>();
        if(listedOrders != null){
            if(listedOrders.size() > 0){
                listed_orders = listedOrders.stream().map(order -> String.valueOf(order.getId())).collect(Collectors.toList());
                loadRunningOrdersStatus(listed_orders);
            }
        }
    }
    public LiveData<List<Order>> getRunningOrderStatus(){
       if(mutableRunningOrdersStatuses == null){
           mutableRunningOrdersStatuses = new MutableLiveData<>(new ArrayList<>());
       }
       return mutableRunningOrdersStatuses;
    }
    public LiveData<ApiResponse> acceptOrder(Order order, int foodPrepareTime,  int userId){
        return acceptOrderApi(order, foodPrepareTime, userId);
    }
    
    public LiveData<List<String>> getAllCanceledOrders(){
        if(mutableCancelOrders == null){
            mutableCancelOrders = new MutableLiveData<>();
        }
        return mutableCancelOrders;
    }
    public LiveData<ApiResponse> cancelOrder(Order order, int userId, String  cancelReason){
        OrderCancelRequest orderCancelRequest = new OrderCancelRequest(order.getId(), cancelReason);
        orderCancelRequest.setUserId(userId);
        return cancelOrderApi(orderCancelRequest);
    }
    public LiveData<ApiResponse> makeOrderAsReady(int orderId){
        ReadyOrderRequest readyOrderRequest = new ReadyOrderRequest(orderId);
        return makeOrderReadyApi(readyOrderRequest);
    }
//    public boolean assignDeliveryPerson(Order order, DeliveryGuy deliveryGuy){
//        // Update accepted orders list:
//        Log.d(TAG, "Inside assignDeliveryPerson...");
//       if(mutableAcceptedOrders == null) return false;
//       List<Order> orderList = new ArrayList<>(mutableAcceptedOrders.getValue());
//       Log.d(TAG, "ACCEPTED_ORDER_SIZE: "+orderList.size());
//       Log.d(TAG, "ACCEPTED_ORDERS: "+ orderList);
//
//       orderList.forEach(orderObj ->{
//           if(orderObj.getId() == order.getId()){
//               orderObj.setDeliveryDetails(deliveryGuy);
//               orderObj.setOrderStatusId(order.getOrderStatusId());
//               orderList.set(orderList.indexOf(orderObj), orderObj);
//           }
//       });
//        mutableAcceptedOrders.setValue(orderList);
//        return true;
//    }
    public boolean setStatusChanged(Order order){
        //Update accepted orders list:
        Log.d(TAG, "Inside setStatusChanged...");
       if(mutableAcceptedOrders == null) return false;
       if(mutableAcceptedOrders.getValue() == null){
           Log.d(TAG, "mutableAcceptedOrders  is null");
           return false;
       }
       List<Order> orderList = new ArrayList<>(mutableAcceptedOrders.getValue());
       Log.d(TAG, "ACCEPTED_ORDER_SIZE: "+orderList.size());
       Log.d(TAG, "ACCEPTED_ORDERS: "+ orderList);

       orderList.forEach(orderObj ->{
           if(orderObj.getId() == order.getId()){
               OrderStatus orderStatus = CommonUtils.mapOrderStatus(order.getOrderStatusId());
               boolean isStatusPresentInTheList = statusListToBeUpdate.stream().anyMatch(orderStatus1 -> orderStatus1 ==  orderStatus);
               if(isStatusPresentInTheList){
                   orderObj.setOrderStatusId(order.getOrderStatusId());

                   if (orderStatus == OrderStatus.DELIVERY_GUY_ASSIGNED || orderStatus == OrderStatus.READY_FOR_PICKUP){
                       orderObj.setDeliveryDetails(order.getDeliveryDetails());
                       Log.d(TAG, "##########################################");
                       Log.d(TAG,"ID: "+ orderObj.getId());
                       Log.d(TAG,"DRIVER: "+orderObj.getDeliveryDetails());
                       Log.d(TAG,"##########################################");
                   }
               }


           }
       });
       orderList.removeIf(orderObj -> orderObj.getOrderStatusId() == OrderStatus.DELIVERED.value() || orderObj.getOrderStatusId() == OrderStatus.CANCELED.value());
        mutableAcceptedOrders.setValue(orderList);
        return true;
    }





    /*========================================================API_CALLS==============================================*/
    private void loadAllAcceptedOrdersFromApi(){
        RequestToken requestToken = new RequestToken();
        Log.d(TAG,  "Inside loadAllAcceptedOrdersFromApi().....");
        Log.d(TAG, "REQUEST: "+new Gson().toJson(requestToken));
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
    private void loadRunningOrdersStatus(List<String> listedOrderIds){
        RunningOrderRequest runningOrderRequest = new RunningOrderRequest(listedOrderIds);
        //Log.d(TAG,  "Inside getRunningOrdersStatus().....");
        //Log.d(TAG, "REQUEST: "+new Gson().toJson(runningOrderRequest));
        ApiInterface apiInterface = ApiService.getApiService();
        //isLoading.setValue(true);
        apiInterface.getRunningOrders(runningOrderRequest).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                //isLoading.setValue(false);
                List<Order> orders = response.body();
                if(orders != null){
                    if(orders.size() > 0){
                        //Log.d(TAG, "Response: OrderStatus");
                        mutableRunningOrdersStatuses.setValue(orders);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                //isLoading.setValue(false);
            }
        });
    }
    private LiveData<ApiResponse> acceptOrderApi(Order order, int foodPrepareTime, int userId){
        AcceptOrderRequest acceptOrderRequest = new AcceptOrderRequest(order.getId(), foodPrepareTime);
        acceptOrderRequest.setUserId(userId);
        MutableLiveData<ApiResponse> apiResponseMutableLiveData = new MutableLiveData<>();
        Log.d(TAG, "Inside acceptOrderApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(acceptOrderRequest));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.acceptOrder(acceptOrderRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                ApiResponse apiResponse = response.body();
                Log.d(TAG, "RESPONSE: "+apiResponse);

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
                Log.d(TAG, "FAIL");
                isLoading.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }
    private LiveData<ApiResponse> cancelOrderApi(OrderCancelRequest orderCancelRequest){
        MutableLiveData<ApiResponse> apiResponseMutableLiveData = new MutableLiveData<>();
        ApiInterface apiInterface = ApiService.getApiService();
        Log.d(TAG,  "Inside cancelOrderApi().....");
        Log.d(TAG, "REQUEST: "+new Gson().toJson(orderCancelRequest));
        isLoading.setValue(true);
        apiInterface.cancelOrder(orderCancelRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                ApiResponse apiResponse = response.body();
                Log.d(TAG, "RESPONSE: "+apiResponse);

                // Update accepted orders list
//                if(mutableCancelOrders == null) mutableCancelOrders = new MutableLiveData<>();
//                if(mutableCancelOrders.getValue() != null){
//                    List<String> acceptedOrderList = new ArrayList<>(mutableCancelOrders.getValue());
//                    acceptedOrderList.add(String.valueOf(requestToken.getOrder_id()));
//                    mutableCancelOrders.setValue(acceptedOrderList);
//                }

                apiResponseMutableLiveData.setValue(apiResponse);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }
    private LiveData<ApiResponse> makeOrderReadyApi(ReadyOrderRequest readyOrderRequest){
        MutableLiveData<ApiResponse> apiResponseMutableLiveData = new MutableLiveData<>();
        ApiInterface apiInterface = ApiService.getApiService();
        Log.d(TAG,  "Inside makeOrderReadyApi().....");
        Log.d(TAG, "REQUEST: "+new Gson().toJson(readyOrderRequest));
        isLoading.setValue(true);
        apiInterface.makeOrderReady(readyOrderRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                ApiResponse apiResponse = response.body();
                Log.d(TAG, "RESPONSE: "+ apiResponse);
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
