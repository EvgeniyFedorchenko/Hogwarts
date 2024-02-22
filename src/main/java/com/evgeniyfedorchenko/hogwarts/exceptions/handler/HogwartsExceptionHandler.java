package com.evgeniyfedorchenko.hogwarts.exceptions.handler;

import com.evgeniyfedorchenko.hogwarts.exceptions.FacultyAlreadyExistsException;
import com.evgeniyfedorchenko.hogwarts.exceptions.ParentProjectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HogwartsExceptionHandler {


    /* Даже поймав FacultyNotFound мы все равно возвращаем статус BAD_REQUEST т.к. это исключение может возникнуть
       только при валидации студента, а это значит, что параметры студента были переданы невалидные - BAD_REQUEST */
    @ExceptionHandler(ParentProjectException.class)
    public ResponseEntity<String> handleInvalidFieldsException(ParentProjectException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(FacultyAlreadyExistsException.class)
    public ResponseEntity<String> handleFacultyAlreadyExistsException(FacultyAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
