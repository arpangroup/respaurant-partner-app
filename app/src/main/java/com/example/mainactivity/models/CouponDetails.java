package com.example.mainactivity.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class CouponDetails {
    private int id;
    private String name;
    private  String description;
    private String code;
    @SerializedName("discount_type")
    private String discountType;
    private String discount;
    @SerializedName("expiryDate")
    private String expiry_date;
    @SerializedName("is_active")
    private int isActive;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("restaurant_id")
    private int restaurantId;
    private String min_order_amount;
    @SerializedName("total_coupon")
    private int totalCoupon;
    private int count;
    @SerializedName("max_count")
    private int maxCount;
    @SerializedName("upto_amount")
    private String uptoAmount;

    private double appliedCouponAmount = 0.0;


    public static double getDiscountAmount(double totalOrderAmount, CouponDetails couponDetails){
        double discountAmount = 0.0;
        double couponAmount = Double.parseDouble(couponDetails.getDiscount());
        if(couponDetails.getDiscountType().equals("PERCENTAGE")) discountAmount =  (totalOrderAmount * couponAmount) / 100;
        else discountAmount = couponAmount;


        double uptoAmount = Double.parseDouble(couponDetails.getUptoAmount());
        if(discountAmount > uptoAmount) discountAmount = uptoAmount;

        return discountAmount;
    }
}
