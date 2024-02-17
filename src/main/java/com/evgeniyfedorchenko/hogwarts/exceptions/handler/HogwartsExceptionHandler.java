package com.evgeniyfedorchenko.hogwarts.exceptions.handler;

import com.evgeniyfedorchenko.hogwarts.exceptions.InvalidFieldsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HogwartsExceptionHandler {

    @ExceptionHandler(InvalidFieldsException.class)
    public ResponseEntity<String> handleInvalidFieldsException(InvalidFieldsException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
