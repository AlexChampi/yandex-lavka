package ru.yandex.yandexlavka.dto;

import java.util.List;

public record CouriersGroupOrders(
        Long courierId,
        List<GroupOrders> orders) {
}
