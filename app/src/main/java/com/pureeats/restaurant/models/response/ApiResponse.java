package com.pureeats.restaurant.models.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
}
