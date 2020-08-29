package com.example.mainactivity.models.response;

import com.example.mainactivity.models.MenuItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class RestaurantItemResponse {
    @SerializedName("data")
    private List<MenuItem> data;
}
