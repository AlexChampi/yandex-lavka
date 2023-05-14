package ru.yandex.yandexlavka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record CompleteOrder(
        @NotNull
        @JsonProperty("courier_id")
        Long courierId,
        @NotNull
        @JsonProperty("order_id")
        Long orderId,
        @DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
        @JsonProperty("complete_time")
        LocalDateTime completeTime

) {
}
