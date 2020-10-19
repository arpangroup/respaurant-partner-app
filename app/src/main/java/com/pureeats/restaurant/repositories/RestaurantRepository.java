package com.pureeats.restaurant.repositories;

import androidx.lifecycle.LiveData;

import com.pureeats.restaurant.models.ItemCategory;
import com.pureeats.restaurant.models.Restaurant;
import com.pureeats.restaurant.models.request.DisableCategoryRequest;
import com.pureeats.restaurant.models.request.DisableItemRequest;
import com.pureeats.restaurant.models.request.RequestToken;
import com.pureeats.restaurant.models.response.ApiResponse;
import com.pureeats.restaurant.models.response.Dashboard;

import java.util.List;

public interface RestaurantRepository {
    public LiveData<Boolean> getIsLoading();
    public void loadDashboard(int userId);
    public LiveData<Dashboard> getDashboard();
    public LiveData<Restaurant> getRestaurantDetails(int userId);
    public LiveData<List<ItemCategory>> getRestaurantItems(int userId);
    public LiveData<ApiResponse> toggleMenuItem(DisableItemRequest disableItemRequest);
    public LiveData<ApiResponse> toggleRestaurant(RequestToken requestToken, boolean status);
    public LiveData<ApiResponse> toggleCategory(DisableCategoryRequest disableCategoryRequest);
}
