package ru.yandex.yandexlavka.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import ru.yandex.yandexlavka.dto.CompleteOrder;

import java.util.List;

public record CompleteOrderRequestDto(@NotEmpty @JsonProperty("complete_info") List<CompleteOrder> completeInfo) {
}
