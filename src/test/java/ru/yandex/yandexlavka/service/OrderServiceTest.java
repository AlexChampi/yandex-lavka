package ru.yandex.yandexlavka.service;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.yandexlavka.dto.CreateOrderDto;
import ru.yandex.yandexlavka.dto.OrderDto;
import ru.yandex.yandexlavka.mapping.CommonMapper;
import ru.yandex.yandexlavka.mapping.OrderMapper;
import ru.yandex.yandexlavka.model.Order;
import ru.yandex.yandexlavka.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Spy
    private final OrderMapper orderMapper = new OrderMapper(new CommonMapper());

    private List<CreateOrderDto> createOrderDtos;
    private List<Order> orders;

    private List<OrderDto> orderDtos;

    @BeforeEach
    void setUp() {
        this.createOrderDtos = Instancio
                .ofList(CreateOrderDto.class)
                .size(10)
                .generate(Select.field(CreateOrderDto.class, "regions"),
                        gen -> gen.ints().range(0, 200))
                .generate(Select.field(CreateOrderDto.class, "cost"),
                        gen -> gen.ints().min(0))
                .generate(Select.field(CreateOrderDto.class, "deliveryHours"),
                        gen -> gen.oneOf(
                                List.of("00:00-09:20", "10:00-15:23", "20:00-20:30"),
                                List.of("10:10-11:20", "19:50-23:00"),
                                List.of("10:30-20:20"),
                                List.of("12:40-15:23"),
                                List.of("08:00-14:00", "16:00-20:00"),
                                List.of("09:00-12:00", "13:00-14:00", "15:00-16:00", "17:00-18:00")
                        ))
                .create();
        this.orders = new ArrayList<>();
        for (int i = 0; i < createOrderDtos.size(); i++) {
            Order order = orderMapper.createOrderDtoToOrder(createOrderDtos.get(i));
            order.setOrderId((long) i);
            this.orders.add(order);
        }
        this.orderDtos = this.orders.stream().map(orderMapper::orderToOrderDto).collect(Collectors.toList());
    }

    @Test
    void shouldCreateOrders() {
        when(orderRepository.saveAll(anyList())).thenReturn(orders);

        List<OrderDto> orderDtoList = orderService.creatOrders(createOrderDtos);

        assertThat(orderDtoList).isEqualTo(this.orderDtos);
    }
}