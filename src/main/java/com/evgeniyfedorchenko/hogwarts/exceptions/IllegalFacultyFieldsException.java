package com.evgeniyfedorchenko.hogwarts.exceptions;

public class IllegalFacultyFieldsException extends IllegalFieldsException {

    public IllegalFacultyFieldsException(String message, String paramName, String invalidValue) {
        super(message, paramName, invalidValue);
    }

    @Override
    public String getMessage() {
        return "Value %s of parameter %s of faculty is invalid".formatted(getInvalidValue(), getParamName());
    }
}
