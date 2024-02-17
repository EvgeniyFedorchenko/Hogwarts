package com.evgeniyfedorchenko.hogwarts.exceptions.handler;

import com.evgeniyfedorchenko.hogwarts.exceptions.FacultyAlreadyExistsException;
import com.evgeniyfedorchenko.hogwarts.exceptions.FacultyNotFoundException;
import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalFieldsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HogwartsExceptionHandler {


    /* Даже поймав FacultyNotFound мы все равно возвращаем код 400 т.к. это исключение может возникнуть только
       при валидации студента, а это значит, что параметры студента были переданы невалидные - BAD_REQUEST */

    @ExceptionHandler({IllegalFieldsException.class, FacultyNotFoundException.class})
    public ResponseEntity<String> handleInvalidFieldsException(IllegalFieldsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(FacultyAlreadyExistsException.class)
    public ResponseEntity<String> handleFacultyAlreadyExistsException(FacultyAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(e.getMessage());
    }
}
