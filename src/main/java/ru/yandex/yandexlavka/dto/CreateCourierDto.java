package ru.yandex.yandexlavka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.yandex.yandexlavka.model.CourierType;
import ru.yandex.yandexlavka.validation.TimeIntervals;
import ru.yandex.yandexlavka.validation.ValueOfEnum;

import java.util.List;

public record CreateCourierDto(
        @NotNull
        @ValueOfEnum(enumClass = CourierType.class)
        @JsonProperty("courier_type")
        String courierType,
        @NotEmpty
        List<@Max(100) @Min(0) Integer> regions,
        @NotEmpty
        @TimeIntervals
        @JsonProperty("working_hours")
        List<String> workingHours) {
}