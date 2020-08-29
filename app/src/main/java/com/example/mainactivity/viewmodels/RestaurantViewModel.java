package com.example.mainactivity.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.repositories.RestaurantRepository;
import com.example.mainactivity.repositories.RestaurantRepositoryImpl;
import com.example.mainactivity.repositories.RestaurantRepositoryStubImpl;

import java.util.List;

public class RestaurantViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private RestaurantRepository restaurantRepository;

    private LiveData<List<MenuItem>> mItems;

    public void init(){
        if (mItems != null){
            return;
        }
        restaurantRepository = RestaurantRepositoryStubImpl.getInstance();
    }

    public LiveData<Boolean> getIsLoading(){
        LiveData<Boolean> isLoading=restaurantRepository.getIsLoading();
        return isLoading;
    }
    public LiveData<List<ItemCategory>> getRestaurantsMenuItems(){
        String userId = new RequestToken().getUser_id();
        return restaurantRepository.getRestaurantItems(userId);
    }

}
