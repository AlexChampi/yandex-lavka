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
import ru.yandex.yandexlavka.mapping.CourierMapper;
import ru.yandex.yandexlavka.model.Courier;
import ru.yandex.yandexlavka.model.CourierType;
import ru.yandex.yandexlavka.model.TimeInterval;
import ru.yandex.yandexlavka.repository.CourierRepository;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourierControllerIT extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private CourierMapper courierMapper;

    private final String AUTO = CourierType.AUTO.name();
    private final String FOOT = CourierType.FOOT.name();
    private final String BIKE = CourierType.BIKE.name();
    private static final String COURIER_PREFIX = "/api/v1/couriers";
    private final ObjectWriter ow;

    public CourierControllerIT() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void shouldReturnCouriers() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(COURIER_PREFIX)
                        .param("limit", "3"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenReturnCouriers_ShouldBadRequestReturn() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(COURIER_PREFIX)
                        .param("limit", "-3")
                        .param("offset", "-31231231231"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindCourierById() throws Exception {
        Courier save = courierRepository.save(new Courier(null, CourierType.AUTO, List.of(2), List.of(new TimeInterval("12:30-13:00"))));
        this.mockMvc.perform(MockMvcRequestBuilders.get(COURIER_PREFIX + "/{courierId}", save.getCourierId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenFindCourierById_ShouldNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(COURIER_PREFIX + "/{courierId}", 302))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}