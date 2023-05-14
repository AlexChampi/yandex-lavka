package ru.yandex.yandexlavka.dto;

import org.springframework.lang.NonNull;
import ru.yandex.yandexlavka.model.CourierType;

import java.util.List;

public record CourierDto(
        @NonNull
        Long courierId,
        @NonNull
        CourierType courierType,
        @NonNull
        List<Integer> regions,
        @NonNull
        List<String> workingHours
) {
}

