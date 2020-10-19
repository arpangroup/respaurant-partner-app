package com.pureeats.restaurant.commons;

public enum DeliveryType {
    NORMAL(1),
    SELF_PICKUP(2),
    BOTH(3);

    private final int value;
    DeliveryType(int value) {
        this.value = value;
    }

    public int value(){
        return value;
    }


}
