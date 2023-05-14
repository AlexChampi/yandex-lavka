package ru.yandex.yandexlavka.mapping;


import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.dto.CourierDto;
import ru.yandex.yandexlavka.dto.CreateCourierDto;
import ru.yandex.yandexlavka.model.Courier;
import ru.yandex.yandexlavka.model.CourierType;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourierMapper {
    private final CommonMapper commonMapper;

    public CourierMapper(CommonMapper commonMapper) {this.commonMapper = commonMapper;}


    public Courier createCourierDtoToCourier(CreateCourierDto dto) {
        if (dto == null) {
            return null;
        }

        Courier courier = new Courier();

        courier.setWorkingHours(commonMapper.mapStringsToTimeIntervals(dto.workingHours()));
        if (dto.courierType() != null) {
            courier.setCourierType(Enum.valueOf(CourierType.class, dto.courierType()));
        }
        List<Integer> list1 = dto.regions();
        if (list1 != null) {
            courier.setRegions(new ArrayList<>(list1));
        }

        return courier;
    }

    public CourierDto courierToCourierDto(Courier courier) {
        if (courier == null) {
            return null;
        }

        List<String> workingHours = commonMapper.mapTimeIntervalsToStrings(courier.getWorkingHours());
        Long courierId = courier.getCourierId();
        CourierType courierType = courier.getCourierType();
        List<Integer> list1 = courier.getRegions();
        List<Integer> regions = null;
        if (list1 != null) {
            regions = new ArrayList<>(list1);
        }

        return new CourierDto(courierId, courierType, regions, workingHours);
    }
}

