package ru.yandex.yandexlavka.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.exception.TimeIntervalException;
import ru.yandex.yandexlavka.model.TimeInterval;
import ru.yandex.yandexlavka.validation.TimeIntervals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class TimeIntervalsValidator implements ConstraintValidator<TimeIntervals, List<String>> {
    private static final Pattern timeIntervalPattern = Pattern.compile("[0-2][0-9]:[0-5][0-9]-[0-2][0-9]:[0-5][0-9]");

    @Override
    public void initialize(TimeIntervals constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        final List<TimeInterval> intervals = new ArrayList<>();
        for (String interval : value) {
            if (interval.length() != 11 || !timeIntervalPattern.matcher(interval).matches()) {
                context.buildConstraintViolationWithTemplate(
                                "Wrong time interval format. Expected hh:mm-hh:mm, but get: " + interval)
                        .addConstraintViolation();
                return false;
            }
            try {
                intervals.add(new TimeInterval(interval));
            } catch (TimeIntervalException e) {
                context.buildConstraintViolationWithTemplate(e.getMessage())
                        .addConstraintViolation();
                return false;
            }
        }
        Collections.sort(intervals);
        for (int i = 1; i < intervals.size(); i++) {
            if (intervals.get(i - 1).getEnd().isAfter(intervals.get(i).getStart())) {
                context.buildConstraintViolationWithTemplate(
                                "Time intervals: " +
                                        intervals.get(i - 1) + " " + intervals.get(i) +
                                        " intersect.")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
