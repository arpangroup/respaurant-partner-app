package com.pureeats.restaurant.models;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.restaurant.adapters.DishListAdapter;
import com.pureeats.restaurant.commons.Constants;
import com.pureeats.restaurant.commons.DeliveryType;
import com.pureeats.restaurant.commons.OrderStatus;
import com.pureeats.restaurant.util.CommonUtils;
import com.pureeats.restaurant.util.FormatDate;
import com.pureeats.restaurant.util.FormatPrice;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Order {
    private int id;
    @SerializedName("unique_order_id")
    private String uniqueOrderId;
    @SerializedName("orderstatus_id")
    private int orderStatusId; //[1=>ORDER_PLACED, 2=>ORDER_RECEIVED, 3=>DELIVERY_GUY_ASSIGNED, 4=>ON_THE_WAY, 5=>DELIVERED, 6=>CANCELED, 7=>SELF_PICKUP]
    private int user_id;
    private String coupon_name;
    private String location;
    private String address;
    private String tax;
    @SerializedName("restaurant_charge")
    private int restaurantCharge;
    @SerializedName("delivery_charge")
    private String deliveryCharge;
    private double total;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("payment_mode")
    private String paymentMode;
    @SerializedName("order_comment")
    private String orderComment;
    @SerializedName("restaurant_id")
    private int restaurantId;
    //    @SerializedName("transaction_id")
//    private String transactionId;
    @SerializedName("delivery_type")
    private int deliveryType;
    @SerializedName("delivery_pin")
    private String deliveryPin;
    @SerializedName("payable")
    private String payable;
    @SerializedName("prepare_time")
    private int prepareTime;

    @SerializedName("restaurant")
    private Restaurant restaurant;

    @SerializedName("discount_amount")
    private String discountAmount;

    @SerializedName("item_total")
    private String itemTotal;

//    @SerializedName("resturant_details")
//    private Restaurant restaurantDetails;

    @SerializedName("coupon_details")
    private CouponDetails couponDetails;

    @SerializedName("orderitems")
    private List<Dish> orderitems;


    @SerializedName("delivery_details")
    private DeliveryGuy deliveryDetails;

    @SerializedName("user")
    private User user;

    public Order(int id, String uniqueOrderId) {
        this.id = id;
        this.uniqueOrderId = uniqueOrderId;
    }
    public String billAmount(){
        try {
            double itemTotal = Double.parseDouble(this.itemTotal);
            double discount = Double.parseDouble(this.discountAmount);
            double billAmount = itemTotal - discount;
            return FormatPrice.formatDecimalPoint(billAmount);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @BindingAdapter("android:driverImage")
    public static void loadDriverImage(ImageView imageView, String imageUrl){
        //System.out.println("=======================================");
        //System.out.println(Constants.ASSET_URL_DELIVERY_GUY + imageUrl);
        //System.out.println("=======================================");
        Picasso.get().load(Constants.ASSET_URL_DELIVERY_GUY + imageUrl).into(imageView);
    }





    /*========================================================================*/
    private int toggle = 1;
    private boolean isAutoCancelled = false;


    @BindingAdapter(value = "setDishes")
    public static void setDishes(RecyclerView recyclerView, List<Dish> dishes){
        if(dishes != null){
            DishListAdapter dishListAdapter = new DishListAdapter();
            dishListAdapter.submitList(dishes);
            recyclerView.setAdapter(dishListAdapter);
        }
    }

    public String getOrderTime(){
        if(this.createdAt == null) return "";
        return FormatDate.format2(this.createdAt);
    }
    public String getOrderDate(){
        if(this.createdAt == null) return "";
        return FormatDate.format3(this.createdAt);
    }
    public String getAcceptedOrderStatus(){
        String statusStr = "";
        OrderStatus orderStatus = CommonUtils.mapOrderStatus(this.orderStatusId);
        if (orderStatus == null) return statusStr;
        switch (orderStatus){
            case ORDER_PLACED:
                statusStr = "PLACED";//ORDER-PLACED
                break;
            case ORDER_RECEIVED:
                //ORDER_RECEIVED: i.e, restaurant preparing the order
                statusStr = "PREPARING"; //ORDER_RECEIVED
                break;
            case DELIVERY_GUY_ASSIGNED:
                statusStr = "DELIVERY ASSIGNED";//DELIVERY ASSIGNED
                break;
            case ON_THE_WAY:
                statusStr = "PICKED-UP";//ON_THE_WAY
                break;
            case DELIVERED:
                statusStr = "DELIVERED";
                break;
            case CANCELED:
                statusStr = "CANCELLED";
                break;
            case ORDER_READY:
                statusStr = "READY";
                break;
            case REACHED_PICKUP_LOCATION:
                statusStr = "RIDER_REACHED";
                break;
            case REACHED_DELIVERY_LOCATION:
                statusStr = "PICKED-UP";//RIDER_REACHED_TO_DELIVERY_LOCATION
                break;
            case READY_FOR_PICKUP:
                statusStr = "READY_TO_PICKUP";
                break;
        }
        return statusStr;
    }

    public boolean isRiderDetailsVisible(){
        OrderStatus orderStatus = CommonUtils.mapOrderStatus(this.orderStatusId);
        List<OrderStatus> checkList = Arrays.asList(
                OrderStatus.DELIVERY_GUY_ASSIGNED,
                OrderStatus.REACHED_PICKUP_LOCATION,
                OrderStatus.READY_FOR_PICKUP
        );
        boolean isExistInCheckList = checkList.stream().anyMatch(orderStatus1 -> orderStatus1 == orderStatus);
        return isExistInCheckList && this.deliveryType != DeliveryType.SELF_PICKUP.value();
    }

    public String getRiderTitle(DeliveryGuy deliveryGuy){
        OrderStatus orderStatus = CommonUtils.mapOrderStatus(this.orderStatusId);
        if(orderStatus == OrderStatus.DELIVERY_GUY_ASSIGNED){
            return this.deliveryDetails.getName() + " is arriving in few minutes";
        }else if(orderStatus == OrderStatus.REACHED_PICKUP_LOCATION){
            return this.deliveryDetails.getName() + " is reached the restaurant";
        }else if(orderStatus == OrderStatus.READY_FOR_PICKUP){
            return deliveryGuy.getName() + " is ready to pickup the order";
        }else if(orderStatus == OrderStatus.ON_THE_WAY){
            return deliveryGuy.getName() + " is on the way to deliver the order";
        }else{
            return "";
        }
    }

    public boolean isOtpVisible(){
        OrderStatus orderStatus = CommonUtils.mapOrderStatus(this.orderStatusId);
        List<OrderStatus> checkList = Arrays.asList(
                OrderStatus.READY_FOR_PICKUP
        );
        boolean isExistInCheckList = checkList.stream().anyMatch(orderStatus1 -> orderStatus1 == orderStatus);
        return isExistInCheckList && this.deliveryType != DeliveryType.SELF_PICKUP.value();
    }

    public String getDeliveryOtp(){
        int lastIndex = this.uniqueOrderId.length();
        String otp = this.uniqueOrderId.substring(lastIndex-4);
        return otp;
    }

    public boolean isOrderReadyButtonVisible(){
        OrderStatus orderStatus = CommonUtils.mapOrderStatus(this.orderStatusId);
        List<OrderStatus> checkList = Arrays.asList(
                OrderStatus.ORDER_RECEIVED,
                OrderStatus.DELIVERY_GUY_ASSIGNED
                //OrderStatus.REACHED_PICKUP_LOCATION
        );
        return checkList.stream().anyMatch(orderStatus1 -> orderStatus1 == orderStatus);
    }

    public String getHistoryOrderStatus(){
        String statusStr = "";
        switch (this.orderStatusId){
            case 1:
                statusStr = "PLACED";//ORDER-PLACED
                break;
            case 2:
                //ORDER_RECEIVED: i.e, restaurant preparing the order
                statusStr = "PREPARING"; //ORDER_RECEIVED
                break;
            case 3:
                statusStr = "DELIVERY ASSIGNED";//DELIVERY ASSIGNED
                break;
            case 4:
                statusStr = "PICKED-UP";//ON_THE_WAY
                break;
            case 5:
                statusStr = "DELIVERED";
                break;
            case 6:
                statusStr = "CANCELLED";
                break;
            case 7:
                statusStr = "READY";
                break;
        }
        return statusStr;
    }


    public static DiffUtil.ItemCallback<Order> itemCallback = new DiffUtil.ItemCallback<Order>() {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            //return oldItem.getId() == newItem.getId() && oldItem.getRestaurant().getDeliveryTime().equals(newItem.getRestaurant().getDeliveryTime());
            //return oldItem.getId() == newItem.getId() ;
            return oldItem.getId() == newItem.getId() && oldItem.getOrderStatusId() == newItem.getOrderStatusId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.equals(newItem);
        }
    };



}