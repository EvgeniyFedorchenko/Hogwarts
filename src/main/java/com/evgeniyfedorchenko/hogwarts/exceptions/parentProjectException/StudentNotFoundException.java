package com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException;

public class StudentNotFoundException extends ParentProjectException {

    public StudentNotFoundException(String message, String invalidObjectName, String invalidValue) {
        super(message, invalidObjectName, invalidValue);
    }

    @Override
    public String getMessage() {
        return "Student with %s = %s isn't found".formatted(getInvalidObjectName(), getInvalidValue());
    }
}
