package com.example.mainactivity.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;
import com.example.mainactivity.models.Restaurant;
import com.example.mainactivity.models.request.DisableCategoryRequest;
import com.example.mainactivity.models.request.DisableItemRequest;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.models.response.RestaurantItemResponse;
import com.example.mainactivity.sharedpref.UserSession;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepositoryImpl implements RestaurantRepository{
    private final String TAG = this.getClass().getSimpleName();
    private static RestaurantRepositoryImpl restaurantRepository;
    private MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private MutableLiveData<List<ItemCategory>> mutableMenuItems;
    private MutableLiveData<Restaurant> mutableRestaurant;
    private MutableLiveData<Dashboard> mutableDashboard;


    public static RestaurantRepositoryImpl getInstance(){
        if (restaurantRepository == null){
            restaurantRepository = new RestaurantRepositoryImpl();
        }
        return restaurantRepository;
    }


    @Override
    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    @Override
    public void loadDashboard(int userId){
        if(mutableDashboard == null){
            mutableDashboard = new MutableLiveData<>();
            loadDashboardApi(userId);
        }
    }

    @Override
    public LiveData<Dashboard> getDashboard(){
        if(mutableDashboard == null){
            mutableDashboard = new MutableLiveData<>();
        }
        return mutableDashboard;
    }

    @Override
    public LiveData<Restaurant> getRestaurantDetails(int userId) {
        if(mutableRestaurant == null){
            mutableRestaurant = new MutableLiveData<>();
        }

        if(UserSession.getRestaurantData() != null){
            Restaurant restaurant = UserSession.getRestaurantData();
            mutableRestaurant.setValue(restaurant);
        }else{
            loadRestaurant(userId);
        }
        return mutableRestaurant;
    }

    @Override
    public LiveData<List<ItemCategory>> getRestaurantItems(int userId){
        if(mutableMenuItems == null){
            mutableMenuItems = new MutableLiveData<>();
        }
        loadRestaurantsMenuApi(userId);
        return mutableMenuItems;
    }

    @Override
    public LiveData<ApiResponse> toggleMenuItem(DisableItemRequest disableItemRequest){
        return toggleItemActive(disableItemRequest);
    }

    @Override
    public LiveData<ApiResponse> toggleRestaurant(RequestToken requestToken, boolean status){
        return toggleRestaurantActive(requestToken, status);
    }

    @Override
    public LiveData<ApiResponse> toggleCategory(DisableCategoryRequest disableCategoryRequest){
        return toggleCategoryActive(disableCategoryRequest);
    }


    /*========================================================API_CALLS==============================================*/
    private void loadDashboardApi(int userId){
        Log.d(TAG, "Inside loadDashboard()......");
        Log.d(TAG, "UserId: "+userId);
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getDashboard(userId).enqueue(new Callback<Dashboard>() {
            @Override
            public void onResponse(Call<Dashboard> call, Response<Dashboard> response) {
                Log.d(TAG, "RESPONSE: "+response);
                Log.d(TAG, "RESPONSE: "+response);
                isLoading.setValue(false);
                Dashboard dashboard = response.body();
                if(dashboard.getAllOrders() != null){
                    if(dashboard != null)Collections.reverse(dashboard.getAllOrders());
                }
                mutableDashboard.setValue(dashboard);
            }

            @Override
            public void onFailure(Call<Dashboard> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL:"+t);
            }
        });
    }
    private void loadRestaurant(int userId){
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getRestaurants(userId).enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                isLoading.setValue(false);
                List<Restaurant> restaurants = response.body();
                if(restaurants != null){
                    UserSession.setRestaurantData(restaurants.get(0));
                    mutableRestaurant.setValue(restaurants.get(0));
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
    private void loadRestaurantsMenuApi(int userId){
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getAllItems(userId).enqueue(new Callback<RestaurantItemResponse>() {
            @Override
            public void onResponse(Call<RestaurantItemResponse> call, Response<RestaurantItemResponse> response) {
                isLoading.setValue(false);
                RestaurantItemResponse itemResponse = response.body();

                List<ItemCategory> categories = new ArrayList<>();
                if (itemResponse != null && itemResponse.getItems() != null) {
                    itemResponse.getItems().entrySet()
                            .forEach(e -> {
                                MenuItem menuItem = e.getValue().get(0);
                                ItemCategory category = new ItemCategory(menuItem.getItemCategoryId(), menuItem.getCategoryName(), menuItem.getItemCategory().getIsEnabled(), e.getValue());
                                categories.add(category);
                            });
                }
                mutableMenuItems.setValue(categories);


            }

            @Override
            public void onFailure(Call<RestaurantItemResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }

    private LiveData<ApiResponse> toggleItemActive(DisableItemRequest disableItemRequest){
        MutableLiveData<ApiResponse> mutableApiResponse = new MutableLiveData<>();
        ApiInterface apiInterface = ApiService.getApiService();
        Log.d(TAG, "Inside toggleItemActive().....");
        Log.d(TAG, "REQUEST: "+new Gson().toJson(disableItemRequest));
        isLoading.setValue(true);
        apiInterface.disableItem(disableItemRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                ApiResponse  apiResponse = response.body();
                if(apiResponse.isSuccess()){
                    mutableApiResponse.setValue(apiResponse);
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return mutableApiResponse;
    }
    private LiveData<ApiResponse> toggleRestaurantActive(RequestToken requestToken, boolean status){
        final MutableLiveData<ApiResponse> mutableApiResponse = new MutableLiveData<>();
        ApiInterface apiInterface = ApiService.getApiService();
        Log.d(TAG, "Inside toggleRestaurantActive().....");
        Log.d(TAG, "REQUEST: "+new Gson().toJson(requestToken));
        isLoading.setValue(true);
        apiInterface.disableRestaurant(requestToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                ApiResponse apiResponse = response.body();
                Log.d(TAG, "RESPONSE: "+apiResponse);
                if(apiResponse.isSuccess()){
                    mutableApiResponse.setValue(apiResponse);
                    boolean currentStatus = apiResponse.getMessage().equals("1") ? true : false;
                    UserSession.toggleRestaurantActive(currentStatus);
                    mutableRestaurant.postValue(UserSession.getRestaurantData());
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return mutableApiResponse;
    }
    private LiveData<ApiResponse> toggleCategoryActive(DisableCategoryRequest disableCategoryRequest){
        final MutableLiveData<ApiResponse> mutableApiResponse = new MutableLiveData<>();
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.disableCategory(disableCategoryRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                ApiResponse  apiResponse = response.body();
                if(apiResponse.isSuccess()){
                    mutableApiResponse.setValue(apiResponse);
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return mutableApiResponse;
    }
}
