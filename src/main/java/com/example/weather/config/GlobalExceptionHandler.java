package com.example.weather.config;

import com.example.weather.model.ApiErrorMessage;
import com.example.weather.model.RateLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorMessage> handleException(Exception e) {
        return new ResponseEntity<>(new ApiErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RateLimitException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<ApiErrorMessage> rateLimitException(Exception e) {
        return new ResponseEntity<>(new RateLimitException(e.getMessage()).toApiErrorMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }
}
