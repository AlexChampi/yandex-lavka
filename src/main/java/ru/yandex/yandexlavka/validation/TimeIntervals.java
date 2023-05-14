package ru.yandex.yandexlavka.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.yandexlavka.validation.validator.TimeIntervalsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TimeIntervalsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeIntervals {
    String message() default "Time intervals validation failed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
