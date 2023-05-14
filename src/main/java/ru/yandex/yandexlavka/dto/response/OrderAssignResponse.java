package ru.yandex.yandexlavka.dto.response;

import ru.yandex.yandexlavka.dto.CouriersGroupOrders;

import java.time.LocalDate;
import java.util.List;

public record OrderAssignResponse(
        LocalDate date,
        List<CouriersGroupOrders> couriers
        ) {
}
