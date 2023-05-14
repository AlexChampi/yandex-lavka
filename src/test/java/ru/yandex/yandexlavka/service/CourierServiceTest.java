package ru.yandex.yandexlavka.service;

import org.instancio.Instancio;
import org.instancio.generators.Generators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.yandexlavka.dto.CourierDto;
import ru.yandex.yandexlavka.dto.CreateCourierDto;
import ru.yandex.yandexlavka.dto.response.GetCourierMetaInfoResponse;
import ru.yandex.yandexlavka.mapping.CommonMapper;
import ru.yandex.yandexlavka.mapping.CourierMapper;
import ru.yandex.yandexlavka.model.AssignedOrder;
import ru.yandex.yandexlavka.model.Courier;
import ru.yandex.yandexlavka.model.CourierType;
import ru.yandex.yandexlavka.model.Order;
import ru.yandex.yandexlavka.repository.CourierRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class})
class CourierServiceTest {
    @Mock
    private CourierRepository courierRepository;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private CourierService courierService;

    @Spy
    private final CourierMapper mapper = new CourierMapper(new CommonMapper());
    private List<CreateCourierDto> createCourierDtos;
    private List<Courier> couriers;
    private List<CourierDto> courierDtos;


    @BeforeEach
    void setUp() {
        this.createCourierDtos = Instancio
                .ofList(CreateCourierDto.class)
                .size(10)
                .generate(field(CreateCourierDto.class, "courierType"),
                        gen -> gen.oneOf(Arrays.stream(CourierType.values())
                                .map(CourierType::getValue)
                                .collect(Collectors.toList())))
                .generate(field(CreateCourierDto.class, "workingHours"),
                        gen -> gen.oneOf(
                                List.of("00:00-09:20", "10:00-15:23", "20:00-20:30"),
                                List.of("10:10-11:20", "19:50-23:00"),
                                List.of("10:30-20:20"),
                                List.of("12:40-15:23"),
                                List.of("08:00-14:00", "16:00-20:00"),
                                List.of("09:00-12:00", "13:00-14:00", "15:00-16:00", "17:00-18:00")
                        ))
                .create();
        this.couriers = new ArrayList<>();
        for (int i = 0; i < createCourierDtos.size(); i++) {
            Courier courierDtoToCourier = mapper.createCourierDtoToCourier(createCourierDtos.get(i));
            courierDtoToCourier.setCourierId((long) i);
            this.couriers.add(courierDtoToCourier);
        }
        this.courierDtos = this.couriers.stream().map(mapper::courierToCourierDto).collect(Collectors.toList());
    }

    @Test
    void shouldCreateCouriers() {
        when(courierRepository.saveAll(anyList())).thenReturn(couriers);

        List<CourierDto> courier = courierService.createCourier(createCourierDtos);

        assertThat(courier).isEqualTo(this.courierDtos);
    }


    @RepeatedTest(100)
    void shouldFindCourierById() {
        int id = ThreadLocalRandom.current().nextInt(couriers.size());
        when(courierRepository.findById((long) id)).thenReturn(Optional.ofNullable(couriers.get(id)));

        CourierDto courierById = courierService.findCourierById((long) id);

        assertThat(courierById).isEqualTo(courierDtos.get(id));
    }

    @RepeatedTest(100)
    void shouldCalculateCourierRating() {
        int size = ThreadLocalRandom.current().nextInt(2, 50);
        long courierId = ThreadLocalRandom.current().nextInt(couriers.size());
        int daysCounter = ThreadLocalRandom.current().nextInt(1, 800);
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = start.plusDays(daysCounter);
        Courier courier = couriers.get((int) courierId);
        List<Order> orders = Instancio.ofList(Order.class)
                .size(size)
                .generate(field(Order::getOrderId), Generators::longs)
                .generate(field(Order::getCost), gen -> gen.ints().min(1))
                .generate(field(Order::getCompletedTime), gen -> gen.temporal().localDateTime())
                .create();
        orders.forEach(o -> {
            AssignedOrder assignedOrder = new AssignedOrder();
            assignedOrder.addOrder(o);
            o.setAssignedOrder(assignedOrder);
        });
        when(orderService.getCompleteOrdersByCourierBetweenDate(eq(courierId), any(), any())).thenReturn(orders);
        when(courierRepository.findById(courierId)).thenReturn(Optional.ofNullable(couriers.get((int) courierId)));

        GetCourierMetaInfoResponse courierMetaInfo = courierService
                .getCourierMetaInfo(courierId,
                        start,
                        end);
        int rating = Math.round((float) size / (24 * daysCounter) * courier.getCourierType().getRatingCoefficient());
        int earnings = Math.round(orders.stream().mapToInt(Order::getCost).sum() *
                courier.getCourierType().getIncomeCoefficient() *
                courier.getCourierType().getFirstOrderCostCoefficient());

        assertEquals(rating, courierMetaInfo.rating());
        assertEquals(earnings, courierMetaInfo.earnings());
    }
}