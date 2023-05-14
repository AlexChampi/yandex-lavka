package ru.yandex.yandexlavka.ratelimiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {
    int tokenPerPeriod() default 10;

    int timeLimit() default 1;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
