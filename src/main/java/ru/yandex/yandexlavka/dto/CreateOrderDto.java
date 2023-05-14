package ru.yandex.yandexlavka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.yandex.yandexlavka.validation.TimeIntervals;

import java.util.List;

public record CreateOrderDto(
        @Min(0)
        @Max(100)
        Float weight,

        @NotNull
        @Min(0)
        @Max(200)
        Integer regions,
        @NotEmpty
        @TimeIntervals
        @JsonProperty("delivery_hours")
        List<String> deliveryHours,
        @NotNull
        @Min(0)
        Integer cost
) {
}
