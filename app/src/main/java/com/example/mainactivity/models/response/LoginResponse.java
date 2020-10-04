package com.example.mainactivity.models.response;

import com.example.mainactivity.models.Restaurant;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@ToString
public class LoginResponse<T> {
    private boolean success;
    private String message;
    private T data;
    @SerializedName("running_order")
    private T runningOrder;
}
