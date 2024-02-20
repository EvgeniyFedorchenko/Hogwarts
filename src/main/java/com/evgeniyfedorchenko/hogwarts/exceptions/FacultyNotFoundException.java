package com.evgeniyfedorchenko.hogwarts.exceptions;

public class FacultyNotFoundException extends RuntimeException {

    private final Long id;

    public FacultyNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Cannot get such a student because the faculty with id %d does not exist".formatted(id);
    }
}
