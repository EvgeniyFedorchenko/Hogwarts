package com.evgeniyfedorchenko.hogwarts.exceptions;

public class InvalidFacultyFieldsException extends InvalidFieldsException {

    public InvalidFacultyFieldsException(String message, String paramName, String invalidValue) {
        super(message, paramName, invalidValue);
    }

    @Override
    public String getMessage() {
        return "Value %s of parameter %s of Faculty is invalid".formatted(getInvalidValue(), getParamName());
    }
}
