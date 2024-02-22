package com.evgeniyfedorchenko.hogwarts.exceptions;

public class FacultyNotFoundException extends ParentProjectException {

    public FacultyNotFoundException(String message, String invalidObjectName, String invalidValue) {
        super(message, invalidObjectName, invalidValue);
    }

    @Override
    public String getMessage() {
        return "Faculty with %s = %s isn't found".formatted(getInvalidObjectName(), getInvalidValue());
    }
}
