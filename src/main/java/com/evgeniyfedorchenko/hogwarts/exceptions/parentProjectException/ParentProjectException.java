package com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException;

public abstract class ParentProjectException extends RuntimeException {

    private final String invalidObjectName;
    private final String invalidValue;

    public ParentProjectException(String message, String invalidObjectName, String invalidValue) {
        super(message);
        this.invalidObjectName = invalidObjectName;
        this.invalidValue = invalidValue;
    }

    public String getInvalidObjectName() {
        return invalidObjectName;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public abstract String getMessage();
}
