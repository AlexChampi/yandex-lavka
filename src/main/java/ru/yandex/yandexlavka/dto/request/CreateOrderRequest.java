package ru.yandex.yandexlavka.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import ru.yandex.yandexlavka.dto.CreateOrderDto;

import java.util.List;

public record CreateOrderRequest(@NotEmpty List<@Valid CreateOrderDto> orders) {
}
