package com.example.mainactivity.models.response;

import com.example.mainactivity.models.Order;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Dashboard {
    private int restaurantsCount;
    private int ordersCount;
    private int orderItemsCount;
    private double totalEarning;
    private List<Order> newOrders;
    private List<Integer> newOrdersIds;
    private List<Order> acceptedOrders;
    private List<Order> allOrders;

}
