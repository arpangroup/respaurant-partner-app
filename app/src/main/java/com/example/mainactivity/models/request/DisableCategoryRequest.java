package com.example.mainactivity.models.request;


import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DisableCategoryRequest extends RequestToken{
    @SerializedName("category_id")
    private int categoryId;

    public DisableCategoryRequest(int categoryId) {
        this.categoryId = categoryId;
    }
}
