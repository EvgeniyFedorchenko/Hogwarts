package com.evgeniyfedorchenko.hogwarts.exceptions;

public class IllegalStudentFieldsException extends IllegalFieldsException {

    public IllegalStudentFieldsException(String message, String paramName, String invalidValue) {
        super(message, paramName, invalidValue);
    }

    @Override
    public String getMessage() {
        return "Value %s of parameter %s of student is invalid".formatted(getInvalidValue(), getParamName());
    }
}
