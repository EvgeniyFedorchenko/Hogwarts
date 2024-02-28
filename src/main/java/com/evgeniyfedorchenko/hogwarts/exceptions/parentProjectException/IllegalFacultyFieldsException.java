package com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException;

public class IllegalFacultyFieldsException extends ParentProjectException {

    public IllegalFacultyFieldsException(String message, String paramName, String invalidValue) {
        super(message, paramName, invalidValue);
    }

    @Override
    public String getMessage() {
        return "Value %s of parameter %s of faculty is invalid".formatted(getInvalidValue(), getInvalidObjectName());
    }
}
