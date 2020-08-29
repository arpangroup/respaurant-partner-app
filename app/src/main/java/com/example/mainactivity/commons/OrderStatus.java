package com.example.mainactivity.commons;

public enum OrderStatus {
    ORDER_PLACED(1), // WaitingForRestaurantToAcceptOrder           [NEW_ORDER]
    ORDER_RECEIVED(2),//Restaurant is preparing your order          [PREPARING]
    DELIVERY_GUY_ASSIGNED(3), //On the way to pikup you order       [PREPARING]
    ON_THE_WAY(4),// Order is pickedUp and is on its way to deliver [PICKED]
    DELIVERED(5),
    CANCELED(6),
    SELF_PICKUP(7);


    private final int value;
    OrderStatus(int value) {
        this.value = value;
    }

    public int value(){
        return value;
    }
}
