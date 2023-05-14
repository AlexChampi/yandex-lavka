package ru.yandex.yandexlavka.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.dto.*;
import ru.yandex.yandexlavka.exception.CompleteOrderException;
import ru.yandex.yandexlavka.exception.NotFoundException;
import ru.yandex.yandexlavka.mapping.OrderMapper;
import ru.yandex.yandexlavka.model.AssignedOrder;
import ru.yandex.yandexlavka.model.Courier;
import ru.yandex.yandexlavka.model.Order;
import ru.yandex.yandexlavka.model.TimeInterval;
import ru.yandex.yandexlavka.repository.AssignedOrderRepository;
import ru.yandex.yandexlavka.repository.OrderRepository;
import ru.yandex.yandexlavka.repository.page.OffsetLimitPageRequest;
import ru.yandex.yandexlavka.util.OrderAssign;
import ru.yandex.yandexlavka.util.Ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CourierService courierService;
    private final AssignedOrderRepository assignedOrderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        @Lazy CourierService courierService,
                        AssignedOrderRepository assignedOrderRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.courierService = courierService;
        this.assignedOrderRepository = assignedOrderRepository;
        this.orderMapper = orderMapper;
    }


    @Transactional
    public List<OrderDto> creatOrders(List<CreateOrderDto> orderDtos) {
        List<Order> ordersMapped = orderDtos.stream().map(orderMapper::createOrderDtoToOrder).collect(Collectors.toList());
        List<Order> orders = orderRepository.saveAll(ordersMapped);
        return orders.stream().map(orderMapper::orderToOrderDto).collect(Collectors.toList());
    }

    public Optional<OrderDto> findById(Long orderId) {
        return orderRepository
                .findById(orderId)
                .map(orderMapper::orderToOrderDto);
    }

    public List<OrderDto> findOrders(int limit, int offset) {
        return orderRepository.findAll(new OffsetLimitPageRequest(limit, offset))
                .map(orderMapper::orderToOrderDto).toList();
    }

    @Transactional
    public List<OrderDto> completeOrder(List<CompleteOrder> completeOrders) {
        List<Order> orders = new ArrayList<>();
        for (CompleteOrder completeOrder : completeOrders) {
            Order o = orderRepository
                    .findById(completeOrder.orderId())
                    .orElseThrow(() -> new NotFoundException("Order id=" + completeOrder.orderId() + " not found"));
            if (o.getAssignedOrder() == null ||
                    !Objects.equals(o.getAssignedOrder().getCourier().getCourierId(), completeOrder.courierId())) {
                throw new CompleteOrderException();
            }
            o.setCompletedTime(completeOrder.completeTime());
            orders.add(o);
        }
        return orders.stream().map(orderRepository::save).map(orderMapper::orderToOrderDto).collect(Collectors.toList());
    }

    private List<Ticket> splitTimeIntervalsToTickets(List<TimeInterval> timeIntervals, int firstOrderDeliveryTime) {
        List<Ticket> tickets = new ArrayList<>();
        for (TimeInterval timeInterval : timeIntervals) {
            LocalTime begin = timeInterval.getStart().plusMinutes(firstOrderDeliveryTime);
            LocalTime end = begin.plusMinutes(firstOrderDeliveryTime);
            while (timeInterval.getEnd().isAfter(end)) {
                tickets.add(new Ticket(new TimeInterval(begin, end)));
                begin = end;
                end = end.plusMinutes(firstOrderDeliveryTime);
            }
        }
        return tickets;
    }

    private OrderAssign distributeOrderByTicket(List<Ticket> tickets, Order order) {
        for (Ticket ticket : tickets) {
            if (!ticket.isUsed()) {
                for (TimeInterval interval : order.getDeliveryHours()) {
                    if (interval.containsStartOf(ticket.getTimeInterval())) {
                        ticket.setUsed(true);
                        return new OrderAssign(ticket, order);
                    }
                }
            }
        }
        return null;
    }

    public List<AssignedOrder> groupOrders(List<OrderAssign> orderAssigns, Courier courier, LocalDate date) {
        if (orderAssigns.size() == 0) {
            return new ArrayList<>();
        }
        List<AssignedOrder> orders = new ArrayList<>();
        AssignedOrder assignedOrder = new AssignedOrder();
        orders.add(assignedOrder);
        OrderAssign last = orderAssigns.get(0);
        assignedOrder.addOrder(last.order());
        assignedOrder.setCourier(courier);
        assignedOrder.setDate(date);
        for (int i = 1; i < orderAssigns.size(); i++) {
            OrderAssign current = orderAssigns.get(i);
            if (!(last.ticket().getTimeInterval().getEnd().equals(current.ticket().getTimeInterval().getStart()) &&
                    assignedOrder.getOrders().size() < courier.getCourierType().getOrdersLimit() &&
                    assignedOrder.getOrders().stream().mapToDouble(Order::getWeight).sum() + current.order().getWeight() < courier.getCourierType().getWeightLimit() &&
                    assignedOrder.getOrders().stream().map(Order::getRegions).collect(Collectors.toSet()).size() < courier.getCourierType().getOrdersLimit())) {
                assignedOrder = new AssignedOrder();
                assignedOrder.setCourier(courier);
                assignedOrder.setDate(date);
                orders.add(assignedOrder);
            }
            assignedOrder.addOrder(current.order());
            current.order().setAssignedOrder(assignedOrder);
        }
        assignedOrderRepository.saveAll(orders);
        return orders;
    }

    private List<AssignedOrder> assignOrdersToCourier(Courier courier, LocalDate date) {
        List<Ticket> tickets = splitTimeIntervalsToTickets(courier.getWorkingHours(), courier.getCourierType().getFirstOrderDeliveryTime());
        List<OrderAssign> orderAssigns = new ArrayList<>();
        for (int region : courier.getRegions()) {
            List<Order> orders = orderRepository.findAllByRegionsAndCompletedTimeAndAssignedOrder(region, null, null);
            for (Order order : orders) {
                if (order.getWeight() <= courier.getCourierType().getWeightLimit()) {
                    OrderAssign orderAssign = distributeOrderByTicket(tickets, order);
                    if (orderAssign != null) {
                        orderAssigns.add(orderAssign);
                    }
                }
            }
            if (orderAssigns.size() == tickets.size()) {
                break;
            }
        }
        return groupOrders(orderAssigns, courier, date);
    }

    @Transactional
    public List<CouriersGroupOrders> ordersAssign(LocalDate date) {
        List<AssignedOrder> orders = assignedOrderRepository.findAssignedOrdersByDate(date);
        if (orders.size() != 0) {
            return orders.stream()
                    .collect(Collectors.groupingBy(AssignedOrder::getCourier))
                    .entrySet().stream()
                    .map(aOrder ->
                            new CouriersGroupOrders(aOrder.getKey().getCourierId(),
                                    aOrder.getValue().stream()
                                            .collect(Collectors.groupingBy(AssignedOrder::getGroupOrderId))
                                            .entrySet().stream()
                                            .map(gOrder ->
                                                    new GroupOrders(gOrder.getKey(),
                                                            gOrder.getValue().stream()
                                                                    .flatMap(q -> q.getOrders().stream())
                                                                    .map(orderMapper::orderToOrderDto)
                                                                    .collect(Collectors.toList())))
                                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }
        List<CouriersGroupOrders> couriersGroupOrders = new ArrayList<>();
        List<Courier> couriers = courierService.findAllCouriers();
        for (Courier courier : couriers) {
            List<AssignedOrder> assignedOrders = assignOrdersToCourier(courier, date);
            couriersGroupOrders.add(new CouriersGroupOrders(courier.getCourierId(),
                    assignedOrders.stream()
                            .map(o ->
                                    new GroupOrders(o.getGroupOrderId(),
                                            o.getOrders().stream()
                                                    .map(orderMapper::orderToOrderDto)
                                                    .collect(Collectors.toList())))
                            .collect(Collectors.toList())));
        }
        return couriersGroupOrders;
    }

    List<Order> getCompleteOrdersByCourierBetweenDate(Long courierId, LocalDate startDate, LocalDate endDate) {
        return orderRepository.findAllByCompletedTimeBetweenAndAssignedOrder_Courier(
                LocalDateTime.of(startDate, LocalTime.MIDNIGHT),
                LocalDateTime.of(endDate, LocalTime.MIDNIGHT),
                courierService.getCourierByIdOrElseThrow(courierId)
        );
    }
}
