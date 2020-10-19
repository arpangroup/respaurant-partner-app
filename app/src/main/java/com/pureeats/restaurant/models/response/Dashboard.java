package com.pureeats.restaurant.models.response;

import com.pureeats.restaurant.models.Order;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Dashboard {
    private int restaurantsCount;
    private int ordersCount;
    private int orderItemsCount;
    //Earning:
    private double todaysTotalEarning;
    private double yesterdaysTotalEarning;
    private double oneWeekTotalEarning;
    private double oneMonthTotalEarning;
    private double totalEarning;
    //Loss:
    private double todaysTotalLoss;
    private double yesterdaysTotalLoss;
    private double oneWeekTotalLoss;
    private double oneMonthTotalLoss;
    //EarningOrders:
    private List<Order> todaysAllCompletedOrders;
    private List<Order> yesterdaysAllCompletedOrders;
    private List<Order> oneWeekAllCompletedOrders;
    private List<Order> oneMonthAllCompletedOrder;
    private List<Order> newOrders;
    private List<Integer> newOrdersIds;
    private List<Order> acceptedOrders;
    private List<Order> allOrders;
    //CancelledOrders:
    private List<Order> todaysAllCancelledOrders;
    private List<Order> yesterdaysAllCancelledOrders;
    private List<Order> oneWeeksAllCancelledOrders;
    private List<Order> oneMonthsAllCancelledOrders;

}
