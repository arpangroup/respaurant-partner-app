package com.example.mainactivity.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;
import com.example.mainactivity.models.response.RestaurantItemResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepositoryImpl implements RestaurantRepository{
    private final String TAG = this.getClass().getSimpleName();
    private static RestaurantRepositoryImpl restaurantRepository;
    private MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private MutableLiveData<List<ItemCategory>> mutableMenuItems;


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
    public LiveData<List<ItemCategory>> getRestaurantItems(String userId){
        if(mutableMenuItems == null){
            mutableMenuItems = new MutableLiveData<>();
            loadRestaurantsMenuApi(userId);
        }
        return mutableMenuItems;
    }


    /*========================================================API_CALLS==============================================*/
    private void loadRestaurantsMenuApi(String userId){
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getAllItems(userId).enqueue(new Callback<RestaurantItemResponse>() {
            @Override
            public void onResponse(Call<RestaurantItemResponse> call, Response<RestaurantItemResponse> response) {
                isLoading.setValue(false);
                RestaurantItemResponse itemResponse = response.body();
                List<MenuItem> menuItems = itemResponse.getData();
                //mutableMenuItems.setValue(menuItems);
            }

            @Override
            public void onFailure(Call<RestaurantItemResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
}
