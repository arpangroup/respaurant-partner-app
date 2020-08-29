package com.example.mainactivity.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mainactivity.api.ApiInterface;
import com.example.mainactivity.api.ApiService;
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;
import com.example.mainactivity.models.response.RestaurantItemResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepositoryStubImpl implements RestaurantRepository{
    private final String TAG = this.getClass().getSimpleName();
    private static RestaurantRepositoryStubImpl restaurantRepository;
    private MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private MutableLiveData<List<ItemCategory>> mutableMenuItems;


    public static RestaurantRepositoryStubImpl getInstance(){
        if (restaurantRepository == null){
            restaurantRepository = new RestaurantRepositoryStubImpl();
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
        isLoading.setValue(true);
        MenuItem menuItem1 = new MenuItem().setId(1).setName("Dal Makhani").setIsActive(1).setPrice("100").setIsVeg(1);
        MenuItem menuItem2 = new MenuItem().setId(2).setName("Alu Paratha").setIsActive(0).setPrice("200").setIsVeg(0);
        MenuItem menuItem3 = new MenuItem().setId(3).setName("Crispy Baby Corn").setIsActive(1).setPrice("150").setIsVeg(0);
        MenuItem menuItem4 = new MenuItem().setId(4).setName("Veg Fried Rice").setIsActive(0).setPrice("180").setIsVeg(1);
        MenuItem menuItem5 = new MenuItem().setId(5).setName("Egg Roll").setIsActive(1).setPrice("160").setIsVeg(1);
        MenuItem menuItem6 = new MenuItem().setId(6).setName("Mixed Veg Grill Sandwich").setIsActive(1).setPrice("80").setIsVeg(0);
        MenuItem menuItem7 = new MenuItem().setId(7).setName("Crispy American Coern").setIsActive(0).setPrice("50");
        MenuItem menuItem8 = new MenuItem().setId(8).setName("Egg Curry").setIsActive(1).setPrice("120").setIsVeg(1);
        MenuItem menuItem9 = new MenuItem().setId(9).setName("Egg Roll").setIsActive(1).setPrice("180");
        MenuItem menuItem10 = new MenuItem().setId(10).setName("Veg Rice").setIsActive(0).setPrice("200");
        MenuItem menuItem11 = new MenuItem().setId(11).setName("Chicken Tikka").setIsActive(1).setPrice("70").setIsVeg(1);

        List<MenuItem> menuItems1 = Arrays.asList(menuItem1, menuItem2, menuItem3, menuItem4);
        List<MenuItem> menuItems2 = Arrays.asList(menuItem5, menuItem2, menuItem4);
        List<MenuItem> menuItems3 = Arrays.asList(menuItem7, menuItem5, menuItem3, menuItem10, menuItem2, menuItem6);
        List<MenuItem> menuItems4 = Arrays.asList(menuItem6, menuItem2, menuItem5, menuItem4, menuItem11);
        List<MenuItem> menuItems5 = Arrays.asList(menuItem1, menuItem10, menuItem9, menuItem11);

        List<ItemCategory> categories = Arrays.asList(
                new ItemCategory(1, "Dinner", 1, menuItems1),
                new ItemCategory(2, "Pasta", 1, menuItems2),
                new ItemCategory(3, "Roll", 1,menuItems3),
                new ItemCategory(4, "Momo", 1,menuItems4),
                new ItemCategory(5, "Rice", 1,menuItems5)
        );

        mutableMenuItems.setValue(categories);
        isLoading.setValue(false);
    }
}
