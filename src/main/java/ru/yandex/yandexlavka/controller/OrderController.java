package ru.yandex.yandexlavka.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.dto.CouriersGroupOrders;
import ru.yandex.yandexlavka.dto.OrderDto;
import ru.yandex.yandexlavka.dto.request.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.dto.request.CreateOrderRequest;
import ru.yandex.yandexlavka.dto.response.OrderAssignResponse;
import ru.yandex.yandexlavka.exception.NotFoundException;
import ru.yandex.yandexlavka.ratelimiter.RateLimiter;
import ru.yandex.yandexlavka.service.OrderService;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @RateLimiter
    public ResponseEntity<List<OrderDto>> getOrders(Optional<Integer> limit, Optional<Integer> offset) {
        final int limitPage = limit.orElse(1);
        final int offsetPage = offset.orElse(0);
        List<OrderDto> orderDtos = orderService.findOrders(limitPage, offsetPage);
        return ResponseEntity.ok(orderDtos);

    }

    @PostMapping
    @RateLimiter
    public ResponseEntity<List<OrderDto>> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        List<OrderDto> orderDtos = orderService.creatOrders(createOrderRequest.orders());
        return ResponseEntity.ok(orderDtos);
    }

    @PostMapping("/complete")
    @RateLimiter
    public ResponseEntity<List<OrderDto>> completeOrder(@Valid @RequestBody CompleteOrderRequestDto completeOrderRequestDto) {
        List<OrderDto> order = orderService.completeOrder(completeOrderRequestDto.completeInfo());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{order_id}")
    @RateLimiter
    public ResponseEntity<OrderDto> getOrder(@PathVariable("order_id") Long orderId) {
        OrderDto order = orderService.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id=" + orderId + " not found"));
        return ResponseEntity.ok(order);
    }

    @PostMapping("/assign")
    @RateLimiter
    public ResponseEntity<OrderAssignResponse> ordersAssign(
            @RequestParam(value = "date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> date) {
        LocalDate assignmentDate = date.orElse(LocalDate.now(ZoneOffset.UTC));
        List<CouriersGroupOrders> couriersGroupOrders = orderService.ordersAssign(assignmentDate);
        return new ResponseEntity<>(new OrderAssignResponse(assignmentDate, couriersGroupOrders), HttpStatus.CREATED);
    }
}
