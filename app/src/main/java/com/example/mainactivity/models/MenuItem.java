package com.example.mainactivity.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class MenuItem {
    @SerializedName("id")
    private int id;

    @SerializedName("restaurant_id")
    private int restaurantId;

    @SerializedName("item_category_id")
    private int itemCategoryId;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private String price;

    @SerializedName("old_price")
    private String oldPrice;

    @SerializedName("image")
    private String image;

    @SerializedName("is_recommended")
    private int isRecommended;

    @SerializedName("is_popular")
    private int isPopular;

    @SerializedName("is_new")
    private int isNew;

    @SerializedName("desc")
    private String desc;

    @SerializedName("placeholder_image")
    private String placeHolderImage;

    @SerializedName("is_active")
    private int isActive;

    @SerializedName("is_veg")
    private int isVeg;

    @SerializedName("item_category")
    private ItemCategory itemCategory;

    private Restaurant restaurant;



    public static DiffUtil.ItemCallback<MenuItem> itemCallback = new DiffUtil.ItemCallback<MenuItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return false;
        }
    };

}
