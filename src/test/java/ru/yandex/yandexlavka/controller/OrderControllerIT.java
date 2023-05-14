package ru.yandex.yandexlavka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.yandexlavka.mapping.OrderMapper;
import ru.yandex.yandexlavka.model.Order;
import ru.yandex.yandexlavka.model.TimeInterval;
import ru.yandex.yandexlavka.repository.OrderRepository;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIT extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;
    private static final String ORDER_PREFIX = "/api/v1/orders";
    private final ObjectWriter ow;

    public OrderControllerIT() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void shouldReturnOrders() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER_PREFIX)
                        .param("limit", "3"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenReturnOrders_ShouldBadRequestReturn() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER_PREFIX)
                        .param("limit", "-3")
                        .param("offset", "-31231231231"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindOrderById() throws Exception {
        Order order = orderRepository.save(new Order(null, 18f, 3, List.of(new TimeInterval("12:30-13:00")), 2, null, null));
        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER_PREFIX + "/{orderId}", order.getOrderId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenFindOrderById_ShouldNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER_PREFIX + "/{order}", 302))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}