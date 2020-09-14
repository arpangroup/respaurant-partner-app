package com.example.mainactivity.models.request;


import com.example.mainactivity.sharedpref.UserSession;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RequestToken {
    @SerializedName("user_id")
    private int userId ;//149:Arpan Restaurant Owner<====VegieCorner
    @SerializedName("restaurant_id")
    private int restaurantId ;//1: VeggieeCorner
    private String token;
    private String unique_order_id = null;
    private int order_id = 0;
    private int notification_id = 0;

    public RequestToken(int orderId) {
        //this.user_id = "149";
        this.order_id = orderId;
        try{
            if(UserSession.isLoggedIn()){
                this.userId = UserSession.getUserData().getId();
                this.token = UserSession.getUserData().getAuthToken();

            }
        }catch (Exception e){
        }
    }
    public RequestToken() {
        try{
            if(UserSession.isLoggedIn()){
                this.userId = UserSession.getUserData().getId();
                this.token = UserSession.getUserData().getAuthToken();

            }
        }catch (Exception e){
        }
    }
}
