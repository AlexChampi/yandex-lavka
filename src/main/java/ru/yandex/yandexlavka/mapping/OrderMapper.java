package ru.yandex.yandexlavka.mapping;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.dto.CreateOrderDto;
import ru.yandex.yandexlavka.dto.OrderDto;
import ru.yandex.yandexlavka.model.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class OrderMapper {
    private final CommonMapper commonMapper;

    public OrderMapper(CommonMapper commonMapper) {this.commonMapper = commonMapper;}


    public Order createOrderDtoToOrder(CreateOrderDto createOrderDto) {
        if (createOrderDto == null) {
            return null;
        }

        Order order = new Order();

        order.setDeliveryHours(commonMapper.mapStringsToTimeIntervals(createOrderDto.deliveryHours()));
        order.setWeight(createOrderDto.weight());
        order.setRegions(createOrderDto.regions());
        order.setCost(createOrderDto.cost());

        return order;
    }

    public OrderDto orderToOrderDto(Order order) {
        if (order == null) {
            return null;
        }

        List<String> deliveryHours = commonMapper.mapTimeIntervalsToStrings(order.getDeliveryHours());
        Long orderId = order.getOrderId();
        Float weight = order.getWeight();
        Integer regions = order.getRegions();
        Integer cost = order.getCost();
        String completedTime = null;
        if (order.getCompletedTime() != null) {
            completedTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(order.getCompletedTime());
        }

        return new OrderDto(orderId, weight, regions, deliveryHours, cost, completedTime);
    }
}
