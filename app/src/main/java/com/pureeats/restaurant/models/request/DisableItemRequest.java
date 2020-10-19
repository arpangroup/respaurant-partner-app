package com.pureeats.restaurant.models.request;


import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DisableItemRequest extends RequestToken{
    @SerializedName("item_id")
    private int itemId;

    public DisableItemRequest(int itemId) {
        super();
        this.itemId = itemId;
    }
}
