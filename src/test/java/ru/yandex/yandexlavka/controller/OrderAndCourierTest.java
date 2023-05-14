package ru.yandex.yandexlavka.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.yandexlavka.dto.CreateCourierDto;
import ru.yandex.yandexlavka.dto.CreateOrderDto;
import ru.yandex.yandexlavka.dto.request.CreateCourierRequest;
import ru.yandex.yandexlavka.dto.request.CreateOrderRequest;
import ru.yandex.yandexlavka.model.CourierType;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderAndCourierTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private final String AUTO = CourierType.AUTO.name();
    private final String FOOT = CourierType.FOOT.name();
    private final String BIKE = CourierType.BIKE.name();
    private static final String COURIER_PREFIX = "/api/v1/couriers";
    private static final String ORDERS_PREFIX = "/api/v1/orders";
    private final ObjectWriter ow;

    public OrderAndCourierTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    private CreateCourierRequest createCourierRequest() {
        return new CreateCourierRequest(
                List.of(new CreateCourierDto(AUTO, List.of(1, 2, 3), List.of("08:00-20:00")),
                        new CreateCourierDto(AUTO, List.of(2, 3, 4), List.of("10:05-12:55", "13:00-15:00", "18:10-20:50")),
                        new CreateCourierDto(FOOT, List.of(1, 4), List.of("10:19-12:39", "13:00-15:40")),
                        new CreateCourierDto(FOOT, List.of(3, 4), List.of("15:10-23:45")),
                        new CreateCourierDto(BIKE, List.of(3, 4), List.of("00:10-05:30", "18:00-20:00")),
                        new CreateCourierDto(BIKE, List.of(4, 5), List.of("10:00-12:00", "18:00-20:00")),
                        new CreateCourierDto(BIKE, List.of(1, 4, 8), List.of("10:00-12:00"))));
    }

    private CreateOrderRequest createOrderRequest() {
        return new CreateOrderRequest(
                List.of(new CreateOrderDto(10f, 2, List.of("00:00-04:00", "20:00-21:00"), 10),
                        new CreateOrderDto(5f, 1, List.of("08:45-14:00", "18:00-19:00"), 2),
                        new CreateOrderDto(20f, 3, List.of("10:40-21:00"), 3),
                        new CreateOrderDto(4.6f, 2, List.of("00:00-04:00", "20:00-21:00"), 5))
        );
    }

    @Test
    void shouldSaveCouriers() throws Exception {
        CreateCourierRequest createCourierRequest = createCourierRequest();
        this.mockMvc.perform(MockMvcRequestBuilders.post(COURIER_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(createCourierRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void shouldSaveOrders() throws Exception {
        CreateOrderRequest createOrderRequest = createOrderRequest();
        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(createOrderRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldAssignOrdersAndReturnOrdersByCourierId() throws Exception {
        CreateCourierRequest createCourierRequest = createCourierRequest();
        CreateOrderRequest createOrderRequest = createOrderRequest();
        this.mockMvc.perform(MockMvcRequestBuilders.post(COURIER_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(createCourierRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(createOrderRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        LocalDate now = LocalDate.now();
        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_PREFIX + "/assign")
                        .param("date", now.toString()))
                .andDo(print())
                .andExpect(status().isCreated());
        this.mockMvc.perform(MockMvcRequestBuilders.get(COURIER_PREFIX + "/assignments")
                        .param("date", now.toString()))
                .andDo(print())
                .andExpect(status().isOk());
        this.mockMvc.perform(MockMvcRequestBuilders.get(COURIER_PREFIX + "/assignments")
                        .param("date", now.toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailToSaveOrder_NegativeWeight() throws Exception {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                List.of(new CreateOrderDto(-1f, 2, List.of("12:00-12:30"), 4))
        );
        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(createOrderRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToSaveOrder_TimeIntervalIntersect() throws Exception {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                List.of(new CreateOrderDto(2f, 2, List.of("12:00-12:30", "10:00-12:10"), 4))
        );
        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(createOrderRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToSaveCouriers_NegativeWeight() throws Exception {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                List.of(new CreateOrderDto(-1f, 2, List.of("12:00-12:30"), 4))
        );
        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(createOrderRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToSaveCouriers_InvalidCourierType() throws Exception {
        CreateCourierRequest createCourierRequest = new CreateCourierRequest(
                List.of(new CreateCourierDto("COURIER", List.of(1), List.of("12:00-12:30", "14:00-14:10")))
        );
        this.mockMvc.perform(MockMvcRequestBuilders.post(COURIER_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(createCourierRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToSaveCouriers_TimeIntervalIntersect() throws Exception {
        CreateCourierRequest createCourierRequest = new CreateCourierRequest(
                List.of(new CreateCourierDto("AUTO", List.of(1), List.of("12:00-12:30", "11:00-14:10")))
        );
        this.mockMvc.perform(MockMvcRequestBuilders.post(COURIER_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(createCourierRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
