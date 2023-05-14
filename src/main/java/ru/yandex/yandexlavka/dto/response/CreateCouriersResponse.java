package ru.yandex.yandexlavka.dto.response;

import ru.yandex.yandexlavka.dto.CourierDto;

import java.util.List;

public record CreateCouriersResponse(List<CourierDto> couriers) {}