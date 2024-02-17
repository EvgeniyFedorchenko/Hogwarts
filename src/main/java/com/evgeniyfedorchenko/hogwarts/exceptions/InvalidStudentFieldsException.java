package com.evgeniyfedorchenko.hogwarts.exceptions;

public class InvalidStudentFieldsException extends InvalidFieldsException {

    public InvalidStudentFieldsException(String message, String paramName, String invalidValue) {
        super(message, paramName, invalidValue);
    }

    @Override
    public String getMessage() {
        return "Value %s of parameter %s of Student is invalid".formatted(getInvalidValue(), getParamName());
    }
}
