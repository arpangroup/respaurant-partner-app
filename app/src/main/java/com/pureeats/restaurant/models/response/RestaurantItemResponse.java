package com.pureeats.restaurant.models.response;

import com.pureeats.restaurant.models.MenuItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RestaurantItemResponse {
    @SerializedName("items")
    private Map<String, List<MenuItem>> items ;
}
