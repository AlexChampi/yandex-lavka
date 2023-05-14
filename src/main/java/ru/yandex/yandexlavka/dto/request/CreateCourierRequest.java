package ru.yandex.yandexlavka.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import ru.yandex.yandexlavka.dto.CreateCourierDto;

import java.util.List;

public record CreateCourierRequest(@NotEmpty List<@Valid CreateCourierDto> couriers) {
}
