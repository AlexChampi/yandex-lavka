package ru.yandex.yandexlavka.exception;

public class TimeIntervalException extends RuntimeException {
    public TimeIntervalException() {
    }

    public TimeIntervalException(String message) {
        super(message);
    }
}
