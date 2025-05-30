package com.example.WheaterApp.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalStateException.class, RuntimeException.class})
    public ResponseEntity<String> handleAppExceptions(RuntimeException ex) {
        System.err.println("ERROR: GlobalExceptionHandler caught: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}