package com.example.mainactivity.models.request;


import android.content.Context;

import com.example.mainactivity.models.User;
import com.example.mainactivity.sharedpref.UserSession;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class RequestToken {
    @SerializedName("user_id")
    private int userId ;//149:Arpan Restaurant Owner<====VegieCorner
    @SerializedName("restaurant_id")
    private int restaurantId ;//1: VeggieeCorner; 3=>HoteloLaxmi
    private String token;

    //private String unique_order_id = null;
    //private int order_id = 0;
    //private int notification_id = 0;

//    public RequestToken(int orderId) {
//        //this.user_id = "149";
//        //this.order_id = orderId;
//        try{
//            if(UserSession.isLoggedIn()){
//                this.userId = UserSession.getUserData().getId();
//                this.token = UserSession.getUserData().getAuthToken();
//                if(UserSession.getRestaurantData() != null){
//                    this.restaurantId = UserSession.getRestaurantData().getId();
//                }
//            }
//        }catch (Exception e){
//        }
//    }
    public RequestToken() {
        try{
            if(UserSession.isLoggedIn()){
                User user = UserSession.getUserData();
                if(user != null){
                    this.userId = user.getId();
                    this.restaurantId = user.getRestaurants().get(0).getId();
                    this.token = UserSession.getUserData().getAuthToken();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public RequestToken(Context context) {
        try{
            if(UserSession.isLoggedIn()){
                this.userId = UserSession.getUserData(context).getId();
                this.token = UserSession.getUserData().getAuthToken();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
