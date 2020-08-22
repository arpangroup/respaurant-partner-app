package com.example.mainactivity.models.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApiResponse {
    private boolean success;
    private String message;
}
