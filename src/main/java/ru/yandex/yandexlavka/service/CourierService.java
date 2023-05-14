package ru.yandex.yandexlavka.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.dto.CourierDto;
import ru.yandex.yandexlavka.dto.CouriersGroupOrders;
import ru.yandex.yandexlavka.dto.CreateCourierDto;
import ru.yandex.yandexlavka.dto.GroupOrders;
import ru.yandex.yandexlavka.dto.response.GetCourierMetaInfoResponse;
import ru.yandex.yandexlavka.exception.NotFoundException;
import ru.yandex.yandexlavka.mapping.CourierMapper;
import ru.yandex.yandexlavka.mapping.OrderMapper;
import ru.yandex.yandexlavka.model.AssignedOrder;
import ru.yandex.yandexlavka.model.Courier;
import ru.yandex.yandexlavka.model.Order;
import ru.yandex.yandexlavka.repository.AssignedOrderRepository;
import ru.yandex.yandexlavka.repository.CourierRepository;
import ru.yandex.yandexlavka.repository.page.OffsetLimitPageRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CourierService {
    private final CourierRepository courierRepository;
    private final AssignedOrderRepository assignedOrderRepository;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final CourierMapper courierMapper;

    public CourierService(CourierRepository courierRepository,
                          AssignedOrderRepository assignedOrderRepository, @Lazy OrderService orderService, OrderMapper orderMapper, CourierMapper courierMapper) {
        this.courierRepository = courierRepository;
        this.assignedOrderRepository = assignedOrderRepository;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.courierMapper = courierMapper;
    }


    @Transactional
    public List<CourierDto> createCourier(List<CreateCourierDto> courierDtos) {
        List<Courier> couriersMapped = courierDtos.stream().map(courierMapper::createCourierDtoToCourier).toList();
        List<Courier> couriers = courierRepository.saveAll(couriersMapped);
        return couriers.stream().map(courierMapper::courierToCourierDto).collect(Collectors.toList());
    }

    public CourierDto findCourierById(Long courierId) {
        return courierMapper.courierToCourierDto(getCourierByIdOrElseThrow(courierId));
    }

    public List<CourierDto> findCouriers(int limit, int offset) {
        return courierRepository.findAll(new OffsetLimitPageRequest(limit, offset))
                .map(courierMapper::courierToCourierDto).toList();
    }

    public GetCourierMetaInfoResponse getCourierMetaInfo(Long courierId, LocalDate startDate, LocalDate endDate) {
        CourierDto courier = findCourierById(courierId);
        List<Order> completeOrders = orderService.getCompleteOrdersByCourierBetweenDate(courierId, startDate, endDate);
        int rating;
        int earnings;
        if (completeOrders.size() == 0) {
            rating = 0;
            earnings = 0;
        } else {
            rating = calculateRating(completeOrders.size(), startDate, endDate, courier.courierType().getRatingCoefficient());
            earnings = calculateEarnings(completeOrders,
                    courier.courierType().getIncomeCoefficient(),
                    courier.courierType().getFirstOrderCostCoefficient(),
                    courier.courierType().getOtherOrderCostCoefficient());
        }
        return new GetCourierMetaInfoResponse(
                courierId,
                courier.courierType(),
                courier.regions(),
                courier.workingHours(),
                rating,
                earnings);
    }

    Courier getCourierByIdOrElseThrow(Long courierId) {
        return courierRepository.findById(courierId).orElseThrow(() -> new NotFoundException("Courier with id=" + courierId + " not found"));
    }


    List<Courier> findAllCouriers() {
        return courierRepository.findAll();
    }

    private int calculateEarnings(List<Order> orders, int coefficient, float firstOrderRatio, float otherOrderRatio) {
        return orders.stream().mapToInt(o ->
                Math.round(o.getCost() * coefficient *
                        (Objects.equals(o.getAssignedOrder().findFirstOrderDelivered(), o.getOrderId()) ? firstOrderRatio : otherOrderRatio))).sum();
    }

    private int calculateRating(int ordersAmount, LocalDate startDate, LocalDate endDate, int coefficient) {
        Duration duration = Duration.between(LocalDateTime.of(startDate, LocalTime.MIDNIGHT),
                LocalDateTime.of(endDate, LocalTime.MIDNIGHT));
        return Math.round(((float) ordersAmount / duration.toHours()) * coefficient);
    }

    public List<CouriersGroupOrders> getCourierAssignments(LocalDate assignmentDate, Integer courierId) {
        List<Courier> couriers;
        if (courierId == null) {
            couriers = courierRepository.findAll();
        } else {
            couriers = List.of(courierRepository.findById(Long.valueOf(courierId)).orElseThrow());
        }
        List<CouriersGroupOrders> result = new ArrayList<>();
        for (Courier courier : couriers) {
            List<AssignedOrder> assignedOrders = assignedOrderRepository.findAssignedOrdersByDateAndCourier(assignmentDate, courier);
            List<GroupOrders> groupOrders = new ArrayList<>();
            for (AssignedOrder order : assignedOrders) {
                groupOrders.add(new GroupOrders(order.getGroupOrderId(), order.getOrders().stream().map(orderMapper::orderToOrderDto).collect(Collectors.toList())));
            }
            result.add(new CouriersGroupOrders(courier.getCourierId(), groupOrders));
        }
        return result;
    }
}
