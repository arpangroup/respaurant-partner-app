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

import java.util.List;

public class RestaurantViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private RestaurantRepository restaurantRepository;
    private ItemCategory mSelectedCategory = null;
    private MutableLiveData<List<ItemCategory>> mutableItemCategories = null;
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
        int userId = new RequestToken().getUserId();
        return restaurantRepository.getRestaurantDetails(userId);
    }
    public LiveData<List<ItemCategory>> getRestaurantsMenuItems(){
        int userId = new RequestToken().getUserId();
        return restaurantRepository.getRestaurantItems(userId);
    }

    public void setCategory(ItemCategory category){
        mSelectedCategory = category;
    }
    public LiveData<ItemCategory> getSelectedCategory(){
        MutableLiveData<ItemCategory> mutableLiveData = null;
       if(mSelectedCategory != null){
           mutableLiveData = new MutableLiveData<>();
           mutableLiveData.setValue(mSelectedCategory);
       }
       return mutableLiveData;
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

    public void setFilterMenus(List<ItemCategory> categoryList){
        if(mutableItemCategories == null){
            mutableItemCategories = new MutableLiveData<>();
        }
        mutableItemCategories.setValue(categoryList);
    }
    public LiveData<List<ItemCategory>> getAllFilteredMenus(){
        if(mutableItemCategories == null){
            mutableItemCategories = new MutableLiveData<>();
        }
        return mutableItemCategories;
    }

    public LiveData<ApiResponse> toggleMenuItem(int itemId){
        DisableItemRequest disableItemRequest = new DisableItemRequest(itemId);
        return restaurantRepository.toggleMenuItem(disableItemRequest);
    }
    public LiveData<ApiResponse> toggleRestaurant(boolean status){
        RequestToken requestToken  = new RequestToken();
        return restaurantRepository.toggleRestaurant(requestToken, status);
    }
    public LiveData<ApiResponse> toggleCategory(int categoryId){
        DisableCategoryRequest disableCategoryRequest  = new DisableCategoryRequest(categoryId);
        return restaurantRepository.toggleCategory(disableCategoryRequest);
    }

}
