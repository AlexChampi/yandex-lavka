package ru.yandex.yandexlavka.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.yandexlavka.dto.response.BadRequestResponse;
import ru.yandex.yandexlavka.dto.response.NotFoundResponse;

import java.util.NoSuchElementException;

@ControllerAdvice
public class YandexLavkaExceptionHandler {
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ValidationException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class,
            CompleteOrderException.class,
            NoSuchElementException.class})
    public ResponseEntity<BadRequestResponse> handelBadRequest() {
        return new ResponseEntity<>(new BadRequestResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<NotFoundResponse> handelNotFound(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new NotFoundResponse(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RequestLimitPerEndpointException.class})
    public ResponseEntity<?> handelTooManyRequest() {
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }
}
