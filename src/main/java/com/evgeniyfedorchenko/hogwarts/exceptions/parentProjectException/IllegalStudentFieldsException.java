package com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException;

public class IllegalStudentFieldsException extends ParentProjectException {

    public IllegalStudentFieldsException(String message, String paramName, String invalidValue) {
        super(message, paramName, invalidValue);
    }

    @Override
    public String getMessage() {
        return "Value %s of parameter %s of student is invalid".formatted(getInvalidValue(), getInvalidObjectName());
    }
}
