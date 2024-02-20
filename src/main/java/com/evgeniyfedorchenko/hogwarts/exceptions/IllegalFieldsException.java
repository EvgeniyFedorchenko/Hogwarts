package com.evgeniyfedorchenko.hogwarts.exceptions;

public abstract class IllegalFieldsException extends RuntimeException {

    private final String paramName;
    private final String invalidValue;

    public IllegalFieldsException(String message, String paramName, String invalidValue) {
        super(message);
        this.paramName = paramName;
        this.invalidValue = invalidValue;
    }

    public String getParamName() {
        return paramName;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public abstract String getMessage();
}
