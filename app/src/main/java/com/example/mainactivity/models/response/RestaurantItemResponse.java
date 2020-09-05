package com.example.mainactivity.models.response;

import com.example.mainactivity.models.MenuItem;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RestaurantItemResponse {
    @SerializedName("items")
    private Map<String, List<MenuItem>> items ;
}
