package com.example.mainactivity.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;
import com.example.mainactivity.models.Restaurant;
import com.example.mainactivity.models.request.DisableCategoryRequest;
import com.example.mainactivity.models.request.DisableItemRequest;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.models.response.ApiResponse;
import com.example.mainactivity.repositories.RestaurantRepository;
import com.example.mainactivity.repositories.RestaurantRepositoryImpl;
import com.example.mainactivity.repositories.RestaurantRepositoryStubImpl;

import java.util.List;

public class RestaurantViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private RestaurantRepository restaurantRepository;
    private MutableLiveData<ItemCategory> categoryMutableLiveData;
    private MutableLiveData<MenuItem> menuMutableLiveData;

    private LiveData<List<MenuItem>> mItems;

    public void init(){
        if (mItems != null){
            return;
        }
        restaurantRepository = RestaurantRepositoryImpl.getInstance();
    }

    public LiveData<Boolean> getIsLoading(){
        LiveData<Boolean> isLoading=restaurantRepository.getIsLoading();
        return isLoading;
    }
    public LiveData<Restaurant> getRestaurantDetails(){
        String userId = new RequestToken().getUser_id();
        return restaurantRepository.getRestaurantDetails(userId);
    }
    public LiveData<List<ItemCategory>> getRestaurantsMenuItems(){
        String userId = new RequestToken().getUser_id();
        return restaurantRepository.getRestaurantItems(userId);
    }

    public void setCategory(ItemCategory category){
        if (categoryMutableLiveData == null){
            categoryMutableLiveData = new MutableLiveData<>();
        }
        categoryMutableLiveData.setValue(category);
    }
    public LiveData<ItemCategory> getCategory(){
        if (categoryMutableLiveData == null){
            categoryMutableLiveData = new MutableLiveData<>();
        }
       return categoryMutableLiveData;
    }
    public void setMenuItem(MenuItem menuItem){
        if (menuMutableLiveData == null){
            menuMutableLiveData = new MutableLiveData<>();
        }
        menuMutableLiveData.setValue(menuItem);
    }
    public LiveData<MenuItem> getMenuItem(){
        if (menuMutableLiveData == null){
            menuMutableLiveData = new MutableLiveData<>();
        }
        return menuMutableLiveData;
    }

    public LiveData<ApiResponse> toggleMenuItem(int itemId){
        DisableItemRequest disableItemRequest = new DisableItemRequest(itemId);
        return restaurantRepository.toggleMenuItem(disableItemRequest);
    }
    public LiveData<ApiResponse> toggleRestaurant(){
        RequestToken requestToken  = new RequestToken();
        return restaurantRepository.toggleRestaurant(requestToken);
    }
    public LiveData<ApiResponse> toggleCategory(int categoryId){
        DisableCategoryRequest disableCategoryRequest  = new DisableCategoryRequest(categoryId);
        return restaurantRepository.toggleCategory(disableCategoryRequest);
    }

}
