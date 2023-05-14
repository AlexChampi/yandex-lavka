package ru.yandex.yandexlavka.dto.response;

import ru.yandex.yandexlavka.model.CourierType;

import java.util.List;

public record GetCourierMetaInfoResponse(
        Long courierId,
        CourierType courierType,
        List<Integer> regions,
        List<String> workingHours,
        Integer rating,
        Integer earnings
) {
}
