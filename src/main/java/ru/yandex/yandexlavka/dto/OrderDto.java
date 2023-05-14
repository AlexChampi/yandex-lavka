package ru.yandex.yandexlavka.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

public record OrderDto(
        @NotNull
        Long orderId,
        @NotNull
        Float weight,
        @NotNull
        Integer regions,
        @NotNull
        List<String> deliveryHours,
        @NotNull
        Integer cost,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        String completedTime

) {
}
