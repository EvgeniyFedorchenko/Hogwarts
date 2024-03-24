package com.evgeniyfedorchenko.hogwarts.exceptions.handler;

import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarProcessingException;
import com.evgeniyfedorchenko.hogwarts.exceptions.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.IntStream;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AvatarProcessingException.class)
    public ResponseEntity<String> handleAvatarProcessingException(AvatarProcessingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)     // Нарушение целостности БД (нарушение констрейнтов)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)    // Невалидные параметры inputDto
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = "Validation error in parameter " + (e.getParameter().getParameterIndex() + 1);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)    // Невалидные параметры пути в контроллере
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder sb = new StringBuilder("Validation errors:\n");
        List<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations().stream().toList();
        IntStream.range(0, constraintViolations.size())
                .forEach(i -> sb.append(i + 1)
                        .append(". ")
                        .append(constraintViolations.get(i).getMessage())
                        .append("\n")
                );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sb.toString());
    }
}
