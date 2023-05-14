package ru.yandex.yandexlavka.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.dto.CourierDto;
import ru.yandex.yandexlavka.dto.CouriersGroupOrders;
import ru.yandex.yandexlavka.dto.request.CreateCourierRequest;
import ru.yandex.yandexlavka.dto.response.CreateCouriersResponse;
import ru.yandex.yandexlavka.dto.response.GetCourierMetaInfoResponse;
import ru.yandex.yandexlavka.dto.response.GetCouriersResponse;
import ru.yandex.yandexlavka.dto.response.OrderAssignResponse;
import ru.yandex.yandexlavka.ratelimiter.RateLimiter;
import ru.yandex.yandexlavka.service.CourierService;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/couriers")
@Validated
public class CourierController {
    private final CourierService courierService;

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping
    @RateLimiter
    public ResponseEntity<GetCouriersResponse> getCouriers(@Valid @RequestParam Optional<@Min(1) Integer> limit,
                                                           @Valid @RequestParam Optional<@Min(0) Integer> offset) {
        final int limitPage = limit.orElse(1);
        final int offsetPage = offset.orElse(0);
        List<CourierDto> couriers = courierService.findCouriers(limitPage, offsetPage);
        return ResponseEntity.ok(new GetCouriersResponse(couriers, limitPage, offsetPage));
    }

    @PostMapping
    @RateLimiter
    public ResponseEntity<CreateCouriersResponse> createCourier(@Valid @RequestBody CreateCourierRequest createCourierRequest) {
        CreateCouriersResponse response = new CreateCouriersResponse(
                courierService.createCourier(createCourierRequest.couriers()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courier_id}")
    @RateLimiter
    public ResponseEntity<CourierDto> getCourierById(@PathVariable("courier_id") Long courierId) {
        CourierDto courier = courierService.findCourierById(courierId);
        return ResponseEntity.ok(courier);
    }

    @GetMapping("/meta-info/{courier_id}")
    @RateLimiter
    public ResponseEntity<GetCourierMetaInfoResponse> getCourierMetaInfo(
            @PathVariable("courier_id") Long courierId,
            @RequestParam(value = "startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        GetCourierMetaInfoResponse response = courierService.getCourierMetaInfo(courierId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assignments")
    @RateLimiter
    public ResponseEntity<OrderAssignResponse> couriersAssignments(@RequestParam("date")
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> date,
                                                   @RequestParam("courier_id") Optional<Integer> courier) {
        LocalDate assignmentDate = date.orElse(LocalDate.now(ZoneOffset.UTC));
        Integer courierId = courier.orElse(null);
        List<CouriersGroupOrders> couriers = courierService.getCourierAssignments(assignmentDate, courierId);
        return ResponseEntity.ok(new OrderAssignResponse(assignmentDate, couriers));
    }
}
