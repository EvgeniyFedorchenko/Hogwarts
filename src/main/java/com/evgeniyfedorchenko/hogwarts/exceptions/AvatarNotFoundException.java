package com.evgeniyfedorchenko.hogwarts.exceptions;

public class AvatarNotFoundException extends ParentProjectException {

    public AvatarNotFoundException(String message, String invalidObjectName, String invalidValue) {
        super(message, invalidObjectName, invalidValue);
    }

    @Override
    public String getMessage() {
        return "%s with ID %s isn't found".formatted(getInvalidObjectName(), getInvalidValue());
    }
}
