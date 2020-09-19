package com.example.mainactivity.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.User;
import com.example.mainactivity.models.request.AcceptOrderRequest;
import com.example.mainactivity.models.request.NewOrderRequest;
import com.example.mainactivity.models.request.OrderCancelRequest;
import com.example.mainactivity.models.request.ReadyOrderRequest;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.models.response.Dashboard;
import com.google.gson.Gson;

import java.util.ArrayList;
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
    MutableLiveData<List<Order>> mutableNewOrderList;

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
    public LiveData<ApiResponse> acceptOrder(Order order, int foodPrepareTime,  int userId){
        return acceptOrderApi(order, foodPrepareTime, userId);
    }


    public LiveData<List<Order>> getNewOrders(NewOrderRequest newOrderRequest){
        if(mutableNewOrderList == null){
            mutableNewOrderList = new MutableLiveData<>();
            loadNewOrdersApi(newOrderRequest);
        }
        return mutableNewOrderList;
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





    /*========================================================API_CALLS==============================================*/
    private void loadNewOrdersApi(NewOrderRequest newOrderRequest){
        Log.d(TAG,  "Inside loadNewOrdersApi().....");
        Log.d(TAG, "REQUEST: "+new Gson().toJson(newOrderRequest));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getNewOrders(newOrderRequest).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                isLoading.setValue(false);
                List<Order> orders = response.body();
                mutableNewOrderList.setValue(orders);
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
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
