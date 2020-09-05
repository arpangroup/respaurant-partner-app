package com.example.mainactivity.models.request;


import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RequestToken {
    @SerializedName("user_id")
    private String user_id = "149";//Arpan Restaurant Owner<====VegieCorner
    @SerializedName("restaurant_id")
    private int restaurantId = 1;// VeggieeCorner
    private String token;
    private String unique_order_id = null;
    private int order_id = 0;
    private int notification_id = 0;

    public RequestToken(int orderId) {
        this.user_id = "149";
        this.order_id = orderId;
    }
    public RequestToken() {
//        try{
//            if(UserSession.isLoggedIn()){
//                this.user_id = UserSession.getUserData().getId()+"";
//                this.token = UserSession.getUserData().getAuthToken();
//
//            }
//        }catch (Exception e){
//        }
    }
}
