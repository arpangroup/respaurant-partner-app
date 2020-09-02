package com.example.mainactivity.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;
import com.example.mainactivity.models.Restaurant;
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
    private MutableLiveData<Restaurant> mutableRestaurant;


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
    public LiveData<Restaurant> getRestaurantDetails(String userId) {
        if(mutableRestaurant == null){
            mutableRestaurant = new MutableLiveData<>();
        }
        loadRestaurant(userId);
        return mutableRestaurant;
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
    private void loadRestaurant(String userId){
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getRestaurants(userId).enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                isLoading.setValue(false);
                List<Restaurant> restaurants = response.body();
                if(restaurants != null){
                    mutableRestaurant.setValue(restaurants.get(0));
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
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
