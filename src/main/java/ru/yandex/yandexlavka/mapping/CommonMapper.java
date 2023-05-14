package ru.yandex.yandexlavka.mapping;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.model.TimeInterval;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommonMapper {
    public List<TimeInterval> mapStringsToTimeIntervals(List<String> value) {
        return value.stream().map(TimeInterval::new).collect(Collectors.toList());
    }

    public List<String> mapTimeIntervalsToStrings(List<TimeInterval> intervals) {
        return intervals
                .stream()
                .map((interval) -> interval.getStart() + "-" + interval.getEnd())
                .collect(Collectors.toList());
    }
}
