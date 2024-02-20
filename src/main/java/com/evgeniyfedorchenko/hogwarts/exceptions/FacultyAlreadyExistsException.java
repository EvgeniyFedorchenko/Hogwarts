package com.evgeniyfedorchenko.hogwarts.exceptions;

public class FacultyAlreadyExistsException extends RuntimeException {
    public FacultyAlreadyExistsException(String message) {
        super(message);
    }
}
