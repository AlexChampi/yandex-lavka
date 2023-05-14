package ru.yandex.yandexlavka.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.yandexlavka.exception.TimeIntervalException;

import java.time.LocalTime;
import java.util.List;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class TimeInterval implements Comparable<TimeInterval> {
    private LocalTime start;
    @Column(name = "`end`")
    private LocalTime end;

    public boolean isIntersect(TimeInterval timeInterval) {
        return this.start.isBefore(timeInterval.start) && this.end.isAfter(timeInterval.start) ||
                this.start.isBefore(timeInterval.end) && this.end.isAfter(timeInterval.end);
    }

    public static boolean isIntervalsIntersect(List<TimeInterval> first, List<TimeInterval> second) {
        for (TimeInterval f : first) {
            for (TimeInterval s : second) {
                if (f.isIntersect(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public TimeInterval(LocalTime start, LocalTime end) {
        if (start.isAfter(end)) {
            throw new TimeIntervalException("The beginning of the time interval should be earlier than the end. " +
                    "Input value: start=" + start + ", end=" + end);
        }
        this.start = start;
        this.end = end;
    }

    public TimeInterval(String workingHours) {
        this(LocalTime.parse(workingHours.substring(0, 5)), LocalTime.parse(workingHours.substring(6)));
    }

    @Override
    public int compareTo(TimeInterval timeInterval) {
        return this.start.compareTo(timeInterval.start);
    }

    public boolean containsStartOf(TimeInterval timeInterval) {
        return !this.start.isAfter(timeInterval.start) && !this.end.isBefore(timeInterval.start);
    }
}
