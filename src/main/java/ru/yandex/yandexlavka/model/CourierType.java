package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CourierType {
    FOOT("FOOT", 3, 2, 10, 2, 25, 10, 1, 0.8f),

    BIKE("BIKE", 2, 3, 20, 4, 12, 8, 1, 0.8f),

    AUTO("AUTO", 1, 4, 40, 7, 8, 4, 1, 0.8f);

    private final String value;
    private final int ratingCoefficient;
    private final int earningCoefficient;

    private final int weightLimit;
    private final int ordersLimit;
    private final int firstOrderDeliveryTime;
    private final int otherOrdersDeliveryTime;

    private final float firstOrderCostCoefficient;
    private final float otherOrderCostCoefficient;

    CourierType(String value, int ratingCoefficient, int earningCoefficient, int weightLimit, int ordersLimit, int firstOrderDeliveryTime, int otherOrdersDeliveryTime, float firstOrderCostCoefficient, float otherOrderCostCoefficient) {
        this.value = value;
        this.ratingCoefficient = ratingCoefficient;
        this.earningCoefficient = earningCoefficient;
        this.weightLimit = weightLimit;
        this.ordersLimit = ordersLimit;
        this.firstOrderDeliveryTime = firstOrderDeliveryTime;
        this.otherOrdersDeliveryTime = otherOrdersDeliveryTime;
        this.firstOrderCostCoefficient = firstOrderCostCoefficient;
        this.otherOrderCostCoefficient = otherOrderCostCoefficient;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static CourierType fromValue(String value) {
        for (CourierType b : CourierType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public int getRatingCoefficient() {
        return ratingCoefficient;
    }

    public int getIncomeCoefficient() {
        return earningCoefficient;
    }

    public int getOtherOrdersDeliveryTime() {
        return otherOrdersDeliveryTime;
    }

    public int getFirstOrderDeliveryTime() {
        return firstOrderDeliveryTime;
    }

    public int getOrdersLimit() {
        return ordersLimit;
    }

    public int getWeightLimit() {
        return weightLimit;
    }

    public float getFirstOrderCostCoefficient() {
        return firstOrderCostCoefficient;
    }

    public float getOtherOrderCostCoefficient() {
        return otherOrderCostCoefficient;
    }
}
