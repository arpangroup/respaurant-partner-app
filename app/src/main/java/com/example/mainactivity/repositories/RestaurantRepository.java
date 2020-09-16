package com.example.mainactivity.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;
import com.example.mainactivity.models.Restaurant;
import com.example.mainactivity.models.request.DisableCategoryRequest;
import com.example.mainactivity.models.request.DisableItemRequest;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.models.response.ApiResponse;

import java.util.List;

public interface RestaurantRepository {
    public LiveData<Boolean> getIsLoading();
    public LiveData<Restaurant> getRestaurantDetails(int userId);
    public LiveData<List<ItemCategory>> getRestaurantItems(int userId);
    public LiveData<ApiResponse> toggleMenuItem(DisableItemRequest disableItemRequest);
    public LiveData<ApiResponse> toggleRestaurant(RequestToken requestToken, boolean status);
    public LiveData<ApiResponse> toggleCategory(DisableCategoryRequest disableCategoryRequest);
}
