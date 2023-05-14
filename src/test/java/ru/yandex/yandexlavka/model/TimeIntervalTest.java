package ru.yandex.yandexlavka.model;

import org.junit.jupiter.api.Test;
import ru.yandex.yandexlavka.exception.TimeIntervalException;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeIntervalTest {
    @Test
    public void shouldOverlap() {
        List<String> t1String = List.of(
                "10:00-11:00",
                "11:00-11:30"
        );
        List<String> t2String = List.of(
                "07:00-09:00",
                "09:00-10:01"
        );
        TimeInterval timeInterval = new TimeInterval("09:00-10:02");
        TimeInterval timeInterval1 = new TimeInterval("10:00-11:00");

        List<TimeInterval> t1 = t1String.stream().map(TimeInterval::new).collect(Collectors.toList());
        List<TimeInterval> t2 = t2String.stream().map(TimeInterval::new).collect(Collectors.toList());

        assertTrue(() -> TimeInterval.isIntervalsIntersect(t1, t2));
        assertTrue(timeInterval.isIntersect(timeInterval1));
    }


    @Test
    public void shouldThrowTimeIntervalException_whenConstruct() {
        assertThrows(TimeIntervalException.class, () -> new TimeInterval("10:00-09:00"));
        assertThrows(TimeIntervalException.class, () -> new TimeInterval("23:59-00:00"));
        assertThrows(TimeIntervalException.class, () -> new TimeInterval("10:00-09:59"));
    }

    @Test
    public void shouldThrowDateTimeParseException_whenConstruct() {
        assertThrows(DateTimeParseException.class, () -> new TimeInterval("9:00-12:00"));
        assertThrows(DateTimeParseException.class, () -> new TimeInterval("09:00-12"));
        assertThrows(DateTimeParseException.class, () -> new TimeInterval("09:00-12:60"));
        assertThrows(DateTimeParseException.class, () -> new TimeInterval("09:00-42:00"));
    }
}