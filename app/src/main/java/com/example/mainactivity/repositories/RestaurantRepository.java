package com.example.mainactivity.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;
import com.example.mainactivity.models.Restaurant;

import java.util.List;

public interface RestaurantRepository {
    public LiveData<Boolean> getIsLoading();
    public LiveData<Restaurant> getRestaurantDetails(String userId);
    public LiveData<List<ItemCategory>> getRestaurantItems(String userId);
}
