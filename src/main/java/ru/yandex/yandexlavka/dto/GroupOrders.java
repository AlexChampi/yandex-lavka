package ru.yandex.yandexlavka.dto;

import java.util.List;
public record GroupOrders(
        Long groupOrderId,
        List<OrderDto> orders
) {
}
